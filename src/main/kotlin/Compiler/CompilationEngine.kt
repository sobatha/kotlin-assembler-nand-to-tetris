package Compiler

import java.io.File

val tokenTypeToXmlTag: Map<String, String> = mapOf(
    "KEYWORD" to "keyword", "SYMBOL" to "symbol", "IDENTIFIER" to "identifier",
    "INT_CONST" to "integerConstant", "STRING_CONST" to "stringConstant")

val KEYWORD_CONST = listOf("true", "false", "null", "this")
val op = listOf("+", "-", "*", "/", "&", "|", ">", "<", "=")
val escape: Map<String, String> = mapOf("<" to "&lt;", ">" to "&gt;","\"" to "&quote;","&" to "&amp;",)

class CompilationEngine(tokenizer: JackTokenizer, outputPath: String) {
    val outputPath = outputPath
    val tokenizer = tokenizer

    fun compile() {
        while (tokenizer.hasMoreToken()) {
            tokenizer.advance()
            compileClass()
        }
    }
    fun compileClass() {
        writeFile("<class>")
        process("class")
        processIdentifier()
        process("{")

        while (tokenizer.currentToken == "static" || tokenizer.currentToken == "field") {
            compileClassVarDec()
        }

        while (tokenizer.currentToken == "constructor" || tokenizer.currentToken == "function"
            || tokenizer.currentToken == "method") {
            compileSubroutine()
        }

        writeFile(compileTerminalTokens(tokenizer.currentToken, tokenizer.currentTokenType))
        writeFile("</class>")
    }

    fun compileClassVarDec() {
        writeFile("<classVarDec>")
        when (tokenizer.currentToken) {
            "static" -> process("static")
            "field" -> process("field")
        }
        process(tokenizer.currentToken)
        processIdentifier()

        while (tokenizer.currentToken == ",") {
            process(",")
            processIdentifier()
        }
        process(";")
        writeFile("</classVarDec>")
    }

    fun compileSubroutine() {
        writeFile("<subroutineDec>")
        when (tokenizer.currentToken) {
            "constructor" -> process("constructor")
            "function" -> process("function")
            "method" -> process("method")
        }
        process(tokenizer.currentToken)
        processIdentifier()

        process("(")
        compileParameterList()
        process(")")
        compileSubroutineBody()
        writeFile("</subroutineDec>")
    }

    fun compileParameterList() {
        writeFile("<parameterList>")
        while (tokenizer.currentToken == "int" ||
            tokenizer.currentToken == "char" ||
            tokenizer.currentToken == "boolean" ||
            tokenizer.currentTokenType == "IDENTIFIER"
        ) {
            process(tokenizer.currentToken)
            processIdentifier()

            while (tokenizer.currentToken == ",") {
                process(",")
                process(tokenizer.currentToken)
                processIdentifier()
            }
        }
        writeFile("</parameterList>")
    }

    fun compileSubroutineBody() {
        writeFile("<subroutineBody>")
        process("{")
        while (tokenizer.currentToken == "var") {
            compileVarDec()
        }
        compileStatements()
        process("}")
        writeFile("</subroutineBody>")
    }

    fun compileVarDec() {
        writeFile("<varDec>")
        if (tokenizer.currentToken == "var") {
            process("var")
            process(tokenizer.currentToken)
            processIdentifier()

            while (tokenizer.currentToken == ",") {
                process(",")
                processIdentifier()
            }
            process(";")
        }
        writeFile("</varDec>")
    }

    fun compileStatements() {
        writeFile("<statements>")
        while (
            tokenizer.currentToken == "let" ||
            tokenizer.currentToken == "if" ||
            tokenizer.currentToken == "while" ||
            tokenizer.currentToken == "do" ||
            tokenizer.currentToken == "return"
        ) {
            when (tokenizer.currentToken) {
                "let" -> compileLet()
                "if" -> compileIf()
                "while" -> compileWhile()
                "do" -> compileDo()
                "return" -> compileReturn()
            }
        }
        writeFile("</statements>")
    }

    fun compileLet() {
        writeFile("<letStatement>")
        process("let")
        processIdentifier()
        if (tokenizer.currentToken == "[") {
            process("[")
            compileExpression()
            process("]")
        }
        process("=")
        compileExpression()
        process(";")
        writeFile("</letStatement>")
    }

    fun compileIf() {
        writeFile("<ifStatement>")
        process("if")
        process("(")
        compileExpression()
        process(")")
        process("{")
        compileStatements()
        process("}")
        if (tokenizer.currentToken == "else") {
            process("else")
            process("{")
            compileStatements()
            process("}")
        }
        writeFile("</ifStatement>")
    }

    fun compileWhile() {
        writeFile("<whileStatement>")
        process("while")
        process("(")
        compileExpression()
        process(")")
        process("{")
        compileStatements()
        process("}")
        writeFile("</whileStatement>")
    }

    fun compileDo() {
        writeFile("<doStatement>")
        process("do")
        processIdentifier()
        if (tokenizer.currentToken == "(" ) {
            process("(")
            compileExpressionList()
            process(")")
        } else if (tokenizer.currentToken == ".") {
            process(".")
            processIdentifier()
            process("(")
            compileExpressionList()
            process(")")
        }
        process(";")
        writeFile("</doStatement>")
    }

    fun compileReturn() {
        writeFile("<returnStatement>")
        process("return")
        if (tokenizer.currentToken != ";") {
            compileExpression()
        }
        process(";")
        writeFile("</returnStatement>")
    }

    fun compileExpression() {
        writeFile("<expression>")
        compileTerm()
        if (tokenizer.currentToken in op) {
            process(tokenizer.currentToken)
            compileTerm()
        }
        writeFile("</expression>")
    }

    fun compileTerm() {
        writeFile("<term>")
        if (tokenizer.currentTokenType == "KEYWORD" ||
            tokenizer.currentTokenType == "INT_CONST" ||
            tokenizer.currentTokenType == "STRING_CONST" ) {
            process(tokenizer.currentToken)
        } else if (tokenizer.currentTokenType == "IDENTIFIER") {
            processIdentifier()
            if (tokenizer.currentToken == "[") {
                // if array access
                process("[")
                compileExpression()
                process("]")
            } else if (tokenizer.currentToken == "(" ) {
                process("(")
                compileExpressionList()
                process(")")
            } else if (tokenizer.currentToken == ".") {
                process(".")
                processIdentifier()
                process("(")
                compileExpressionList()
                process(")")
            }
        } else if (tokenizer.currentToken == "(") {
            process("(")
            compileExpression()
            process(")")
        } else if (tokenizer.currentToken == "-" || tokenizer.currentToken == "~") {
            process(tokenizer.currentToken)
            compileTerm()
        }
        writeFile("</term>")
    }

    fun compileExpressionList() {
        writeFile("<expressionList>")
        if (tokenizer.currentToken != ")") {
            compileExpression()
            while (tokenizer.currentToken == ",") {
                process(",")
                compileExpression()
            }
        }
        writeFile("</expressionList>")
    }

    fun compileTerminalTokens(token:String, tokenType:String): String {
        return "<${tokenTypeToXmlTag[tokenType]}> $token </${tokenTypeToXmlTag[tokenType]}>"
    }


    private fun process(expectedToken: String) {
        if (tokenizer.currentToken == expectedToken) {
            tokenizer.currentToken = escape[tokenizer.currentToken] ?: tokenizer.currentToken
            writeFile(compileTerminalTokens(tokenizer.currentToken, tokenizer.currentTokenType))
        } else {
            println("Syntax error")
        }
        tokenizer.advance()
    }

    private fun processIdentifier() {
        if (tokenizer.currentTokenType == "IDENTIFIER") {
            writeFile(compileTerminalTokens(tokenizer.currentToken, tokenizer.currentTokenType))
        } else {
            println("Syntax error")
        }
        tokenizer.advance()
    }

    private fun writeFile(tag:String) {
        File(outputPath).appendText("$tag\n")
    }
}
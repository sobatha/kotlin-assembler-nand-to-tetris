package Compiler

import java.io.File

val tokenTypeToXmlTag: Map<String, String> = mapOf(
    "KEYWORD" to "keyword", "SYMBOL" to "symbol", "IDENTIFIER" to "identifier",
    "INT_CONST" to "integerConstant", "STRING_CONST" to "stringConstant")

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

        process("}")
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
        while (tokenizer.currentToken == "var") {
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
        writeFile("<doStatement>")
        writeFile("</doStatement>")
    }

    fun compileWhile() {
        writeFile("<whileStatement>")
        writeFile("</whileStatement>")
    }

    fun compileDo() {
        writeFile("<doStatement>")
        writeFile("<doStatement>")
    }

    fun compileReturn() {
        writeFile("<returnStatement>")
        writeFile("</returnStatement>")
    }

    fun compileExpression() {
        writeFile("<expression>")
        writeFile("</expression>")
    }

    fun compileTerm() {
        writeFile("<term>")
        writeFile("</term>")
    }

    fun compileExpressionList() {
        writeFile("<expressionList>")
        writeFile("</expressionList>")
    }

    fun compileTerminalTokens(token:String, tokenType:String): String {
        return "<${tokenTypeToXmlTag[tokenType]}> $token </${tokenTypeToXmlTag[tokenType]}>"
    }


    private fun process(expectedToken: String) {
        if (tokenizer.currentToken == expectedToken) {
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
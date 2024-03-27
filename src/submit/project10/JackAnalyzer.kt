
import java.io.File

fun main(args: Array<String>) {
    if (args.size < 1) {
        println("Usage: main.kt <input file name>")
        return
    }
    val path = args[0]

     if (path.endsWith(".jack")) {
         var tokenizer = JackTokenizer(path)
//         println(tokenizer.tokens)
         var compiler = CompilationEngine(tokenizer, path.removeSuffix(".jack")+".xml")
         compiler.compile()
         return
     }


     File(path).walk().forEach {
         if (it.toString().endsWith(".jack")) {
             var tokenizer = JackTokenizer(it.toString())
             var compiler = CompilationEngine(tokenizer, it.toString().removeSuffix(".jack")+".xml")
             compiler.compile()
         }
     }

 }

val TOKEN_TYPE = listOf("KEYWORD", "SYMBOL", "IDENTIFIER", "INT_CONST", "STRING_CONST")
val KEYWORDS = listOf("class", "constructor", "function", "method", "field",  "static", "var", "int", "char",
    "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return")
val SYMBOLS = listOf('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~')
class JackTokenizer(filePath: String) {

    var currentTokenType = ""
    var currentToken = ""
    var currentIndex = 0
    private var commentFlag = false
    var tokens:MutableList<String> = mutableListOf()

    init {
        val lines = File(filePath)
            .readLines()
            .filter { skipCommentsAndBlankLine(it) }
            .map { skipInlineCommentsAndWhiteSpace(it) }
        for (line in lines) decomposeToken(line, tokens)
        tokens = composeStringConst(tokens)
    }
    fun hasMoreToken():Boolean {
        return currentIndex < tokens.size
    }

    fun advance() {
        currentToken = tokens[currentIndex]
        currentTokenType =  when {
            currentToken.length == 1 && currentToken[0] in SYMBOLS  -> "SYMBOL"
            currentToken in KEYWORDS -> "KEYWORD"
            currentToken.toIntOrNull() != null -> "INT_CONST"
            currentToken.startsWith("\"") && currentToken.endsWith("\"") -> "STRING_CONST"
            else -> "IDENTIFIER"
        }
        currentIndex++
    }

    fun getTokenType():String {
        return currentTokenType
    }

    fun getKeyWord(): String {
        return currentToken
    }

    fun getSymbol(): Char {
        return currentToken[0]
    }

    fun getIdentifier(): String {
        return currentToken
    }

    fun getIntVal(): Int {
        return currentToken.toInt()
    }

    fun getStringVal(): String {
        return currentToken
    }
    private fun skipCommentsAndBlankLine(line: String): Boolean {
        var temp = line.trim()

        if (temp.startsWith("/**")) {
            commentFlag = true
        }
        if (commentFlag) {
            if (temp.endsWith("*/")) {
                commentFlag = false
            }
            temp = ""
        }
        return temp.isNotEmpty() && !temp.startsWith("//")
    }

    private fun skipInlineCommentsAndWhiteSpace(line: String): String {
        if (line.contains("//")) {
            val startIndex = line.indexOf("//")
            return line.substring(0, startIndex).trim()
        }
        return line.trim()
    }

    private fun decomposeToken(line: String, tokens:MutableList<String>) {
        val words = line.split(" ")

        for (word in words) {
//            println(word)
            var temp = word.trim()
            var token = ""
            for (c in temp) {
                if (c in SYMBOLS) {
                    if (token.isNotEmpty()) {
                        tokens.add(token)
                        token = ""
                    }
                    tokens.add(c.toString())
                }
                else token += c
            }
            if (token.isNotEmpty()) tokens.add(token)
        }
    }

    private fun composeStringConst(words: List<String>): MutableList<String> {
        val tokens:MutableList<String> = mutableListOf()
        var stringConst = ""
//        println("words; $words")
        for (word in words) {
//            println("word; $word stringConst: $stringConst")
            if (word.startsWith("\"")) {
                if (word.endsWith("\"")) {
                    if (stringConst.isEmpty()) tokens.add(word)
                    else {
                        tokens.add("$stringConst $word")
                        stringConst = ""
                    }
                } else {
                    stringConst += word
                }
            }
            else if (word.endsWith("\"")) {
                stringConst += " $word"
                tokens.add(stringConst)
                stringConst = ""
            } else {
                if (stringConst.isNotEmpty()) stringConst += " $word"
                else tokens.add(word)
            }
        }
        return tokens
    }
}

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
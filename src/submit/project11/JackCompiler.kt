
import java.io.File

fun main(args: Array<String>) {
    if (args.size < 1) {
        println("Usage: <input file name>")
        return
    }
    val path = args[0]


     if (path.endsWith(".jack")) {
         var tokenizer = JackTokenizer(path)
         var compiler = CompilationEngine(tokenizer, path.removeSuffix(".jack")+".vm")
         compiler.compile()
         return
     }


     File(path).walk().forEach {
         if (it.toString().endsWith(".jack")) {
             var tokenizer = JackTokenizer(it.toString())
             var compiler = CompilationEngine(tokenizer, it.toString().removeSuffix(".jack")+".vm")
             compiler.compile()
         }
     }

 }

val tokenTypeToXmlTag: Map<String, String> = mapOf(
    "KEYWORD" to "keyword", "SYMBOL" to "symbol", "IDENTIFIER" to "identifier",
    "INT_CONST" to "integerConstant", "STRING_CONST" to "stringConstant")

val KEYWORD_CONST = listOf("true", "false", "null", "this")
val op = listOf("+", "-", "*", "/", "&", "|", ">", "<", "=")
val escape: Map<String, String> = mapOf("<" to "&lt;", ">" to "&gt;","\"" to "&quote;","&" to "&amp;",)

class CompilationEngine(tokenizer: JackTokenizer, private val outputPath: String) {
    val tokenizer = tokenizer
    var className: String = ""
    val classSymbolTable = SymbolTable()
    var functionSymbolTable = SymbolTable()
    val vmWriter = VMwriter(outputPath)
    var labelIndex = 0

    fun compile() {
        while (tokenizer.hasMoreToken()) {
            tokenizer.advance()
            compileClass()
        }
    }
    fun compileClass() {
        process("class")
        className = processIdentifier()
        process("{")

        while (tokenizer.currentToken == "static" || tokenizer.currentToken == "field") {
            compileClassVarDec()
        }
//        classSymbolTable.printTable()

        while (tokenizer.currentToken == "constructor" || tokenizer.currentToken == "function"
            || tokenizer.currentToken == "method") {
            compileSubroutine()
        }

    }

    fun compileClassVarDec() {
        val kind = classSymbolTable.kindOf(process(tokenizer.currentToken))
        val type = process(tokenizer.currentToken)
        var identifier = processIdentifier()
        classSymbolTable.define(identifier, type, kind)

        while (tokenizer.currentToken == ",") {
            process(",")
            identifier = tokenizer.currentToken
            processIdentifier()

            classSymbolTable.define(identifier, type, kind)
        }
        process(";")
    }

    fun compileSubroutine() {
        functionSymbolTable.reset()

        val methodType = process(tokenizer.currentToken)
        val type = process(tokenizer.currentToken)
        val functionName = processIdentifier()

        if (methodType == "method") {
            // thisを引数の初めに入れ、ポインタにthisのアドレスを保管する
            functionSymbolTable.define("this", className, functionSymbolTable.kindOf("argument"))
        }

        process("(")
        val paramNum = compileParameterList()
        process(")")

        compileSubroutineBody(functionName, methodType)
    }

    fun compileParameterList(): Int {
        var paramNum = 0
        while (tokenizer.currentToken == "int" ||
            tokenizer.currentToken == "char" ||
            tokenizer.currentToken == "boolean" ||
            tokenizer.currentTokenType == "IDENTIFIER"
        ) {
            var type = process(tokenizer.currentToken)
            var identifier = processIdentifier()
            val kind = functionSymbolTable.kindOf("argument")
            functionSymbolTable.define(identifier, type, kind)
            paramNum++

            while (tokenizer.currentToken == ",") {
                process(",")
                type = process(tokenizer.currentToken)
                identifier = processIdentifier()
                functionSymbolTable.define(identifier, type, kind)
                paramNum++
            }
        }
        return paramNum
    }

    fun compileSubroutineBody(functionName:String, methodType: String) {
        process("{")
        while (tokenizer.currentToken == "var") {
            compileVarDec()
        }

        val numVars = when (methodType) {
            "method" -> functionSymbolTable.table.size + 1
            else -> functionSymbolTable.table.size
        }
        vmWriter.writeFunction(className, functionName, numVars)
        if (methodType == "method") {
            pushFromSymbolTable("this")
            vmWriter.writePop("pointer", 0)
        } else if (methodType == "constructor") {
            // thisポインタに現在のアドレスを保管する
            vmWriter.writePush("constant", classSymbolTable.table.size)
            vmWriter.writeCall("Memory.alloc", 1)
            vmWriter.writePop("pointer", 0)
        }
//        functionSymbolTable.printTable()
        compileStatements()
        process("}")
    }

    fun compileVarDec() {
        if (tokenizer.currentToken == "var") {
            val kind = classSymbolTable.kindOf(process("var"))
            val type = process(tokenizer.currentToken)
            var identifier = processIdentifier()
            functionSymbolTable.define(identifier, type, kind)

            while (tokenizer.currentToken == ",") {
                process(",")
                identifier = processIdentifier()
                functionSymbolTable.define(identifier, type, kind)
            }
            process(";")
        }
    }

    fun compileStatements() {
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
    }

    fun compileLet() {
        process("let")
        val destination = processIdentifier()
        if (tokenizer.currentToken == "[") {
            pushFromSymbolTable(destination)
            process("[")
            compileExpression()
            process("]")
            vmWriter.writeArithmetic("+")
            process("=")
            compileExpression()
            process(";")

            vmWriter.writePop("temp", 0)
            vmWriter.writePop("pointer", 1)
            vmWriter.writePush("temp", 0)
            vmWriter.writePop("that", 0)

        } else {
            process("=")
            compileExpression()
            process(";")
            popFromSymbolTable(destination)
        }
    }

    fun compileIf() {
        labelIndex++
        var labelIndex = labelIndex
        process("if")
        process("(")
        compileExpression()
        vmWriter.writeUnaryOp("~")
        process(")")
        //expression trueの場合　分岐しない　falseでelse節まで分岐
        vmWriter.writeIf("L1.$labelIndex")
        process("{")
        compileStatements()
        process("}")
        vmWriter.writeGoto("L2.$labelIndex")
        vmWriter.writeLabel("L1.$labelIndex")
        if (tokenizer.currentToken == "else") {
            process("else")
            process("{")
            compileStatements()
            process("}")
        }
        vmWriter.writeLabel("L2.$labelIndex")
    }

    fun compileWhile() {
        labelIndex++
        var labelIndex = labelIndex
        vmWriter.writeLabel("L1.$labelIndex")
        process("while")
        process("(")
        compileExpression()
        vmWriter.writeUnaryOp("~")
        vmWriter.writeIf("L2.$labelIndex")
        process(")")
        process("{")
        compileStatements()
        process("}")
        vmWriter.writeGoto("L1.$labelIndex")
        vmWriter.writeLabel("L2.$labelIndex")
    }

    fun compileDo() {
        process("do")
//        println("processing do")
        compileExpression()
        process(";")
        vmWriter.writePop("temp", 0)
    }

    fun compileReturn() {
        process("return")
        if (tokenizer.currentToken != ";") {
            compileExpression()
        } else {
            vmWriter.writePush("constant", 0)
        }
        process(";")
        vmWriter.writeReturn()
    }

    fun compileExpression() {
//        println("compile expression ${tokenizer.currentToken}")
        compileTerm()
//        println("${tokenizer.currentToken} in ope ? ${tokenizer.currentToken in op}")
        if (tokenizer.currentToken in op) {
            val operand = process(tokenizer.currentToken)
            compileTerm()
            vmWriter.writeArithmetic(operand)
        }
    }

    fun compileTerm() {
        if (tokenizer.currentTokenType == "KEYWORD") {
            when(process(tokenizer.currentToken)) {
                "this" -> vmWriter.writePush("pointer", 0)
                "false" -> vmWriter.writePush("constant", 0)
                "true" -> {
                    vmWriter.writePush("constant", 1)
                    vmWriter.writeUnaryOp("-")
                }
                "null" -> vmWriter.writePop("constant", 0)
                else -> throw IllegalStateException("expected keyword const but received not")
            }
        } else if (tokenizer.currentTokenType == "INT_CONST") {
            vmWriter.writePush("constant", process(tokenizer.currentToken).toInt())
        } else if (tokenizer.currentTokenType == "STRING_CONST") {
            vmWriter.writePush("constant", tokenizer.currentToken.length)
            vmWriter.writeCall("String.new", 1)
            for (c in tokenizer.currentToken) {
                vmWriter.writePush("constant", c.toInt())
                vmWriter.writeCall("String.appendChar", 2)
            }
            process(tokenizer.currentToken)
        } else if (tokenizer.currentTokenType == "IDENTIFIER") {
            val varName = processIdentifier()
            when (tokenizer.currentToken) {
                "[" -> {
                    // if array access
                    pushFromSymbolTable(varName)
                    process("[")
                    compileExpression()
                    process("]")
                    vmWriter.writeArithmetic("+")
                    vmWriter.writePop("pointer", 1)
                    vmWriter.writePush("that", 0)
                }
                "(" -> {
                    process("(")
                    var numParam = compileExpressionList()
                    process(")")
                    vmWriter.writePush("pointer", 0)
                    vmWriter.writeCall("$className.$varName", numParam + 1)
                }
                "." -> {
                    process(".")
                    val funcName = processIdentifier()
                    if (!varName[0].isUpperCase()) {
                        pushFromSymbolTable(varName) //methodsの呼び出し
                    }
                    process("(")
                    var numParam = compileExpressionList()
                    process(")")
                    if (varName[0].isUpperCase()) { //他クラスの呼び出し
                        vmWriter.writeCall("$varName.$funcName", numParam)
                    } else { //methodsの呼び出し
                        var classOfMethod = getTypeOfSymbol(varName)
                        vmWriter.writeCall("$classOfMethod.$funcName", numParam + 1)
                    }
                }
                else -> {
                    pushFromSymbolTable(varName)
                }
            }
        } else if (tokenizer.currentToken == "(") {
            process("(")
            compileExpression()
            process(")")
        } else if (tokenizer.currentToken == "-" || tokenizer.currentToken == "~") {
            var unaryOp = process(tokenizer.currentToken)
            compileTerm()
            vmWriter.writeUnaryOp(unaryOp)
        }
    }

    fun compileExpressionList(): Int {
        var numExpression = 0
        if (tokenizer.currentToken != ")") {
            compileExpression()
            numExpression++
            while (tokenizer.currentToken == ",") {
                process(",")
                compileExpression()
                numExpression++
            }
        }
        return numExpression
    }
    private fun process(expectedToken: String): String {
        var currentToken = tokenizer.currentToken
        if (currentToken != expectedToken) {
//            println("Syntax error expected: $expectedToken -> received: $currentToken")
        }
        tokenizer.advance()
        return currentToken
    }

    private fun processIdentifier(): String {
        var currentToken = tokenizer.currentToken
        if (tokenizer.currentTokenType != "IDENTIFIER") {
//            println("Syntax error")
        }
        tokenizer.advance()
        return currentToken
    }

    private fun writeFile(tag:String) {
        File(outputPath).appendText("$tag\n")
    }

    private fun popFromSymbolTable(symbolName: String) {
        try {
            vmWriter.writePop(functionSymbolTable.segmentOf(symbolName), functionSymbolTable.indexOf(symbolName))
        } catch(exception: Exception) {
            vmWriter.writePop(classSymbolTable.segmentOf(symbolName), classSymbolTable.indexOf(symbolName))
        }
    }

    private fun pushFromSymbolTable(symbolName: String) {
        try {
            vmWriter.writePush(functionSymbolTable.segmentOf(symbolName), functionSymbolTable.indexOf(symbolName))
        } catch(exception: Exception) {
            vmWriter.writePush(classSymbolTable.segmentOf(symbolName), classSymbolTable.indexOf(symbolName))
        }
    }
    private fun getTypeOfSymbol(symbolName: String): String {
        var type = ""
        type = try {
            functionSymbolTable.typeOf(symbolName)
        } catch (e: Exception) {
            classSymbolTable.typeOf(symbolName)
        }
        return type
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
enum class Kind {
    STATIC, FIELD, ARG, VAR, NULL
}
class SymbolTable {
    var table = mutableMapOf<String, Symbol>()
    var kindIndex = mutableMapOf<Kind, Int>(
        Kind.VAR to 0, Kind.STATIC to 0, Kind.FIELD to 0, Kind.ARG to 0
    )
    fun printTable() {
        for (symbol in table.keys) {
            println("name: $symbol, type: ${table[symbol]?.type}," +
                    " kind: ${table[symbol]?.kind}, index:  ${table[symbol]?.index}")
        }
    }

    val reset = {
        table = mutableMapOf<String, Symbol>()
        kindIndex = mutableMapOf<Kind, Int>(
            Kind.VAR to 0, Kind.STATIC to 0, Kind.FIELD to 0, Kind.ARG to 0
        )
    }
    val define = { name: String, type:String, kind:Kind ->
        val index = kindIndex[kind]!!
        kindIndex[kind] = index + 1
        val symbol = Symbol(name, type, kind, index)
        table[name] = symbol
    }
    val varCount = { it:String ->
        kindIndex.getOrDefault(kindOf(it), 0)
    }
    val kindOf = { it: String ->
        when (it) {
            "static" -> Kind.STATIC
            "field" -> Kind.FIELD
            "argument" -> Kind.ARG
            "var" -> Kind.VAR
            else -> Kind.NULL
        }
    }
    val typeOf = { name: String ->
        table.getValue(name).type
    }
    val indexOf = { name:String ->
        table.getValue(name).index
    }
    val segmentOf = { name:String ->
        when(table.getValue(name).kind) {
            Kind.ARG -> "argument"
            Kind.VAR -> "local"
            Kind.FIELD -> "this"
            Kind.STATIC -> "static"
            else -> throw IllegalStateException()
        }
    }
}

data class Symbol(val name: String, val type: String, val kind: Kind, val index:Int) {
}


class VMwriter(private val outputFilePath: String) {
    fun writePush(segment: String, index:Int) {
        write("push ${segment.lowercase()} $index")
    }
    fun writePop(segment: String, index: Int) {
        write("pop ${segment.lowercase()} $index")
    }
    fun writeArithmetic(command:String) {
        val operand = when(command) {
            "+" -> "add"
            "-" -> "sub"
            "=" -> "eq"
            "<" -> "lt"
            ">" -> "gt"
            "&" -> "and"
            "|" -> "or"
            "*" -> "call Math.multiply 2"
            "/" -> "call Math.divide 2"
            else -> "unknown op"
        }
        write("$operand")
    }
    fun writeUnaryOp(command: String) {
        val operand = when(command) {
            "-" -> "neg"
            "~" -> "not"
            else -> "unknown op"
        }
        write("$operand")
    }
    fun writeLabel(label: String) {
        write("label $label")
    }
    fun writeGoto(label: String) {
        write("goto $label")
    }
    fun writeIf(label: String) {
        write("if-goto $label")
    }
    fun writeCall(name: String, nArgs: Int) {
        write("call $name $nArgs")
    }
    fun writeFunction(className: String, funName:String, nVars: Int) {
        write("function $className.$funName $nVars")
    }
    fun writeReturn() { write("return") }
    private fun write(content:String) {
        File(outputFilePath).appendText(content + "\n")
    }
}
package Compiler

import java.io.File

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
    var labelIndexL1 = 0
    var labelIndexL2 = 0

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

        process("(")
        val paramNum = compileParameterList()
        process(")")

        vmWriter.writeFunction(className, functionName, paramNum)

        if (methodType == "method") {
            // thisを引数の初めに入れ、ポインタにthisのアドレスを保管する
            functionSymbolTable.define("this", className, functionSymbolTable.kindOf("argument"))
            pushFromSymbolTable("this")
            vmWriter.writePop("point", 0)
        } else if (methodType == "constructor") {
            // thisポインタに現在のアドレスを保管する
            vmWriter.writePush("constant", paramNum)
            vmWriter.writeCall("Memory.alloc", 1)
            vmWriter.writePop("point", 0)
        }
//        functionSymbolTable.printTable()
        compileSubroutineBody()
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

    fun compileSubroutineBody() {
        process("{")
        while (tokenizer.currentToken == "var") {
            compileVarDec()
        }
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
            TODO("array access")
            process("[")
            compileExpression()
            process("]")
        }
        process("=")
        compileExpression()
        process(";")
        popFromSymbolTable(destination)
    }

    fun compileIf() {
        process("if")
        println("processing if")
        process("(")
        compileExpression()
        vmWriter.writeUnaryOp("~")
        process(")")
        //expression trueの場合　分岐しない　falseでelse節まで分岐
        vmWriter.writeIf("L1.$labelIndexL1")
        process("{")
        compileStatements()
        process("}")
        vmWriter.writeGoto("L2.$labelIndexL2")
        vmWriter.writeLabel("L1.$labelIndexL1")
        if (tokenizer.currentToken == "else") {
            process("else")
            process("{")
            compileStatements()
            process("}")
        }
        vmWriter.writeLabel("L2.$labelIndexL2")
        labelIndexL1++
        labelIndexL2++
    }

    fun compileWhile() {
        vmWriter.writeLabel("L1.$labelIndexL1")
        process("while")
        process("(")
        compileExpression()
        vmWriter.writeUnaryOp("~")
        vmWriter.writeIf("L2.$labelIndexL2")
        process(")")
        process("{")
        compileStatements()
        process("}")
        vmWriter.writeGoto("L1.$labelIndexL1")
        vmWriter.writeLabel("L2.$labelIndexL2")
        labelIndexL1++
        labelIndexL2++
    }

    fun compileDo() {
        process("do")
        println("processing do")
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
        println("compile expression ${tokenizer.currentToken}")
        compileTerm()
        println("${tokenizer.currentToken} in ope ? ${tokenizer.currentToken in op}")
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
                    vmWriter.writeArithmetic("neg")
                }
                "null" -> vmWriter.writePop("constant", 0)
                else -> throw IllegalStateException("expected keyword const but received not")
            }
        } else if (tokenizer.currentTokenType == "INT_CONST") {
            vmWriter.writePush("constant", process(tokenizer.currentToken).toInt())
        } else if (tokenizer.currentTokenType == "STRING_CONST") {
            vmWriter.writePush("constant", process(tokenizer.currentToken).length)
            vmWriter.writeCall("String.new", 1)
        } else if (tokenizer.currentTokenType == "IDENTIFIER") {
            val varName = processIdentifier()
            when (tokenizer.currentToken) {
                "[" -> {
                    // if array access
                    TODO("array access")
                    process("[")
                    compileExpression()
                    process("]")
                }
                "(" -> {
                    process("(")
                    compileExpressionList()
                    process(")")
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
                        vmWriter.writeCall("$classOfMethod.$varName", numParam + 1)
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
            println("Syntax error expected: $expectedToken -> received: $currentToken")
        }
        tokenizer.advance()
        return currentToken
    }

    private fun processIdentifier(): String {
        var currentToken = tokenizer.currentToken
        if (tokenizer.currentTokenType != "IDENTIFIER") {
            println("Syntax error")
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
            vmWriter.writePop(classSymbolTable.segmentOf(symbolName), functionSymbolTable.indexOf(symbolName))
        }
    }

    private fun pushFromSymbolTable(symbolName: String) {
        try {
            vmWriter.writePush(functionSymbolTable.segmentOf(symbolName), functionSymbolTable.indexOf(symbolName))
        } catch(exception: Exception) {
            vmWriter.writePush(classSymbolTable.segmentOf(symbolName), functionSymbolTable.indexOf(symbolName))
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
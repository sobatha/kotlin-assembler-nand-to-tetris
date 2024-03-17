import java.io.File

fun main(args: Array<String>) {
    if (args.size < 1) {
        println("Usage: main.kt <input file name>")
        return
    }

    val inputFileName = args[0]
    val outputFileName = args[0].removeSuffix(".vm") + ".asm"

    val parser = Parser(inputFileName)
    val codeWriter = CodeWriter(outputFileName)
    codeWriter.write(parser)
}

class MemorySegment(className:String) {

    private val memorySegmentName = mapOf("local" to "LCL", "argument" to "ARG", "this" to "THIS", "that" to "THAT")
    val className = className
    fun setDToRAMAt(segment:String, address: Int):String = when (segment) {
        "constant" -> setDataRegisterToConstant(address)
        "static" -> setDRegisterToStaticMemory(address)
        "pointer" -> setDRegisterToPointer(address)
        "temp" -> setDataRegisterToTemp(address)
        else -> setDRegisterToMemoryAtLocalOrArgumentOrThisOrThat(segment, address)
    }

    fun addDtoAddress(segment:String, address: Int):String = when (segment) {
        "constant" -> addDtoConstantAddress(address)
        "static" -> addDtoStaticAddress(address)
        "pointer" -> addDtoPointerAddress(address)
        "temp" -> addDtoTempAddress(address)
        else -> addDtoLocalOrArgumentOrThisOrThatAddress(segment, address)
    }
    fun setDToAddress(segment:String, address: Int):String = when (segment) {
        "constant" -> setDataRegisterToConstant(address)
        "static" -> setDRegisterToStaticAddress(address)
        "pointer" -> setDRegisterToPointerAddress(address)
        "temp" -> setDataRegisterToTempAddress(address)
        else -> setDataRegisterAtLocalOrArgumentOrThisOrThat(segment, address)
    }

    private fun setDRegisterToMemoryAtLocalOrArgumentOrThisOrThat(segment:String, address: Int):String {
        val segmentAssemblyName = memorySegmentName[segment] ?: throw IllegalStateException("不正なメモリーセグメントが指定されています")
        var memoryAssembly = "@$segmentAssemblyName\nD=M\n@$address\nD=D+A\nA=D\nD=M\n"
        return memoryAssembly
    }

    private fun setDataRegisterAtLocalOrArgumentOrThisOrThat(segment:String, address: Int):String {
        val segmentAssemblyName = memorySegmentName[segment] ?: throw IllegalStateException("不正なメモリーセグメントが指定されています")
        var memoryAssembly = "@$segmentAssemblyName\nD=M\n@$address\nD=D+A\n"
        return memoryAssembly
    }

    private fun addDtoLocalOrArgumentOrThisOrThatAddress(segment:String, address: Int):String {
        val segmentAssemblyName = memorySegmentName[segment] ?: throw IllegalStateException("不正なメモリーセグメントが指定されています")
        var memoryAssembly = "@$segmentAssemblyName\nD=D+M\n@$address\nD=D+A\n"
        return memoryAssembly
    }

    fun setDataRegisterToConstant(address: Int):String {
        return "@$address\nD=A\n"
    }

    fun addDtoConstantAddress(address: Int):String {
        return "@$address\nD=D+A\n"
    }

    fun setDataRegisterToTemp(address: Int):String {
        return "@${address+5}\nD=M\n"
    }
    fun addDtoTempAddress(address: Int):String {
        return "@${address+5}\nD=D+A\n"
    }
    fun setDataRegisterToTempAddress(address: Int):String {
        return "@${address+5}\nD=A\n"
    }

    fun setDRegisterToStaticMemory(address: Int):String {
        return "@$className.$address\nD=M\n"
    }

    fun setDRegisterToStaticAddress(address: Int):String {
        return "@$className.$address\nD=A\n"
    }

    fun addDtoStaticAddress(address: Int):String {
        return "@$className.$address\nD=D+A\n"
    }

    fun setDRegisterToPointer(address:Int):String {
        val pointer = if (address==0) 3 else 4
        return "@$pointer\nD=M\n"
    }
    fun addDtoPointerAddress(address:Int):String {
        val pointer = if (address==0) 3 else 4
        return "@$pointer\nD=D+A\n"
    }
    fun setDRegisterToPointerAddress(address:Int):String {
        val pointer = if (address==0) 3 else 4
        return "@$pointer\nD=A\n"
    }
}

class Pointer(className:String) {
    val memorySegment = MemorySegment(className)
    fun codePush(seg:String, address:Int):String {
        var code = ""
        code += memorySegment.setDToRAMAt(seg, address) //push元のアドレスをアドレスレジスタに入れる
        code += setMemorySP + "M=D\n" //Stackにpush元データを保存
        code += incrementSP
        return code
    }

    fun codePop(seg:String, address:Int):String {
        var code = ""
        code += decrementSP + "A=M\nD=M\n" //popするvalueをDに格納
        code += memorySegment.addDtoAddress(seg, address) //Dにpop先のアドレスを格納(D=popするvalue + pop先.address)
        code += setMemorySP + "A=M\n" //A=pop value
        code += "A=D-A\n" //Aにpop先のアドレスを格納(D=popするvalue + pop先address)
        code += "M=D-A\n" //M=popする値
        return code
    }

    val incrementSP = "@SP\nM=M+1\n"
    val decrementSP = "@SP\nM=M-1\n"
    val setMemorySP = "@SP\nA=M\n"
}

class Arithmetic {
    private val pointer = Pointer("")
    var counter = 0

    fun code(operand:String):String =
        when (operand) {
            "add" -> codeAdd()
            "sub" -> codeSub()
            "eq" -> codeEq()
            "neg" -> codeNeg()
            "gt" -> codeGt()
            "lt" -> codeLt()
            "and" -> codeAnd()
            "or" -> codeOr()
            "not" -> codeNot()
            else -> throw IllegalStateException("許容されていないオペランドです")
        }

    fun codeAdd():String {
        var addCode = ""
        addCode += pointer.decrementSP + pointer.setMemorySP
        addCode += "D=M\n"
        addCode += pointer.decrementSP + pointer.setMemorySP
        addCode += "M=D+M\n"
        addCode += pointer.incrementSP

        return addCode
    }
    fun codeSub():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "M=M-D\n"
        code += pointer.incrementSP
        return code
    }

    fun codeNeg():String {
        var code = pointer.decrementSP + pointer.setMemorySP
        code += "M=-M\n"
        code += pointer.incrementSP
        return code
    }
    fun codeEq():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@TRUE$counter\nD;JEQ\n" +
                pointer.setMemorySP + "M=0\n@EQEND$counter\n0;JMP\n${setSPTrue(counter)}"
        code += "(EQEND$counter)\n"
        code += pointer.incrementSP
        counter++
        return code
    }
    fun codeGt():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@TRUE$counter\nD;JGT\n" +
                pointer.setMemorySP + "M=0\n@EQEND$counter\n0;JMP\n${setSPTrue(counter)}"
        code += "(EQEND$counter)\n"
        code += pointer.incrementSP

        counter++
        return code
    }

    fun codeLt():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@TRUE$counter\nD;JLT\n" +
                pointer.setMemorySP + "M=0\n@EQEND$counter\n0;JMP\n${setSPTrue(counter)}"
        code += "(EQEND$counter)\n"
        code += pointer.incrementSP

        counter++
        return code
    }
    fun codeAnd():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "M=M&D\n"
        code += pointer.incrementSP
        return code
    }
    fun codeOr():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "M=M|D\n"
        code += pointer.incrementSP
        return code
    }
    fun codeNot():String {
        var code = pointer.decrementSP + pointer.setMemorySP
        code += "M=!M\n"
        code += pointer.incrementSP
        return code
    }

    fun setSPTrue(counter:Int) = "(TRUE$counter)\n@SP\nA=M\nM=-1\n"
}

class CodeWriter (filepath:String) {
    val filepath = filepath
    val arithmetic = Arithmetic()
    val pointer = Pointer(filepath.substringAfterLast("/").substringBeforeLast("."))
    fun write(parser: Parser) {
        while (true) {
            var assembley = ""
            try {
                assembley = generateAssembler(parser)
//                println("${parser.vmCommands[parser.currentCommandIndex]}")
//                println("$assembley")

            } catch (e: Exception) {
                println("error has occur $e")
            }

            File(filepath).appendText("$assembley")
            if (!parser.hasNext()) break
            parser.advance()
        }

    }

    fun generateAssembler(parser: Parser) =
        when (parser.detectCommandType()) {
            "C_ARITHMETIC" -> arithmetic.code(parser.arg1())
            "C_POP" -> pointer.codePop(parser.arg1(), parser.arg2())
            "C_PUSH" -> pointer.codePush(parser.arg1(), parser.arg2())
            else -> "UNKNOWN type code"
        }
}

val C_ARITHMETIC = mutableListOf("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not")
val C_COMMANDS = mutableMapOf(
    "pop" to "C_POP", "push" to "C_PUSH", "label" to "C_LABEL", "goto" to "C_GOTO", "if-goto" to "C_IF",
    "call" to "C_CALL", "return" to "C_RETURN"
)
class Parser(filePath: String) {

    val vmCommands = File(filePath)
        .readLines()
        .filter { skipCommentsAndBlankLine(it) }
        .map { skipInlineCommentsAndWhiteSpace(it) }
        .map { trimAndSplitCommands(it) }

    var currentCommandIndex :Int = 0


    fun detectCommandType():String =
        when (vmCommands[currentCommandIndex]["command"]) {
            in C_ARITHMETIC -> "C_ARITHMETIC"
            in C_COMMANDS -> C_COMMANDS[vmCommands[currentCommandIndex]["command"]]!!
            else -> throw IllegalStateException("登録していないコマンドです")
        }

    fun arg1():String {
        val commandType = detectCommandType()
        return when (detectCommandType()) {
            "C_ARITHMETIC" -> vmCommands[currentCommandIndex]["command"]!!
            "C_RETURN" -> throw IllegalStateException("returnにargumentは有りません")
            else -> vmCommands[currentCommandIndex]["arg1"]!!
        }
    }

    fun arg2():Int {
        val commandType = detectCommandType()
        return when (detectCommandType()) {
            in C_COMMANDS.values -> vmCommands[currentCommandIndex]["arg2"]?.toInt()!!
            else -> throw IllegalStateException("第二引数が数値では有りません")
        }
    }

    fun advance() {
        if (hasNext()) {
            currentCommandIndex++
        }
    }

    fun hasNext(): Boolean {
        return currentCommandIndex < vmCommands.size - 1
    }

    private fun skipCommentsAndBlankLine(line: String): Boolean {
        return line.isNotEmpty() && !line.startsWith("//")
    }

    private fun skipInlineCommentsAndWhiteSpace(line: String): String {
        if (line.contains("//")) {
            val startIndex = line.indexOf("//")
            return line.substring(0, startIndex).trim()
        }
        return line
    }

    private fun trimAndSplitCommands(line: String): Map<String, String> {
        val lis = line.split(" ")
        return mapOf(
            "command" to lis[0],
            "arg1" to if (lis.size >= 2) lis[1] else "",
            "arg2" to if (lis.size >= 3) lis[2] else ""
        )
    }
}

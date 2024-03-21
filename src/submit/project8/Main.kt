import java.io.File

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    if (args.size < 1) {
        println("Usage: main.kt <input file name>")
        return
    }
    val fileName = args[0]

    if (fileName.endsWith(".vm")) {
        val parser = Parser("$fileName")
        val codeWriter = CodeWriter(fileName.removeSuffix(".vm")+".asm", fileName)
        codeWriter.write(parser)
        return
    }

    val className = fileName.substringAfterLast("/")
    val output:String = "$className.asm"
    initSys(output)
    File(fileName).walk().forEach {
        if (it.toString().endsWith(".vm")) {
            val parser = Parser(it.toString())
            val codeWriter = CodeWriter(output, it.toString())
            codeWriter.write(parser)
        }
    }

}

fun initSys(fileDir:String) {
    var functionImple = FunctionImple("Sys")
    File(fileDir).appendText("@256\nD=A\n@SP\nM=D\n" + functionImple.call("Sys.init", 0,""))
}
class Arithmetic {
    private val pointer = Pointer("")
    var counter = 0

    fun code(operand:String, currentFunction:String):String =
        when (operand) {
            "add" -> codeAdd()
            "sub" -> codeSub()
            "eq" -> codeEq(currentFunction)
            "neg" -> codeNeg()
            "gt" -> codeGt(currentFunction)
            "lt" -> codeLt(currentFunction)
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
    fun codeEq(currentFunction: String):String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@$currentFunction.TRUE$counter\nD;JEQ\n" +
                pointer.setMemorySP + "M=0\n@$currentFunction.EQEND$counter\n0;JMP\n${setSPTrue(counter, currentFunction)}"
        code += "($currentFunction.EQEND$counter)\n"
        code += pointer.incrementSP
        counter++
        return code
    }
    fun codeGt(currentFunction:String):String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@$currentFunction.TRUE$counter\nD;JGT\n" +
                pointer.setMemorySP + "M=0\n@$currentFunction.EQEND$counter\n0;JMP\n${setSPTrue(counter, currentFunction)}"
        code += "($currentFunction.EQEND$counter)\n"
        code += pointer.incrementSP

        counter++
        return code
    }

    fun codeLt(currentFunction:String):String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@$currentFunction.TRUE$counter\nD;JLT\n" +
                pointer.setMemorySP + "M=0\n@$currentFunction.EQEND$counter\n0;JMP\n${setSPTrue(counter, currentFunction)}"
        code += "($currentFunction.EQEND$counter)\n"
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

    fun setSPTrue(counter:Int, currentFunction:String) = "($currentFunction.TRUE$counter)\n@SP\nA=M\nM=-1\n"
}

class Branch {
    fun writeLabel(name: String, funcName:String):String =
        "(${funcName.uppercase()}"+"$"+"${name.uppercase()})\n"

    fun writeGoto(label: String, funcName:String):String =
        "@${funcName.uppercase()}"+"$"+"${label.uppercase()}\n0;JMP\n"

    fun writeIfGoto(label:String, funcName:String):String {
        var code = "@SP\nM=M-1\nA=M\nD=M\n" + //set pointer top of stack and D=pop()
                "@${funcName.uppercase()}"+"$"+"${label.uppercase()}\nD+1;JEQ\nD;JGT\n"
        return code
    }
}
class CodeWriter (outputFilepath:String, inputFilepath:String) {
    val filepath = outputFilepath
    val arithmetic = Arithmetic()
    val className = inputFilepath.substringAfterLast("/").substringBeforeLast(".")
    val pointer = Pointer(className)
    val branch = Branch()
    val function = FunctionImple(className)
    fun write(parser:Parser) {
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

    var currentFunction:String = ""

    fun generateAssembler(parser:Parser) =
        when(parser.detectCommandType()) {
            "C_ARITHMETIC" -> arithmetic.code(parser.arg1(), "$className.$currentFunction")
            "C_POP" -> pointer.codePop(parser.arg1(), parser.arg2())
            "C_PUSH" -> pointer.codePush(parser.arg1(), parser.arg2())
            "C_LABEL" -> branch.writeLabel(parser.arg1(), "$className.$currentFunction")
            "C_IF" -> branch.writeIfGoto(parser.arg1(), "$className.$currentFunction")
            "C_GOTO" -> branch.writeGoto(parser.arg1(), "$className.$currentFunction")
            "C_FUNCTION" -> {
                currentFunction = parser.arg1()
                function.def(parser.arg1(), parser.arg2())
            }
            "C_RETURN" -> function.returnFunc()
            "C_CALL" -> function.call(parser.arg1(), parser.arg2(), "$className.$currentFunction")
            else -> "UNKNOWN type code"
        }

}

class FunctionImple(className: String) {
    val className = className
    var returnIndex = 0
    fun def(name:String, numOfVar: Int):String {
        var code = ""
        code += "(${name.uppercase()})\n"
        for (i in 1..numOfVar) code += setMemorySP + "M=0\n" + incrementSP
        return code
    }

    fun saveCurrentPointers(): String {
        var code = ""
        code += saveMemorySegmentAddress("LCL") + incrementSP //set local address
        code += saveMemorySegmentAddress("ARG") + incrementSP
        code += saveMemorySegmentAddress("THIS") + incrementSP
        code += saveMemorySegmentAddress("THAT") + incrementSP
        return code
    }


    fun returnFunc():String {
        var code = ""
        code += "@LCL\nD=M\n" //D=LCL
        code += "@5\nD=D-A\nA=D\nD=M\n@15\nM=D\n" //RAM[15]=retAddress

        code += decrementSP + "A=M\nD=M\n" //popするvalueをDに格納
        code += "@ARG\nD=D+M\n" //Dにpop先のアドレスを格納(D=popするvalue + pop先.address)
        code += setMemorySP + "A=M\n" //A=pop value
        code += "A=D-A\n" //Aにpop先のアドレスを格納(D=popするvalue + pop先address)
        code += "M=D-A\n" //M=popする値

        code += "D=A+1\n@SP\nM=D\n" //SP = ARG + 1
        code += "@LCL\nAM=M-1\nD=M\n@THAT\nM=D\n"// THAT = *(endFrame – 1)
        code += "@LCL\nAM=M-1\nD=M\n@THIS\nM=D\n"
        code += "@LCL\nAM=M-1\nD=M\n@ARG\nM=D\n"
        code += "@LCL\nAM=M-1\nD=M\n@LCL\nM=D\n"
        code += "@15\nA=M\n0;JMP\n"

        return code

    }

    fun call(name:String, numOfArg:Int, funcName:String):String {
        var code = ""
        var label = "${funcName.uppercase()}"+"$"+"ret.$returnIndex"
        returnIndex++

        code += saveReturnAddress(label)
        code += saveCurrentPointers()
        code += repositionPointers(numOfArg)
        code += "@${name.uppercase()}\n0;JMP\n"
        code += "($label)\n"
        return code
    }

    private fun saveReturnAddress(label:String): String {
        var code = ""
        code += "@$label\nD=A\n" + setMemorySP + "M=D\n" + incrementSP //set return address
        return code
    }

    private fun repositionPointers(numOfArg: Int):String {
        var code = ""
        code += "@5\nD=A\n@SP\nD=M-D\n@$numOfArg\n" +
                "D=D-A\n@ARG\nM=D\n"        //reposition ARG
        code += "@SP\nD=M\n@LCL\nM=D\n"     //reposition LCL
        return code
    }

    val incrementSP = "@SP\nM=M+1\n"
    val decrementSP = "@SP\nM=M-1\n"
    val setMemorySP = "@SP\nA=M\n"

    fun saveMemorySegmentAddress(seg:String) = "@$seg\nD=M\n" + setMemorySP + "M=D\n"

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

val C_ARITHMETIC = mutableListOf("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not")
val C_COMMANDS = mutableMapOf(
    "pop" to "C_POP", "push" to "C_PUSH", "label" to "C_LABEL", "goto" to "C_GOTO", "if-goto" to "C_IF",
    "call" to "C_CALL", "return" to "C_RETURN", "function" to "C_FUNCTION"
)
class Parser(filePath: String) {

    val vmCommands = File(filePath)
        .readLines()
        .filter { skipCommentsAndBlankLine(it) }
        .map { skipInlineCommentsAndWhiteSpace(it) }
        .map { trimAndSplitCommands(it) }
        .filter { it["command"]?.isNotEmpty() == true }

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
            "command" to lis[0].trim(),
            "arg1" to if (lis.size >= 2) lis[1] else "",
            "arg2" to if (lis.size >= 3) lis[2] else ""
        )
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

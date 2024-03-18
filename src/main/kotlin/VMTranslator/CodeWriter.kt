package org.example.VMTranslator

import VMTranslator.FunctionImple
import java.io.File

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
                println("${parser.vmCommands[parser.currentCommandIndex]}")
                println("$assembley")

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
            "C_ARITHMETIC" -> arithmetic.code(parser.arg1())
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
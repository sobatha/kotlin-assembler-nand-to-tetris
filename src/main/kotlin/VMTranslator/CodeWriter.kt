package org.example.VMTranslator

import VMTranslator.FunctionImple
import java.io.File

class CodeWriter (filepath:String) {
    val filepath = filepath
    val arithmetic = Arithmetic()
    val pointer = Pointer(filepath.substringAfterLast("/").substringBeforeLast("."))
    val branch = Branch()
    val function = FunctionImple(filepath)
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

    fun generateAssembler(parser:Parser) =
        when(parser.detectCommandType()) {
            "C_ARITHMETIC" -> arithmetic.code(parser.arg1())
            "C_POP" -> pointer.codePop(parser.arg1(), parser.arg2())
            "C_PUSH" -> pointer.codePush(parser.arg1(), parser.arg2())
            "C_LABEL" -> branch.writeLabel(parser.arg1())
            "C_IF" -> branch.writeIfGoto(parser.arg1())
            "C_GOTO" -> branch.writeGoto(parser.arg1())
            "C_FUNCTION" -> function.def(parser.arg1(), parser.arg2())
            "C_RETURN" -> function.returnFunc()
            "C_CALL" -> function.call(parser.arg1(), parser.arg2())
            else -> "UNKNOWN type code"
        }

}
package org.example.VMTranslator

import java.io.File

class CodeWriter (filepath:String) {
    val filepath = filepath
    val arithmetic = Arithmetic()
    val pointer = Pointer(filepath.substringAfterLast("/").substringBeforeLast("."))
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
            else -> "UNKNOWN type code"
        }

}
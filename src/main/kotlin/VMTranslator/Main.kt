package org.example.VMTranslator

import java.io.File

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: main.kt <input file name> <output file name>")
        return
    }

    val inputFileName = args[0]
    val outputFileName = args[1]

    val parser = Parser(inputFileName)
    val codeWriter = CodeWriter(outputFileName)
    codeWriter.write(parser)
}
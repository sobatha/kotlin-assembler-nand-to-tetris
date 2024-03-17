package org.example.VMTranslator

import java.io.File

fun main() {
    val fileName = "data/project8/ProgramFlow/BasicLoop/BasicLoop"

    val parser = Parser("$fileName.vm")

    println(parser.vmCommands)
    val codeWriter = CodeWriter("$fileName.asm")
    codeWriter.write(parser)
}
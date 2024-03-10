package org.example.VMTranslator

import java.io.File

fun main() {
    val parser = Parser("vmCode/SimpleAdd.vm")
    val codeWriter = CodeWriter("vmCode/SimpleAdd.asm")
    codeWriter.write(parser)
}
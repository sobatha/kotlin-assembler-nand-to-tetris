package org.example.VMTranslator

import VMTranslator.FunctionImple
import java.io.File

fun main() {
    val fileName = "data/project8/FunctionCalls/StaticsTest"

    if (fileName.endsWith(".vm")) {
        val parser = Parser("$fileName")
        val codeWriter = CodeWriter(fileName.removeSuffix(".vm")+".asm", fileName)
        codeWriter.write(parser)
        return
    }

    val className = fileName.substringAfterLast("/")
    val output:String = fileName+"/$className.asm"
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
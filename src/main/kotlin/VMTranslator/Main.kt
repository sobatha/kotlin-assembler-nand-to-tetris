package org.example.VMTranslator

import VMTranslator.FunctionImple
import java.io.File

fun main() {
    val fileName = "data/project8/FunctionCalls/SimpleFunction/SimpleFunction.vm"

    if (fileName.endsWith(".vm")) {
        val parser = Parser("$fileName")
        val codeWriter = CodeWriter("$fileName".removeSuffix(".vm")+".asm")
        codeWriter.write(parser)
        return
    }

    initSys(fileName)
    File(fileName).walk().forEach {
        if (it.endsWith(".vm")) {
            val parser = Parser(it.toString())
            val codeWriter = CodeWriter(fileName+".asm")
            codeWriter.write(parser)
        }
    }

}

fun initSys(fileDir:String) {
    var functionImple = FunctionImple("Sys")
    File(fileDir).appendText("@256\nD=A\n@SP\nM=D\n" + functionImple.call("Sys.init", 0,""))
}
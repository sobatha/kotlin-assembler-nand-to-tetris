package Compiler

import java.io.File

class VMwriter(val outputFilePath: String) {
    fun writePush(segment: String, index:Int) {
        write("push ${segment.lowercase()} $index")
    }
    fun writePop(segment: String, index: Int) {
        write("pop ${segment.lowercase()} $index")
    }
    fun writeArithmetic(command:String) {
        write("${command.lowercase()}")
    }
    fun writeLabel(label: String) {
        write("label $label")
    }
    fun writeGoto(label: String) {
        write("goto $label")
    }
    fun writeIf(label: String) {
        write("if-goto $label")
    }
    fun writeCall(name: String, nArgs: Int) {
        write("call $name $nArgs")
    }
    fun writeFunction(name: String, nVars: Int) {
        write("function $name $nVars")
    }
    fun writeReturn() { write("return") }
    private fun write(content:String) {
        File(outputFilePath).writeText(content + "\n")
    }
}
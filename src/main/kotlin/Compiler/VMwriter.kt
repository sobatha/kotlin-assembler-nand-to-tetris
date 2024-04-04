package Compiler

import java.io.File

class VMwriter(private val outputFilePath: String) {
    fun writePush(segment: String, index:Int) {
        write("push ${segment.lowercase()} $index")
    }
    fun writePop(segment: String, index: Int) {
        write("pop ${segment.lowercase()} $index")
    }
    fun writeArithmetic(command:String) {
        val operand = when(command) {
            "+" -> "add"
            "-" -> "sub"
            "=" -> "eq"
            "<" -> "lt"
            ">" -> "gt"
            "&" -> "and"
            "|" -> "or"
            "*" -> "call Math.multiply 2"
            "/" -> "call Math.divide 2"
            else -> "unknown op"
        }
        write("$operand")
    }
    fun writeUnaryOp(command: String) {
        val operand = when(command) {
            "-" -> "neg"
            "~" -> "not"
            else -> "unknown op"
        }
        write("$operand")
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
    fun writeFunction(className: String, funName:String, nVars: Int) {
        write("function $className.$funName $nVars")
    }
    fun writeReturn() { write("return") }
    private fun write(content:String) {
        File(outputFilePath).appendText(content + "\n")
    }
}
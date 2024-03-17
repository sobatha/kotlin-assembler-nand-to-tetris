package org.example.VMTranslator

class Branch {
    fun writeLabel(name: String, funcName:String):String =
        "(${funcName.uppercase()}"+"$"+"${name.uppercase()})\n"

    fun writeGoto(label: String, funcName:String):String =
        "@${funcName.uppercase()}"+"$"+"${label.uppercase()}\n0;JMP\n"

    fun writeIfGoto(label:String, funcName:String):String {
        var code = "@SP\nM=M-1\nA=M\nD=M\n" + //set pointer top of stack and D=pop()
                "@${funcName.uppercase()}"+"$"+"${label.uppercase()}\nD;JGT\n"
        return code
    }
}
package org.example.VMTranslator

class Branch {
    fun writeLabel(name: String):String =
        "(${name.uppercase()})\n"

    fun writeGoto(label: String):String =
        "@${label.uppercase()}\n0;JMP\n"

    fun writeIfGoto(label:String):String {
        var code = "@SP\nM=M-1\nA=M\nD=M\n" + //set pointer top of stack and D=pop()
                "@${label.uppercase()}\nD;JGT\n"
        return code
    }
}
package org.example.VMTranslator

class Pointer(className:String) {
    val memorySegment = MemorySegment(className)
    fun codePush(seg:String, address:Int):String {
        var code = ""
        code += memorySegment.setDToRAMAt(seg, address) //push元のアドレスをアドレスレジスタに入れる
        code += setMemorySP + "M=D\n" //Stackにpush元データを保存
        code += incrementSP
        return code
    }

    fun codePop(seg:String, address:Int):String {
        var code = ""
        code += decrementSP + "A=M\nD=M\n" //popするvalueをDに格納
        code += memorySegment.addDtoAddress(seg, address) //Dにpop先のアドレスを格納(D=popするvalue + pop先.address)
        code += setMemorySP + "A=M\n" //A=pop value
        code += "A=D-A\n" //Aにpop先のアドレスを格納(D=popするvalue + pop先address)
        code += "M=D-A\n" //M=popする値
        return code
    }

    val incrementSP = "@SP\nM=M+1\n"
    val decrementSP = "@SP\nM=M-1\n"
    val setMemorySP = "@SP\nA=M\n"
}
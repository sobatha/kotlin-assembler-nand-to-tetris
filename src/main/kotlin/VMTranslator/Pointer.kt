package org.example.VMTranslator

class Pointer() {
    val memorySegment = MemorySegment()
    fun codePush(seg:String, address:Int):String {
        var code = ""
        code += memorySegment.setMemoryDataToDRegister(seg, address) //push元のアドレスをアドレスレジスタに入れる
        code += setMemorySP + "M=D\n" //Stackにpush元データを保存
        code += incrementSP
        return code
    }

    fun codePop(seg:String, address:Int):String {
        var code = ""
        code += decrementSP //popするStackの位置へ移動
        code += memorySegment.setDataRegisterAddress(seg, address) //pop先アドレスをDに格納
        code += "@pop\nM=D\n" //pop先アドレスを@popに保存
        code += setMemorySP + "D=M\n" //popする数値をDに格納
        code += "@pop\nA=M\nM=D\n" //popする数値を格納したDをpop先アドレスにうつす
        code += incrementSP
        return code

    }

    val incrementSP = "@SP\nM=M+1\n"
    val decrementSP = "@SP\nM=M-1\n"
    val setMemorySP = "@SP\nA=M\n"
}
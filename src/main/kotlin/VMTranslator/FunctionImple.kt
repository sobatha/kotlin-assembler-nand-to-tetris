package VMTranslator;

import org.example.VMTranslator.Branch

public class FunctionImple(className: String) {
    val className = className
    var returnIndex = 0
    fun def(name:String, numOfVar: Int):String {
        var code = ""
        code += "(${name.uppercase()})\n"
        for (i in 1..numOfVar) code += setMemorySP + "M=0\n" + incrementSP
        return code
    }

    fun saveCurrentPointers(): String {
        var code = ""
        code += saveMemorySegmentAddress("LCL") + incrementSP //set local address
        code += saveMemorySegmentAddress("ARG") + incrementSP
        code += saveMemorySegmentAddress("THIS") + incrementSP
        code += saveMemorySegmentAddress("THAT") + incrementSP
        return code
    }


    fun returnFunc():String {
        var code = ""
        code += "@LCL\nD=M\n" //D=LCL
        code += "@5\nD=D-A\nA=D\nD=M\n@15\nM=D\n" //RAM[15]=retAddress

        code += decrementSP + "A=M\nD=M\n" //popするvalueをDに格納
        code += "@ARG\nD=D+M\n" //Dにpop先のアドレスを格納(D=popするvalue + pop先.address)
        code += setMemorySP + "A=M\n" //A=pop value
        code += "A=D-A\n" //Aにpop先のアドレスを格納(D=popするvalue + pop先address)
        code += "M=D-A\n" //M=popする値

        code += "D=A+1\n@SP\nM=D\n" //SP = ARG + 1
        code += "@LCL\nAM=M-1\nD=M\n@THAT\nM=D\n"// THAT = *(endFrame – 1)
        code += "@LCL\nAM=M-1\nD=M\n@THIS\nM=D\n"
        code += "@LCL\nAM=M-1\nD=M\n@ARG\nM=D\n"
        code += "@LCL\nAM=M-1\nD=M\n@LCL\nM=D\n"
        code += "@15\nA=M\n0;JMP\n"

        return code

    }

    fun call(name:String, numOfArg:Int):String {
        var code = ""
        var label = "${className.uppercase()}.$returnIndex"
        returnIndex++

        code += saveReturnAddress(label)
        code += saveCurrentPointers()
        code += repositionPointers(numOfArg)
        code += "@${name.uppercase()}\n0;JMP\n"
        code += "($label)"
        return code
    }

    private fun saveReturnAddress(label:String): String {
        var code = ""
        code += saveMemorySegmentAddress(label) + incrementSP //set return address
        return code
    }

    private fun repositionPointers(numOfArg: Int) {
        var code = ""
        code += "@5\nD=A\n@SP\nD=A-D\n@$numOfArg\n" +
                "D=D-A\n@ARG\nM=D\n"        //reposition ARG
        code += "@SP\nD=A\n@LCL\nM=D\n"     //reposition LCL
    }

    val incrementSP = "@SP\nM=M+1\n"
    val decrementSP = "@SP\nM=M-1\n"
    val setMemorySP = "@SP\nA=M\n"

    fun saveMemorySegmentAddress(seg:String) = "@$seg\nD=A\n" + setMemorySP + "M=D\n"

}

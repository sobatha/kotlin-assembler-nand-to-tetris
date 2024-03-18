package org.example.VMTranslator


class Arithmetic {
    private val pointer = Pointer("")
    var counter = 0

    fun code(operand:String, currentFunction:String):String =
        when (operand) {
            "add" -> codeAdd()
            "sub" -> codeSub()
            "eq" -> codeEq(currentFunction)
            "neg" -> codeNeg()
            "gt" -> codeGt(currentFunction)
            "lt" -> codeLt(currentFunction)
            "and" -> codeAnd()
            "or" -> codeOr()
            "not" -> codeNot()
            else -> throw IllegalStateException("許容されていないオペランドです")
        }

    fun codeAdd():String {
        var addCode = ""
        addCode += pointer.decrementSP + pointer.setMemorySP
        addCode += "D=M\n"
        addCode += pointer.decrementSP + pointer.setMemorySP
        addCode += "M=D+M\n"
        addCode += pointer.incrementSP

        return addCode
    }
    fun codeSub():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "M=M-D\n"
        code += pointer.incrementSP
        return code
    }

    fun codeNeg():String {
        var code = pointer.decrementSP + pointer.setMemorySP
        code += "M=-M\n"
        code += pointer.incrementSP
        return code
    }
    fun codeEq(currentFunction: String):String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@$currentFunction.TRUE$counter\nD;JEQ\n" +
                pointer.setMemorySP + "M=0\n@$currentFunction.EQEND$counter\n0;JMP\n${setSPTrue(counter, currentFunction)}"
        code += "($currentFunction.EQEND$counter)\n"
        code += pointer.incrementSP
        counter++
        return code
    }
    fun codeGt(currentFunction:String):String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@$currentFunction.TRUE$counter\nD;JGT\n" +
                pointer.setMemorySP + "M=0\n@$currentFunction.EQEND$counter\n0;JMP\n${setSPTrue(counter, currentFunction)}"
        code += "($currentFunction.EQEND$counter)\n"
        code += pointer.incrementSP

        counter++
        return code
    }

    fun codeLt(currentFunction:String):String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M-D\n@$currentFunction.TRUE$counter\nD;JLT\n" +
                pointer.setMemorySP + "M=0\n@$currentFunction.EQEND$counter\n0;JMP\n${setSPTrue(counter, currentFunction)}"
        code += "($currentFunction.EQEND$counter)\n"
        code += pointer.incrementSP

        counter++
        return code
    }
    fun codeAnd():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "M=M&D\n"
        code += pointer.incrementSP
        return code
    }
    fun codeOr():String {
        var code = ""
        code += pointer.decrementSP + pointer.setMemorySP
        code += "D=M\n"
        code += pointer.decrementSP + pointer.setMemorySP
        code += "M=M|D\n"
        code += pointer.incrementSP
        return code
    }
    fun codeNot():String {
        var code = pointer.decrementSP + pointer.setMemorySP
        code += "M=!M\n"
        code += pointer.incrementSP
        return code
    }

    fun setSPTrue(counter:Int, currentFunction:String) = "($currentFunction.TRUE$counter)\n@SP\nA=M\nM=-1\n"
}
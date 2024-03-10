package org.example.VMTranslator


class Arithmetic {
    private val pointer = Pointer()

    fun code(operand:String):String =
        when (operand) {
            "add" -> codeAdd()
            "sub" -> codeSub()
            "eq" -> codeEq()
            "neg" -> codeNeg()
            "gt" -> codeGt()
            "lt" -> codeLt()
            "and" -> codeAdd()
            "or" -> codeOr()
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
        var subCode = ""
        return subCode
    }

    fun codeNeg():String {
        var negCode = ""
        return negCode
    }
    fun codeEq():String {
        var eqCode = ""
        return eqCode
    }
    fun codeGt():String {
        var gtCode = ""
        return gtCode
    }

    fun codeLt():String {
        var ltCode = ""
        return ltCode
    }
    fun codeAnd():String {
        var andCode = ""
        return andCode
    }
    fun codeOr():String {
        var orCode = ""
        return orCode
    }
    fun codeNot():String {
        var notCode = ""
        return notCode
    }
}
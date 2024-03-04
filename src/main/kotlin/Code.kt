package org.example

val C_CODE = mutableMapOf(
    "0" to "0101010", "1" to "0111111", "-1" to "0111010", "D" to "0001100", "A" to "0110000", "!D" to "0001101",
    "!A" to "0110001", "-D" to "0001111", "-A" to "0110011", "D+1" to "0011111", "A+1" to "0110111", "D-1" to "0001110",
    "A-1" to "0110010", "D+A" to "0000010", "D-A" to "0010011", "A-D" to "0000111", "D&A" to "0000000", "D|A" to "0010101",
    "M" to "1110000", "!M" to "1110001", "-M" to "1110011", "M+1" to "1110111",
    "M-1" to "1110010", "D+M" to "1000010", "D-M" to "1010011", "M-D" to "1000111", "D&M" to "1000000", "D|M" to "1010101"
)

val JMP_CODE = mutableMapOf(
    "JGT" to "001", "JEQ" to "010", "JGE" to "011", "JLT" to "100",
    "JNE" to "101", "JLE" to "110", "JMP" to "111"
)

class Code {
    fun codeAInstruction(instruction: String): String {
        var num = convertDecimalToBinary(instruction.toLong())
        return String.format("%016d", num)
    }

    fun codeCInstruction(instruction: String): String {
        var prefix: String = "111"
        var destinationCode: String = "000"
        var opeCode: String = ""
        var jumpCode: String = "000"

        if (instruction.contains("=")) {
            var (dest, ope) = instruction.split("=")
            destinationCode = codeDestination(dest)
            opeCode = C_CODE.getValue(ope)
        } else if (instruction.contains(";")) {
            var (ope, jmp) = instruction.split(";")
            opeCode = C_CODE.getValue(ope)
            jumpCode = JMP_CODE.getOrDefault(jmp, "000")
        }

        return "$prefix$opeCode$destinationCode$jumpCode"
    }

    private fun convertDecimalToBinary(decimal: Long) :Long {
        var target = decimal
        val intList = mutableListOf<Long>()

        if (decimal == 0.toLong()) return decimal

        while (target > 0) {
            intList.add(target % 2)
            target /= 2
        }
        return intList.reversed().joinToString("").toLong()
    }

    private fun codeDestination(dest:String):String {
        var code = 0.toLong()
        if (dest.contains("M")) code += 1
        if (dest.contains("D")) code += 2
        if (dest.contains("A")) code += 4

        code = convertDecimalToBinary(code)
        return String.format("%03d", code)
    }
}
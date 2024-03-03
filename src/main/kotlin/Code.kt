package org.example

class Code {
    fun CodeAInstruction(instruction: String): String {
        var num = convertDecimalToBinary(instruction.toInt())
        return String.format("%016d", num)
    }

    fun convertDecimalToBinary(int: Int) :Int {
        var target = int
        val intList = mutableListOf<Int>()

        if (int == 0) return int

        while (target > 0) {
            intList.add(target % 2)
            target /= 2
        }
        return intList.reversed().joinToString("").toInt()
    }
}
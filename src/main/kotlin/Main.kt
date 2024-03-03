package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    val parser = Parser()
    val code = Code()
    val instructions = parser.readline("data/Add.asm")
    println(instructions)
    for (instruction in instructions) {
        if (instruction.startsWith("@")) {
            val machineCode = code.CodeAInstruction(instruction.removePrefix("@"))
            println("$instruction => $machineCode")
        }
    }
}
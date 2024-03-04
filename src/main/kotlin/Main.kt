package org.example
import java.io.File

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    val parser = Parser()
    val code = Code()
    val instructions = parser.readline("data/RectL.asm")

    for (instruction in instructions) {
        var machineCode: String = if (instruction.startsWith("@")) {
            code.codeAInstruction(instruction.removePrefix("@"))
        } else {
            code.codeCInstruction(instruction)
        }
        println("$instruction => $machineCode")
        File("data/RectL.hack").appendText("$machineCode\n")
    }
}
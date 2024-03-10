package org.example.VMTranslator
import org.jetbrains.annotations.NotNull
import java.io.File

val C_ARITHMETIC = mutableListOf("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not")
val C_COMMANDS = mutableMapOf(
    "pop" to "C_POP", "push" to "C_PUSH", "label" to "C_LABEL", "goto" to "C_GOTO", "if-goto" to "C_IF",
    "call" to "C_CALL", "return" to "C_RETURN"
)
class Parser(filePath: String) {

    val vmCommands = File(filePath)
        .readLines()
        .filter { skipCommentsAndBlankLine(it) }
        .map { skipInlineCommentsAndWhiteSpace(it) }
        .map { trimAndSplitCommands(it) }

    var currentCommandIndex :Int = 0


    fun detectCommandType():String =
        when (vmCommands[currentCommandIndex]["command"]) {
            in C_ARITHMETIC -> "C_ARITHMETIC"
            in C_COMMANDS -> C_COMMANDS[vmCommands[currentCommandIndex]["command"]]!!
            else -> throw IllegalStateException("登録していないコマンドです")
        }

    fun arg1():String {
        val commandType = detectCommandType()
        return when (detectCommandType()) {
            "C_ARITHMETIC" -> vmCommands[currentCommandIndex]["command"]!!
            "C_RETURN" -> throw IllegalStateException("returnにargumentは有りません")
            else -> vmCommands[currentCommandIndex]["arg1"]!!
        }
    }

    fun arg2():Int {
        val commandType = detectCommandType()
        return when (detectCommandType()) {
            in C_COMMANDS.values -> vmCommands[currentCommandIndex]["arg2"]?.toInt()!!
            else -> throw IllegalStateException("第二引数が数値では有りません")
        }
    }

    fun advance() {
        if (hasNext()) {
            currentCommandIndex++
        }
    }

    fun hasNext(): Boolean {
        return currentCommandIndex < vmCommands.size - 1
    }

    private fun skipCommentsAndBlankLine(line: String): Boolean {
        return line.isNotEmpty() && !line.startsWith("//")
    }

    private fun skipInlineCommentsAndWhiteSpace(line: String): String {
        if (line.contains("//")) {
            val startIndex = line.indexOf("//")
            return line.substring(0, startIndex).trim()
        }
        return line
    }

    private fun trimAndSplitCommands(line: String): Map<String, String> {
        val lis = line.split(" ")
        return mapOf(
            "command" to lis[0],
            "arg1" to if (lis.size >= 2) lis[1] else "",
            "arg2" to if (lis.size >= 3) lis[2] else ""
        )
    }
}
package org.example.Assembler
import java.io.File

class Parser {
    public fun readline(filePath: String): List<String> {
        val linesWithoutComments = File(filePath)
            .readLines()
            .map { it.trim() }
            .filter { skipCommentsAndBlankLine(it) }
            .map { skipInlineCommentsAndWhiteSpace(it) }
        return linesWithoutComments
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
}
package Compiler

import java.io.File

val TOKEN_TYPE = listOf("KEYWORD", "SYMBOL", "IDENTIFIER", "INT_CONST", "STRING_CONST")
val KEYWORDS = listOf("class", "constructor", "function", "method", "field",  "static", "var", "int", "char",
"boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return")
val SYMBOLS = listOf('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~')
class JackTokenizer(filePath: String) {

    var token = ""
    var currentTokenType = ""
    private var commentFlag = false
    var tokens:MutableList<String> = mutableListOf()

    init {
        val lines = File(filePath)
            .readLines()
            .filter { skipCommentsAndBlankLine(it) }
            .map { skipInlineCommentsAndWhiteSpace(it) }
        for (line in lines) decomposeToken(line)
    }
    fun hasMoreToken():Boolean {
        return true
    }

    fun advance() {

    }

    fun getTokenType():String {
        return TOKEN_TYPE.shuffled().first()
    }

    fun getKeyWord(): String {
        return KEYWORDS.shuffled().first()
    }

    fun getSymbol(): Char {
        return ';'
    }

    fun getIdentifier(): String {
        return "variable"
    }

    fun getIntVal(): Int {
        return 1
    }

    fun getStringVal(): String {
        return "string"
    }
    private fun skipCommentsAndBlankLine(line: String): Boolean {
        var temp = line

        if (temp.startsWith("/**")) {
            commentFlag = true
        }
        if (commentFlag) {
            if (temp.endsWith("*/")) {
                commentFlag = false
            }
            temp = ""
        }
        return temp.isNotEmpty() && !temp.startsWith("//")
    }

    private fun skipInlineCommentsAndWhiteSpace(line: String): String {
        if (line.contains("//")) {
            val startIndex = line.indexOf("//")
            return line.substring(0, startIndex).trim()
        }
        return line.trim()
    }

    private fun decomposeToken(line: String) {
        val words = line.split(" ")
        for (word in words) {
            println(word)
            var temp = word.trim()
            var token = ""
            for (c in temp) {
                if (c in SYMBOLS) {
                    if (token.isNotEmpty()) {
                        tokens.add(token)
                        token = ""
                    }
                    tokens.add(c.toString())
                }
                else token += c
            }
            if (token.isNotEmpty()) tokens.add(token)
        }
    }

}
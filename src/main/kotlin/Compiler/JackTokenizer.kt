package Compiler

import java.io.File

val TOKEN_TYPE = listOf("KEYWORD", "SYMBOL", "IDENTIFIER", "INT_CONST", "STRING_CONST")
val KEYWORDS = listOf("class", "constructor", "function", "method", "field",  "static", "var", "int", "char",
"boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return")
val SYMBOLS = listOf('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~')
class JackTokenizer(filePath: String) {

    var currentTokenType = ""
    var currentToken = ""
    var currentIndex = 0
    private var commentFlag = false
    var tokens:MutableList<String> = mutableListOf()

    init {
        val lines = File(filePath)
            .readLines()
            .filter { skipCommentsAndBlankLine(it) }
            .map { skipInlineCommentsAndWhiteSpace(it) }
        for (line in lines) decomposeToken(line, tokens)
        tokens = composeStringConst(tokens)
    }
    fun hasMoreToken():Boolean {
        return currentIndex < tokens.size
    }

    fun advance() {
        if (currentIndex >= tokens.size) throw IllegalStateException("current index is out of range")
        currentToken = tokens[currentIndex]
        currentTokenType =  when {
            currentToken.length == 1 && currentToken[0] in SYMBOLS  -> "SYMBOL"
            currentToken in KEYWORDS -> "KEYWORD"
            currentToken.toIntOrNull() != null -> "INT_CONST"
            currentToken.startsWith("\"") && currentToken.endsWith("\"") -> "STRING_CONST"
            else -> "IDENTIFIER"
        }
        currentIndex++
    }

    fun getTokenType():String {
        return currentTokenType
    }

    fun getKeyWord(): String {
        return currentToken
    }

    fun getSymbol(): Char {
        return currentToken[0]
    }

    fun getIdentifier(): String {
        return currentToken
    }

    fun getIntVal(): Int {
        return currentToken.toInt()
    }

    fun getStringVal(): String {
        return currentToken
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

    private fun decomposeToken(line: String, tokens:MutableList<String>) {
        val words = line.split(" ")

        for (word in words) {
//            println(word)
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

    private fun composeStringConst(words: List<String>): MutableList<String> {
        val tokens:MutableList<String> = mutableListOf()
        var stringConst = ""
        for (word in words) {
            if (word.startsWith("\"")) {
                if (word.endsWith("\"")) {
                    tokens.add(word)
                } else {
                    stringConst += word
                }
            }
            else if (word.endsWith("\"")) {
                stringConst += " $word"
                tokens.add(stringConst)
                stringConst = ""
            } else {
                if (stringConst.isNotEmpty()) stringConst += " $word"
                else tokens.add(word)
            }
        }
        return tokens
    }
}
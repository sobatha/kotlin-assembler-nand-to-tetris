package Compiler
val TOKEN_TYPE = listOf("KEYWORD", "SYMBOL", "IDENTIFIER", "INT_CONST", "STRING_CONST")
val KEYWORDS = listOf("CLASS", "METHOD", "CONSTRUCTOR", "INT", "BOOLEAN", "CHAR", "VOID", "STATIC",
                    "VAR", "FIELD", "LET", "DO", "IF", "ELSE", "WHILE", "RETURN", "TRUE", "FALSE", "NULL", "THIS")
class JackTokenizer(fileName: String) {
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
}
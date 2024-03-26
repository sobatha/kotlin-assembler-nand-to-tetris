
package Compiler
 fun main() {
     val file = "data/10/ExpressionLessSquare/Main.jack"

     var tokenizer = JackTokenizer(file)
     println(tokenizer.tokens)

     while (tokenizer.hasMoreToken()) {
         tokenizer.advance()
         var token = when (tokenizer.getTokenType()) {
             "KEYWORD" -> tokenizer.getKeyWord()
             "SYMBOL" -> tokenizer.getSymbol()
             "IDENTIFIER" -> tokenizer.getIdentifier()
             "INT_CONST" -> tokenizer.getIntVal()
             "STRING_CONST" -> tokenizer.getStringVal()
             else -> "error"
         }

         println("${tokenizer.getTokenType()} : $token")
     }

 }
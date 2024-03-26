
package Compiler
 fun main() {
     val file = "data/10/ExpressionLessSquare/Main.jack"
     val outputFile = file.removeSuffix(".jack") + ".xml"

     var tokenizer = JackTokenizer(file)
     println(tokenizer.tokens)

     var compiler = CompilationEngine(tokenizer, outputFile)
     compiler.compile()
 }
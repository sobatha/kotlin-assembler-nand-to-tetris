
package Compiler

import org.example.VMTranslator.CodeWriter
import org.example.VMTranslator.Parser
import org.example.VMTranslator.initSys
import java.io.File

fun main() {
     val path = "data/10/ArrayTest"


     if (path.endsWith(".jack")) {
         var tokenizer = JackTokenizer(path)
//         println(tokenizer.tokens)
         var compiler = CompilationEngine(tokenizer, path.removeSuffix(".jack")+"Test.xml")
         compiler.compile()
         return
     }


     File(path).walk().forEach {
         if (it.toString().endsWith(".jack")) {
             var tokenizer = JackTokenizer(it.toString())
             var compiler = CompilationEngine(tokenizer, it.toString().removeSuffix(".jack")+"Test.xml")
             compiler.compile()
         }
     }

 }
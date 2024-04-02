package Compiler

class VMwriter(val outputFilePath: String) {
    fun writePush(segment: String, index:Int) {

    }

    fun writePop(segment: String, index: Int) {

    }

    fun writeArithmetic(command:String) {

    }
    fun writeLabel(label: String) {

    }

    fun writeGoto(label: String) {

    }
    fun writeIf(label: String) {

    }
    fun writeCall(name: String, nArgs: Int) {

    }
    fun writeFunction(name: String, nVars: Int) {

    }
    fun writeReturn() {}
}
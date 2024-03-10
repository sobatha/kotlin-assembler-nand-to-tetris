package org.example.VMTranslator

class MemorySegment {

    private val memorySegmentName = mapOf("local" to "LCL", "argument" to "ARG", "this" to "THIS", "that" to "THAT")

    fun setMemoryDataToDRegister(segment:String, address: Int):String = when (segment) {
            "constant" -> setDataRegisterAtConstantAddress(address)
            "static" -> ""
            "pointer" -> ""
            "temp" -> ""
            else -> setDRegisterToMemoryAtLocalOrArgumentOrThisOrThat(segment, address)
        }
    fun setDataRegisterAddress(segment:String, address: Int):String = when (segment) {
        "constant" -> setDataRegisterAtConstantAddress(address)
        "static" -> ""
        "pointer" -> ""
        "temp" -> ""
        else -> setDataRegisterAtLocalOrArgumentOrThisOrThat(segment, address)
    }

    private fun setDRegisterToMemoryAtLocalOrArgumentOrThisOrThat(segment:String, address: Int):String {
        val segmentAssemblyName = memorySegmentName[segment] ?: throw IllegalStateException("不正なメモリーセグメントが指定されています")
        var memoryAssembly = "@$segmentAssemblyName\nD=M\n@$address\nD=D+A\nA=D\nD=M\n"
        return memoryAssembly
    }

    private fun setDataRegisterAtLocalOrArgumentOrThisOrThat(segment:String, address: Int):String {
        val segmentAssemblyName = memorySegmentName[segment] ?: throw IllegalStateException("不正なメモリーセグメントが指定されています")
        var memoryAssembly = "@$segmentAssemblyName\nD=M\n@$address\nD=D+A\n"
        return memoryAssembly
    }

    fun setDataRegisterAtConstantAddress(address: Int):String {
        return "@$address\nD=A\n"
    }
}
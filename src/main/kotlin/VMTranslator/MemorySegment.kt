package org.example.VMTranslator

class MemorySegment(className:String) {

    private val memorySegmentName = mapOf("local" to "LCL", "argument" to "ARG", "this" to "THIS", "that" to "THAT")
    val className = className
    fun setDToRAMAt(segment:String, address: Int):String = when (segment) {
            "constant" -> setDataRegisterToConstant(address)
            "static" -> setDRegisterToStaticMemory(address)
            "pointer" -> setDRegisterToPointer(address)
            "temp" -> setDataRegisterToTemp(address)
            else -> setDRegisterToMemoryAtLocalOrArgumentOrThisOrThat(segment, address)
        }

    fun addDtoAddress(segment:String, address: Int):String = when (segment) {
        "constant" -> addDtoConstantAddress(address)
        "static" -> addDtoStaticAddress(address)
        "pointer" -> addDtoPointerAddress(address)
        "temp" -> addDtoTempAddress(address)
        else -> addDtoLocalOrArgumentOrThisOrThatAddress(segment, address)
    }
    fun setDToAddress(segment:String, address: Int):String = when (segment) {
        "constant" -> setDataRegisterToConstant(address)
        "static" -> setDRegisterToStaticAddress(address)
        "pointer" -> setDRegisterToPointerAddress(address)
        "temp" -> setDataRegisterToTempAddress(address)
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

    private fun addDtoLocalOrArgumentOrThisOrThatAddress(segment:String, address: Int):String {
        val segmentAssemblyName = memorySegmentName[segment] ?: throw IllegalStateException("不正なメモリーセグメントが指定されています")
        var memoryAssembly = "@$segmentAssemblyName\nD=D+M\n@$address\nD=D+A\n"
        return memoryAssembly
    }

    fun setDataRegisterToConstant(address: Int):String {
        return "@$address\nD=A\n"
    }

    fun addDtoConstantAddress(address: Int):String {
        return "@$address\nD=D+A\n"
    }

    fun setDataRegisterToTemp(address: Int):String {
        return "@${address+5}\nD=M\n"
    }
    fun addDtoTempAddress(address: Int):String {
        return "@${address+5}\nD=D+A\n"
    }
    fun setDataRegisterToTempAddress(address: Int):String {
        return "@${address+5}\nD=A\n"
    }

    fun setDRegisterToStaticMemory(address: Int):String {
        return "@$className.$address\nD=M\n"
    }

    fun setDRegisterToStaticAddress(address: Int):String {
        return "@$className.$address\nD=A\n"
    }

    fun addDtoStaticAddress(address: Int):String {
        return "@$className.$address\nD=D+A\n"
    }

    fun setDRegisterToPointer(address:Int):String {
        val pointer = if (address==0) 3 else 4
        return "@$pointer\nD=M\n"
    }
    fun addDtoPointerAddress(address:Int):String {
        val pointer = if (address==0) 3 else 4
        return "@$pointer\nD=D+A\n"
    }
    fun setDRegisterToPointerAddress(address:Int):String {
        val pointer = if (address==0) 3 else 4
        return "@$pointer\nD=A\n"
    }
}
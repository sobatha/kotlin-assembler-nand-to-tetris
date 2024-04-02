package Compiler
enum class Kind {
    STATIC, FIELD, ARG, VAR
}
class SymbolTable {
    var table = mutableMapOf<String, Symbol>()
    var kindIndex = mutableMapOf<Kind, Int>(
        Kind.VAR to 0, Kind.STATIC to 0, Kind.FIELD to 0, Kind.ARG to 0
    )

    val reset = {
        table = mutableMapOf<String, Symbol>()
        kindIndex = mutableMapOf<Kind, Int>(
            Kind.VAR to 0, Kind.STATIC to 0, Kind.FIELD to 0, Kind.ARG to 0
        )
    }
    val define = { name: String, type:String, kind:Kind ->
        val index = kindIndex[kind]!!
        kindIndex[kind] = index + 1
        val symbol = Symbol(name, type, kind, index)
        table[name] = symbol
    }
    val varCount = { it:String ->
        kindIndex.getOrDefault(kindOf(it), 0)
    }
    val kindOf = { it: String ->
        when (it) {
            "static" -> Kind.STATIC
            "field" -> Kind.FIELD
            "arg" -> Kind.ARG
            "var" -> Kind.VAR
            else -> IllegalStateException("ünknown identifier kind")
        }
    }
    val typeOf = { name: String ->
        table.getOrDefault(name, null)?.type
    }
    val indexOf = { name:String ->
        table.getOrDefault(name, null)?.index
    }
}

data class Symbol(val name: String, val type: String, val kind: Kind, val index:Int) {
}
package com.scurab.android.anuitor.extract2

abstract class BaseExtractor {

    open fun fillValues(item: Any, context: ExtractingContext): MutableMap<String, Any> {
        onFillValues(item, context)
        return context.data
    }

    protected open fun onFillValues(item: Any, context: ExtractingContext) {
        context.put("Inheritance", 0, item) { inheritance() }
        context.put("Type", 0, item) { item.javaClass.name }
        context.put("StringValue", 0, item) { this.toString() }
        context.put("Extractor", 0, this.javaClass.name) { this.toString() }
    }

    protected fun Any?.extract(context: ExtractingContext): Any {
        if (this == null) {
            return "null"
        }
        return when {
            this is Iterable<*> -> this.filterNotNull().map { extractItem(it, context) }
            this is Array<*> -> this.filterNotNull().map { extractItem(it, context) }
            else -> extractItem(this, context)
        }
    }

    private fun extractItem(item: Any, context: ExtractingContext): MutableMap<String, Any> {
        return (DetailExtractor.findExtractor(item::class.java)
                ?: ReflectionExtractor(true))
                .fillValues(item, context)
    }

    protected fun Any?.interfaces(): String {
        if (this == null) {
            return ""
        }
        val result = mutableSetOf<String>()
        var clazz: Class<*>? = this::class.java
        while (clazz != null) {
            result.addAll(clazz.interfaces.map { it.simpleName })
            clazz = clazz.superclass
        }
        return result.sorted().joinToString(", ")
    }
}
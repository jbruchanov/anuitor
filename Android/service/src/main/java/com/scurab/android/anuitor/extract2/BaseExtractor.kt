package com.scurab.android.anuitor.extract2

abstract class BaseExtractor {

    abstract val parent: String?

    open fun fillValues(item: Any, context: ExtractingContext): MutableMap<String, Any> {
        if (!context.data.containsKey("Type")) {
            context.put("Inheritance", 0, item) { inheritance() }
            context.put("Type", 0, item) { item.javaClass.name }
            context.put("StringValue", 0, item) { this.toString() }
            context.put("Extractor", 0, this.javaClass.name) { this.toString() }
        }
        onFillValues(item, context)

        //recursively follow inheritance
        parent?.let {
            DetailExtractor.findExtractor(Class.forName(it))
                    ?.fillValues(item, context)
        }
        return context.data
    }

    protected abstract fun onFillValues(item: Any, context: ExtractingContext)

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
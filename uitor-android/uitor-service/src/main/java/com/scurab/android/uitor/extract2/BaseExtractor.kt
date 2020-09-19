package com.scurab.android.uitor.extract2

private const val CYCLE_PARENT_CHECK = "_cycleParentCheck"
// necessary for child fragments so FragmentExtractor might be used multiple times
// Having (CYCLE_DEPTH_CHECK + 1) child fragments depth will throw an exception
private const val CYCLE_DEPTH_CHECK = 10

/**
 * Base class for all extractors.
 * Pre-fills most of important properties
 */
abstract class BaseExtractor {

    /**
     * Define a "parent" class in terms of extracting for your extractor
     * For example an extractor for [android.view.ViewGroup] would have [android.view.View]
     */
    abstract val parent: Class<*>?

    internal open fun fillValues(item: Any, context: ExtractingContext): MutableMap<String, Any> {
        if (!context.data.containsKey("Type")) {
            context.put("Inheritance", 0, item) { inheritance() }
            context.put("Type", 0, item) { item.javaClass.name }
            context.put("StringValue", 0, item) { this.toString() }
            context.put("Extractor", 0, this.javaClass.name) { this.toString() }
        }
        onFillValues(item, context)

        // recursively follow inheritance
        parent?.let {
            cycleCheck(context)
            DetailExtractor.findExtractor(it)?.run { fillValues(item, context) }
                ?: throw IllegalArgumentException("Not found extractor for class: ${it.name}")
            removeCycleTag(context)
        }
        return context.data
    }

    private fun cycleCheck(context: ExtractingContext) {
        if (!context.contextData.containsKey(CYCLE_PARENT_CHECK)) {
            context.contextData[CYCLE_PARENT_CHECK] = mutableMapOf<Class<*>, Int>()
        }
        parent?.let { parent ->
            @Suppress("UNCHECKED_CAST")
            (context.contextData[CYCLE_PARENT_CHECK] as MutableMap<Class<*>, Int>).also {
                it[parent] = 0
                val counter = it[parent] as Int
                it[parent] = counter + 1
                if (counter > CYCLE_DEPTH_CHECK) {
                    throw IllegalStateException(
                        "Parent class extraction cycle detected!\n" +
                            "Extractor:'${this.javaClass.name}' is trying to use parent:'${parent.name}'\n" +
                            "This parent has been already used for extracting $counter times.\n" +
                            "Update your extractor to use correct parent!"
                    )
                }
            }
        }
    }

    protected abstract fun onFillValues(item: Any, context: ExtractingContext)

    protected fun Any?.extract(context: ExtractingContext): Any {
        if (this == null) {
            return "null"
        }
        return when (this) {
            is Iterable<*> -> this.filterNotNull().map {
                extractItem(it, context)
            }
            is Array<*> -> this.filterNotNull().map {
                extractItem(it, context)
            }
            else -> extractItem(this, context)
        }
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

    private fun removeCycleTag(context: ExtractingContext) {
        parent?.let { parent ->
            @Suppress("UNCHECKED_CAST")
            (context.contextData[CYCLE_PARENT_CHECK] as MutableMap<Class<*>, Int>).let {
                it[parent] = (it[parent] as Int) - 1
            }
        }
    }

    private fun extractItem(item: Any, context: ExtractingContext): MutableMap<String, Any> {
        return (
            DetailExtractor.findExtractor(item::class.java)
                ?: ReflectionExtractor(true)
            )
            .fillValues(
                item,
                ExtractingContext(contextData = context.contextData, depth = context.depth + 1)
            )
    }
}

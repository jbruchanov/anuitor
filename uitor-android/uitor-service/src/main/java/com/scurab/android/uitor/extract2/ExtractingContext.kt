package com.scurab.android.uitor.extract2

import android.graphics.drawable.Drawable
import android.os.Build
import java.util.TreeMap

/**
 * An extracting token for each particular element
 */
class ExtractingContext(
    /**
     * Data what are suppose to be for a web client
     */
    val data: MutableMap<String, Any> = TreeMap(),
    /**
     * Context data for actual extracting,
     * Not delivered to web client
     */
    val contextData: MutableMap<String, Any> = TreeMap(),
    /**
     * Tracking depth of stack during extracting.
     * Important to avoid any infinite loops in potential references
     */
    var depth: Int = 0,
    /**
     * A field to track all references what are part of extracting to
     * avoid infinite loops
     */
    val cycleHandler: MutableSet<Any> = mutableSetOf()
) {

    constructor(data: MutableMap<String, Any>) : this(data, mutableMapOf(), 0, mutableSetOf())

    /**
     * Put an item into dataset. Swallowing an exception if thrown.
     * [name] a name of the property
     * [minApi] minimum supported API for the property
     * [convertToString] convert value to a string, call a [toString] on the reference
     * [codeBlock] lambda to provide a extracted property
     */
    fun <T> put(
        name: String,
        minApi: Int,
        item: T,
        convertToString: Boolean = true,
        codeBlock: T.() -> Any?
    ) {
        if (Build.VERSION.SDK_INT >= minApi) {
            try {
                var result: Any? = codeBlock(item)?.verbosed()
                if (convertToString) {
                    result = if (result?.isArray() == true) {
                        result.toArrayString()
                    } else {
                        result?.toString()
                    }
                }
                @Suppress("UNCHECKED_CAST")
                if (item is Drawable) {
                    (result as? Map<out String, Any>)?.let { r ->
                        r.forEach { (key, v) ->
                            data["${name}_$key"] = v
                        }
                        result = result.toString()
                    }
                }
                data[name] = result ?: "null"
            } catch (e: Throwable) {
                data[name] = e.message ?: "Null error message"
            }
        }
    }

    fun <T> put(name: String, item: T) {
        data[name] = item ?: "null"
    }

    private fun Any.verbosed(): Any {
        return when {
            this == Int.MAX_VALUE -> "$this (Int.MAX_VALUE)"
            this == Int.MIN_VALUE -> "$this (Int.MIN_VALUE)"
            this == Long.MIN_VALUE -> "$this (Long.MIN_VALUE)"
            this == Long.MAX_VALUE -> "$this (Long.MIN_VALUE)"
            else -> this
        }
    }
}

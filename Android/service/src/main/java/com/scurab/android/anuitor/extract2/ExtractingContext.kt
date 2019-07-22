package com.scurab.android.anuitor.extract2

import android.graphics.drawable.Drawable
import android.os.Build
import java.util.*

data class ExtractingContext(
        val data: MutableMap<String, Any> = TreeMap(),
        val contextData: MutableMap<String, Any> = TreeMap(),
        var depth: Int = 0,
        val cycleHandler: MutableSet<Any> = mutableSetOf()
) {

    constructor(data: MutableMap<String, Any>) : this(data, mutableMapOf(), 0, mutableSetOf())

    fun <T> put(name: String, minApi: Int, item: T, convertToString: Boolean = true, codeBlock: T.() -> Any?) {
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
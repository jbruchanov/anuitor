package com.scurab.android.anuitor.extract2

import android.graphics.drawable.Drawable
import android.os.Build

abstract class BaseExtractor {

    open fun fillValues(item: Any, data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?, depth: Int): MutableMap<String, Any> {
        return onFillValues(item, data, contextData, depth)
    }

    protected open fun onFillValues(item: Any, data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?, depth: Int): MutableMap<String, Any> {
        data.put("Inheritance", 0, item) { inheritance() }
        data.put("Type", 0, item) { item.javaClass.name }
        data.put("StringValue", 0, item) { this.toString() }
        return data
    }

    protected inline fun <T> MutableMap<String, Any>.put(name: String, minApi: Int, item: T, convertToString: Boolean = true, function: T.() -> Any?) {
        if (Build.VERSION.SDK_INT >= minApi) {
            try {
                var result: Any? = function(item)
                if (convertToString) {
                    result = result?.toString()
                }
                @Suppress("UNCHECKED_CAST")
                if (item is Drawable) {
                    (result as? Map<out String, Any>)?.let { r ->
                        r.forEach { (key, v) ->
                            put("${name}_$key", v)
                        }
                        result = result.toString()
                    }
                }
                put(name, result ?: "null")
            } catch (e: Throwable) {
                put(name, e.message ?: "Null error message")
            }
        }
    }

    protected fun Any?.extract(depth: Int): Any {
        if (this == null) {
            return "null"
        }
        return (DetailExtractor.findExtractor(this::class.java) ?: ReflectionExtractor(true))
                .fillValues(this, mutableMapOf(), mutableMapOf(), depth + 1)
    }
}
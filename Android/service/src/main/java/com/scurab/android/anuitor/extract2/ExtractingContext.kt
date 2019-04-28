package com.scurab.android.anuitor.extract2

import android.graphics.drawable.Drawable
import android.os.Build

data class ExtractingContext(
        val data: MutableMap<String, Any> = mutableMapOf(),
        val contextData: MutableMap<String, Any> = mutableMapOf(),
        var depth: Int = 0,
        val cycleHandler: MutableSet<Any> = mutableSetOf()
) {

    constructor(data: MutableMap<String, Any>) : this(data, mutableMapOf(), 0, mutableSetOf())

    fun <T> put(name: String, minApi: Int, item: T, convertToString: Boolean = true, codeBlock: T.() -> Any?) {
        if (Build.VERSION.SDK_INT >= minApi) {
            try {
                var result: Any? = codeBlock(item)
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
}
package com.scurab.android.anuitor.tools

class StringUtils {

    fun String?.replaceNull(value: String): String {
        return valueIfNull(this, value)
    }

    companion object {
        @JvmStatic
        fun valueIfNull(value: String?, default: String): String {
            return value ?: default
        }
    }
}
package com.scurab.android.uitor.extract2.translator

interface Translator {
    fun translate(value: Int): String

    fun translate(values: IntArray): String {
        return values.joinToString(", ") { translate(it) }
    }
}

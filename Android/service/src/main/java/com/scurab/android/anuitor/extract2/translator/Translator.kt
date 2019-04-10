package com.scurab.android.anuitor.extract2.translator

interface Translator {
    fun translate(value: Int): String

    fun translate(values: IntArray): String {
        return values.joinToString(", ") { translate(it) }
    }
}
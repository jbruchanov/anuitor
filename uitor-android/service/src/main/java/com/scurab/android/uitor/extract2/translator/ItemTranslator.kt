package com.scurab.android.uitor.extract2.translator

import com.scurab.android.uitor.extract2.TranslatorName
import com.scurab.android.uitor.extract2.stripLastChars

class ItemTranslator(private val name: TranslatorName, private val binary: Boolean) : Translator {
    private val mapping = mutableMapOf<Int, String>()
    private val orderedItems = mutableListOf<Pair<Int, String>>()

    private fun add(item: Pair<Int, String>) {
        val (key, name) = item
        mapping[key] = name
        orderedItems.add(item)
    }

    operator fun Pair<Int, String>.unaryPlus() {
        add(this)
    }

    override fun translate(value: Int): String {
        return if (binary) binaryTranslate(value)
        else unaryTranslate(value)
    }

    private fun binaryTranslate(value: Int): String {
        val sb = StringBuilder()
        var subValue = value
        orderedItems.forEach { (bit, name) ->
            if ((bit and value) == bit) {
                sb.append("$name|")
            }
            subValue = subValue and bit.inv()
            if(subValue == 0) {
                return@forEach
            }
        }
        if (subValue != 0) {
            sb.append(subValue.toString(2))
        }
        return sb.stripLastChars(1).toString()
    }

    private fun unaryTranslate(value: Int): String {
        return mapping[value] ?: "$name undefined for:'$value'"
    }
}
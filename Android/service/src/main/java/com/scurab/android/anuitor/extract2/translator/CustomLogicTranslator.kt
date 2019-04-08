package com.scurab.android.anuitor.extract2.translator

class CustomLogicTranslator(private val function: (Int) -> String) : Translator {
    override fun translate(value: Int): String {
        return function(value)
    }
}
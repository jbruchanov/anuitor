package com.scurab.android.anuitor.extract2

import android.view.Gravity
import android.view.View
import org.junit.Assert.*
import org.junit.Test

class ItemTranslatorTest {


    @Test
    fun testUnaryTranslation() {
        val translator = Translators[TranslatorName.Visibility]
        assertEquals("VISIBLE", translator.translate(View.VISIBLE))
        assertEquals("INVISIBLE", translator.translate(View.INVISIBLE))
        assertEquals("GONE", translator.translate(View.GONE))
        assertEquals("${TranslatorName.Visibility} undefined for:'1'", translator.translate(1))
    }

    @Test
    fun testBinaryTranslation() {
        val translator = Translators[TranslatorName.Gravity]
        assertEquals("TOP|CENTER", translator.translate(Gravity.TOP or Gravity.CENTER))
        assertEquals("LEFT|CENTER", translator.translate(Gravity.LEFT or Gravity.CENTER_VERTICAL))
        assertEquals("FILL_HORIZONTAL|CENTER_HORIZONTAL", translator.translate(Gravity.LEFT or Gravity.RIGHT))
    }
}
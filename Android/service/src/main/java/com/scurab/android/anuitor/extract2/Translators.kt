package com.scurab.android.anuitor.extract2

import android.os.Build
import android.text.InputType
import android.text.util.Linkify
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.gridlayout.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.scurab.android.anuitor.extract2.translator.CustomLogicTranslator
import com.scurab.android.anuitor.extract2.translator.ItemTranslator
import com.scurab.android.anuitor.extract2.translator.Translator
import com.scurab.android.anuitor.hierarchy.IdsHelper
import com.scurab.android.anuitor.tools.atLeastApi

enum class TranslatorName {
    Visibility,
    Gravity,
    LayoutParams,
    ImportantForA11Y,
    LayoutDirection,
    ChoiceMode,
    DrawerLockMode,
    GridLayoutOrientation,
    LinearLayoutOrientation,
    LinearLayoutShowDividers,
    RecyclerViewScrollState,
    LinkMask,
    InputType,
    ViewGroupLayoutMode,
    ViewLayerType,
    TabLayoutTabMode,
    DrawableState,
    FragmentState,
    LayoutSize
}


object Translators {
    private val items = mutableMapOf<TranslatorName, Translator>()

    init {
        itemTranslator(TranslatorName.ViewLayerType) {
            +(View.LAYER_TYPE_HARDWARE to "HW")
            +(View.LAYER_TYPE_SOFTWARE to "SW")
            +(View.LAYER_TYPE_NONE to "NONE")
        }

        itemTranslator(TranslatorName.Visibility) {
            +(View.VISIBLE to "VISIBLE")
            +(View.INVISIBLE to "INVISIBLE")
            +(View.GONE to "GONE")
        }

        itemTranslator(TranslatorName.LayoutParams) {
            +(ViewGroup.LayoutParams.MATCH_PARENT to "match_parent")
            +(ViewGroup.LayoutParams.WRAP_CONTENT to "wrap_content")
        }

        atLeastApi(Build.VERSION_CODES.JELLY_BEAN) {
            itemTranslator(TranslatorName.ImportantForA11Y) {
                +(View.IMPORTANT_FOR_ACCESSIBILITY_YES to "YES")
                +(View.IMPORTANT_FOR_ACCESSIBILITY_NO to "NO")
                +(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO to "AUTO")
                +(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS to "NO_HIDE_DESCENDANTS")
            }
        }

        atLeastApi(Build.VERSION_CODES.JELLY_BEAN_MR1) {
            itemTranslator(TranslatorName.LayoutDirection) {
                +(View.LAYOUT_DIRECTION_INHERIT to "INHERIT")
                +(View.LAYOUT_DIRECTION_LOCALE to "LOCALE")
                +(View.LAYOUT_DIRECTION_LTR to "LTR")
                +(View.LAYOUT_DIRECTION_RTL to "RTL")
            }
        }

        itemTranslator(TranslatorName.ChoiceMode) {
            +(AbsListView.CHOICE_MODE_NONE to "NONE")
            +(AbsListView.CHOICE_MODE_SINGLE to "SINGLE")
            +(AbsListView.CHOICE_MODE_MULTIPLE to "MULTIPLE")
            +(AbsListView.CHOICE_MODE_MULTIPLE_MODAL to "MUTLIPLE_MODAL")
        }

        customTranslator(TranslatorName.LayoutSize) { size ->
            when (size) {
                ViewGroup.LayoutParams.MATCH_PARENT -> "match_parent"
                ViewGroup.LayoutParams.WRAP_CONTENT -> "wrap_content"
                else -> size.toString()
            }
        }

        itemTranslator(TranslatorName.FragmentState) {
            +(-1 to "INVALID_STATE")
            +(0 to "INITIALIZING")
            +(1 to "CREATED")
            +(2 to "ACTIVITY_CREATED")
            +(3 to "STOPPED")
            +(4 to "STARTED")
            +(5 to "RESUMED")
        }

        customTranslator(TranslatorName.DrawableState) { state ->
            if(state == 0) {
                "default"
            } else {
                IdsHelper.getNameForId(state)
            }
        }

        customTranslator(TranslatorName.Gravity) { g ->
            StringBuilder().apply {
                if (g has Gravity.FILL) {
                    plus("FILL")
                } else {
                    if (g has Gravity.FILL_VERTICAL) {
                        plus("FILL_VERTICAL")
                    } else {
                        plus("TOP", g has Gravity.TOP)
                        plus("BOTTOM", g has Gravity.BOTTOM)
                    }
                    if (g has Gravity.FILL_HORIZONTAL) {
                        plus("FILL_HORIZONTAL")
                    } else {
                        if (g has Gravity.START) {
                            plus("START")
                        } else if (g has Gravity.LEFT) {
                            plus("LEFT")
                        }
                        if (g has Gravity.END) {
                            plus("END")
                        } else if (g has Gravity.RIGHT) {
                            plus("RIGHT")
                        }
                    }
                }
                if (g has Gravity.CENTER) {
                    plus("CENTER")
                } else {
                    plus("CENTER_VERTICAL", g has Gravity.CENTER_VERTICAL)
                    plus("CENTER_HORIZONTAL", g has Gravity.CENTER_HORIZONTAL)
                }
                plus("NO GRAVITY", isEmpty())
                plus("DISPLAY_CLIP_VERTICAL", g has Gravity.DISPLAY_CLIP_VERTICAL)
                plus("DISPLAY_CLIP_HORIZONTAL", g has Gravity.DISPLAY_CLIP_HORIZONTAL)
                deleteCharAt(length - 1)
            }.toString()
        }

        itemTranslator(TranslatorName.LinearLayoutOrientation) {
            +(LinearLayout.VERTICAL to "VERTICAL")
            +(LinearLayout.HORIZONTAL to "HORIZONTAL")
        }

        binaryIntTranslator(TranslatorName.LinearLayoutShowDividers) {
            +(0 to "NONE")
            +(LinearLayout.SHOW_DIVIDER_BEGINNING to "BEGGINNING")
            +(LinearLayout.SHOW_DIVIDER_END to "END")
            +(LinearLayout.SHOW_DIVIDER_MIDDLE to "MIDDLE")
        }

        customTranslator(TranslatorName.LinkMask) { mask ->
            val linkMasks = intArrayOf(Linkify.EMAIL_ADDRESSES, Linkify.MAP_ADDRESSES, Linkify.PHONE_NUMBERS, Linkify.WEB_URLS)
            val linkMaskValues = arrayOf("EMAIL_ADDRESSES", "MAP_ADDRESSES", "PHONE_NUMBERS", "WEB_URLS")
            when (mask) {
                Linkify.ALL -> "ALL"
                0 -> "NONE"
                else -> {
                    val sb = StringBuilder()
                    for (i in linkMasks.indices) {
                        if (mask and linkMasks[i] == linkMasks[i]) {
                            sb.append(linkMaskValues[i]).append("|")
                        }
                    }
                    sb.stripLastChars(1).toString()
                }
            }
        }

        customTranslator(TranslatorName.InputType) { inputType ->
            if (inputType == InputType.TYPE_NUMBER_VARIATION_NORMAL) {//0
                "TYPE_NUMBER_VARIATION_NORMAL"
            } else {
                val sb = StringBuilder()
                val fields = InputType::class.java.fields
                try {
                    for (field in fields) {
                        if (field.isAccessible && field.type == Int::class.javaPrimitiveType) {
                            val name = field.name
                            val value = field.getInt(null)//
                            if (inputType and value == value) {
                                sb.append(name).append("|")
                            }
                        }
                    }
                    val length = sb.length
                    if (length > 0) {
                        sb.setLength(length - 1)
                    }
                } catch (e: IllegalAccessException) {
                    sb.setLength(0)
                    sb.append(e.message)
                    e.printStackTrace()
                }
                sb.toString()
            }
        }

        atLeastApi(Build.VERSION_CODES.JELLY_BEAN_MR2) {
            itemTranslator(TranslatorName.ViewGroupLayoutMode) {
                +(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS to "LAYOUT_MODE_CLIP_BOUNDS")
                +(ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS to "LAYOUT_MODE_OPTICAL_BOUNDS")
            }
        }

        //androidx
        itemTranslator(TranslatorName.DrawerLockMode) {
            +(DrawerLayout.LOCK_MODE_UNLOCKED to "LOCK_MODE_UNLOCKED")
            +(DrawerLayout.LOCK_MODE_LOCKED_OPEN to "LOCK_MODE_LOCKED_OPEN")
            +(DrawerLayout.LOCK_MODE_LOCKED_CLOSED to "LOCK_MODE_LOCKED_CLOSED")
        }

        //
        itemTranslator(TranslatorName.DrawerLockMode) {
            +(GridLayout.VERTICAL to "VERTICAL")
            +(GridLayout.HORIZONTAL to "HORIZONTAL")
        }

        itemTranslator(TranslatorName.RecyclerViewScrollState) {
            +(RecyclerView.SCROLL_STATE_DRAGGING to "SCROLL_STATE_DRAGGING")
            +(RecyclerView.SCROLL_STATE_IDLE to "SCROLL_STATE_IDLE")
            +(RecyclerView.SCROLL_STATE_SETTLING to "SCROLL_STATE_SETTLING")
        }

        itemTranslator(TranslatorName.TabLayoutTabMode) {
            +(TabLayout.MODE_FIXED to "MODE_FIXED")
            +(TabLayout.MODE_SCROLLABLE to "MODE_SCROLLABLE")
        }
    }

    private fun itemTranslator(name: TranslatorName, function: ItemTranslator.() -> Unit) {
        val itemTranslator = ItemTranslator(name, false).apply(function)
        items[name] = itemTranslator
    }

    private fun binaryIntTranslator(name: TranslatorName, function: ItemTranslator.() -> Unit) {
        val itemTranslator = ItemTranslator(name, true).apply(function)
        items[name] = itemTranslator
    }

    private fun customTranslator(name : TranslatorName, function:  (Int) -> String) {
        val itemTranslator = CustomLogicTranslator(function)
        items[name] = itemTranslator
    }

    operator fun get(name: TranslatorName): Translator {
        return items[name] ?: throw IllegalStateException("Undefined $name")
    }
}

private fun StringBuilder.plus(s: String): java.lang.StringBuilder {
    return plus(s, true)
}

private fun StringBuilder.plus(s: String, takeIf: Boolean): java.lang.StringBuilder {
    if (takeIf) {
        append(s).append('|')
    }
    return this
}


private infix fun Int.has(bit : Int) : Boolean {
    return (this and bit) == bit
}
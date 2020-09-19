package com.scurab.android.uitor.extract2

import android.graphics.Typeface
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.text.util.Linkify
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.Barrier
import androidx.drawerlayout.widget.DrawerLayout
import androidx.gridlayout.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.scurab.android.uitor.extract2.translator.CustomLogicTranslator
import com.scurab.android.uitor.extract2.translator.ItemTranslator
import com.scurab.android.uitor.extract2.translator.Translator
import com.scurab.android.uitor.hierarchy.IdsHelper
import com.scurab.android.uitor.tools.atLeastApi
import kotlin.math.absoluteValue

enum class TranslatorName {
    Visibility,
    Gravity,
    LayoutParams,
    ImportantForA11Y,
    ViewLayoutDirection,
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
    LayoutSize,
    Shape,
    Ellipsize,
    TextStyle,
    ScaleType,
    CoordinatorLayoutBarrierType,
    ViewPager2ScrollState,
    ViewOverScroll,
    ViewImportantForAutoFill,
    ViewFocusable,
    ViewTextAlignment,
    ViewTextDirection,
    ViewScrollBarType,
    ViewGroupDescendantFocusability,
    WebSettingsMixedContentMode,
    WebSettingsForceDark,
    ;
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

        customTranslator(TranslatorName.LayoutParams) {
            when (it) {
                ViewGroup.LayoutParams.MATCH_PARENT -> "match_parent"
                ViewGroup.LayoutParams.WRAP_CONTENT -> "wrap_content"
                else -> it.toString()
            }
        }

        atLeastApi(Build.VERSION_CODES.JELLY_BEAN) {
            itemTranslator(TranslatorName.ImportantForA11Y) {
                +(View.IMPORTANT_FOR_ACCESSIBILITY_YES to "YES")
                +(View.IMPORTANT_FOR_ACCESSIBILITY_NO to "NO")
                +(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO to "AUTO")
                atLeastApi(Build.VERSION_CODES.KITKAT) {
                    +(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS to "NO_HIDE_DESCENDANTS")
                }
            }
        }

        atLeastApi(Build.VERSION_CODES.JELLY_BEAN_MR1) {
            itemTranslator(TranslatorName.ViewLayoutDirection) {
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
            if (state == 0) {
                "default"
            } else {
                val name = IdsHelper.getNameForId(state.absoluteValue)
                val isEnabled = state > 0
                "$name=$isEnabled"
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

        itemTranslator(TranslatorName.GridLayoutOrientation) {
            +(GridLayout.VERTICAL to "VERTICAL")
            +(GridLayout.HORIZONTAL to "HORIZONTAL")
        }

        itemTranslator(TranslatorName.Shape) {
            +(0 to "rectangle")
            +(1 to "oval")
            +(2 to "line")
            +(3 to "ring")
        }

        customTranslator(TranslatorName.Ellipsize) {
            TextUtils.TruncateAt.values()[it].toString()
        }

        itemTranslator(TranslatorName.TextStyle) {
            +(Typeface.NORMAL to "NORMAL")
            +(Typeface.BOLD to "BOLD")
            +(Typeface.ITALIC to "ITALIC")
            +(Typeface.BOLD_ITALIC to "BOLD_ITALIC")
        }

        customTranslator(TranslatorName.ScaleType) {
            ImageView.ScaleType.values()[it].toString()
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
            if (inputType == InputType.TYPE_NUMBER_VARIATION_NORMAL) { // 0
                "TYPE_NUMBER_VARIATION_NORMAL"
            } else {
                val sb = StringBuilder()
                val fields = InputType::class.java.fields
                try {
                    for (field in fields) {
                        if (field.isAccessible && field.type == Int::class.javaPrimitiveType) {
                            val name = field.name
                            val value = field.getInt(null) //
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

        itemTranslator(TranslatorName.ViewOverScroll) {
            +(View.OVER_SCROLL_NEVER to "OVER_SCROLL_NEVER")
            +(View.OVER_SCROLL_ALWAYS to "OVER_SCROLL_ALWAYS")
            +(View.OVER_SCROLL_IF_CONTENT_SCROLLS to "OVER_SCROLL_IF_CONTENT_SCROLLS")
        }

        atLeastApi(Build.VERSION_CODES.LOLLIPOP) {
            itemTranslator(TranslatorName.WebSettingsMixedContentMode) {
                +(WebSettings.MIXED_CONTENT_NEVER_ALLOW to "MIXED_CONTENT_NEVER_ALLOW")
                +(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW to "MIXED_CONTENT_ALWAYS_ALLOW")
                +(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE to "MIXED_CONTENT_COMPATIBILITY_MODE")
            }
        }

        atLeastApi(Build.VERSION_CODES.Q) {
            itemTranslator(TranslatorName.WebSettingsForceDark) {
                +(WebSettings.FORCE_DARK_AUTO to "FORCE_DARK_AUTO")
                +(WebSettings.FORCE_DARK_ON to "FORCE_DARK_ON")
                +(WebSettings.FORCE_DARK_OFF to "FORCE_DARK_OFF")
            }
        }

        atLeastApi(Build.VERSION_CODES.O) {
            itemTranslator(TranslatorName.ViewImportantForAutoFill) {
                +(View.IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS to "IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS")
                +(View.IMPORTANT_FOR_AUTOFILL_YES to "IMPORTANT_FOR_AUTOFILL_YES")
                +(View.IMPORTANT_FOR_AUTOFILL_AUTO to "IMPORTANT_FOR_AUTOFILL_AUTO")
                +(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS to "IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS")
                +(View.IMPORTANT_FOR_AUTOFILL_NO to "IMPORTANT_FOR_AUTOFILL_NO")
            }
        }

        itemTranslator(TranslatorName.ViewFocusable) {
            +(View.FOCUSABLES_TOUCH_MODE to "FOCUSABLES_TOUCH_MODE")
            atLeastApi(Build.VERSION_CODES.O) {
                +(View.FOCUSABLE to "FOCUSABLE")
                +(View.FOCUSABLE_AUTO to "FOCUSABLE_AUTO")
                +(View.NOT_FOCUSABLE to "NOT_FOCUSABLE")
            }
        }

        atLeastApi(Build.VERSION_CODES.KITKAT) {
            itemTranslator(TranslatorName.ViewTextAlignment) {
                +(View.TEXT_ALIGNMENT_INHERIT to "TEXT_ALIGNMENT_INHERIT")
                +(View.TEXT_ALIGNMENT_GRAVITY to "TEXT_ALIGNMENT_GRAVITY")
                +(View.TEXT_ALIGNMENT_TEXT_START to "TEXT_ALIGNMENT_TEXT_START")
                +(View.TEXT_ALIGNMENT_TEXT_END to "TEXT_ALIGNMENT_TEXT_END")
                +(View.TEXT_ALIGNMENT_CENTER to "TEXT_ALIGNMENT_CENTER")
                +(View.TEXT_ALIGNMENT_VIEW_START to "TEXT_ALIGNMENT_VIEW_START")
                +(View.TEXT_ALIGNMENT_VIEW_END to "TEXT_ALIGNMENT_VIEW_END")
            }
        }

        atLeastApi(Build.VERSION_CODES.KITKAT) {
            itemTranslator(TranslatorName.ViewTextDirection) {
                +(View.TEXT_DIRECTION_INHERIT to "TEXT_DIRECTION_INHERIT")
                +(View.TEXT_DIRECTION_FIRST_STRONG to "TEXT_DIRECTION_FIRST_STRONG")
                +(View.TEXT_DIRECTION_ANY_RTL to "TEXT_DIRECTION_ANY_RTL")
                +(View.TEXT_DIRECTION_LTR to "TEXT_DIRECTION_LTR")
                +(View.TEXT_DIRECTION_RTL to "TEXT_DIRECTION_RTL")
                +(View.TEXT_DIRECTION_LOCALE to "TEXT_DIRECTION_LOCALE")
                atLeastApi(Build.VERSION_CODES.M) {
                    +(View.TEXT_DIRECTION_FIRST_STRONG_LTR to "TEXT_DIRECTION_FIRST_STRONG_LTR")
                    +(View.TEXT_DIRECTION_FIRST_STRONG_RTL to "TEXT_DIRECTION_FIRST_STRONG_RTL")
                }
            }
        }

        itemTranslator(TranslatorName.ViewScrollBarType) {
            +(View.SCROLLBARS_INSIDE_OVERLAY to "SCROLLBARS_INSIDE_OVERLAY")
            +(View.SCROLLBARS_INSIDE_INSET to "SCROLLBARS_INSIDE_INSET")
            +(View.SCROLLBARS_OUTSIDE_OVERLAY to "SCROLLBARS_OUTSIDE_OVERLAY")
            +(View.SCROLLBARS_OUTSIDE_INSET to "SCROLLBARS_OUTSIDE_INSET")
        }

        itemTranslator(TranslatorName.ViewGroupDescendantFocusability) {
            +(ViewGroup.FOCUS_BEFORE_DESCENDANTS to "FOCUS_BEFORE_DESCENDANTS")
            +(ViewGroup.FOCUS_AFTER_DESCENDANTS to "FOCUS_AFTER_DESCENDANTS")
            +(ViewGroup.FOCUS_BLOCK_DESCENDANTS to "FOCUS_BLOCK_DESCENDANTS")
        }

        // androidx
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

        itemTranslator(TranslatorName.ViewPager2ScrollState) {
            +(ViewPager2.SCROLL_STATE_DRAGGING to "SCROLL_STATE_DRAGGING")
            +(ViewPager2.SCROLL_STATE_SETTLING to "SCROLL_STATE_SETTLING")
            +(ViewPager2.SCROLL_STATE_IDLE to "SCROLL_STATE_IDLE")
        }

        itemTranslator(TranslatorName.CoordinatorLayoutBarrierType) {
            +(Barrier.LEFT to "LEFT")
            +(Barrier.RIGHT to "RIGHT")
            +(Barrier.TOP to "TOP")
            +(Barrier.BOTTOM to "BOTTOM")
            +(Barrier.START to "START")
            +(Barrier.END to "END")
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

    private fun customTranslator(name: TranslatorName, function: (Int) -> String) {
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

private infix fun Int.has(bit: Int): Boolean {
    return (this and bit) == bit
}

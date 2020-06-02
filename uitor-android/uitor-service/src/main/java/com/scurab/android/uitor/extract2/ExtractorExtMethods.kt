package com.scurab.android.uitor.extract2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.scurab.android.uitor.hierarchy.IdsHelper
import com.scurab.android.uitor.reflect.Reflector
import com.scurab.android.uitor.tools.HttpTools
import com.scurab.android.uitor.tools.atLeastApi
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

fun Int.idName() = IdsHelper.getNameForId(this)
fun View.idName() = IdsHelper.getNameForId(id)
fun IntArray.idsName() = map { IdsHelper.getNameForId(it) }
fun Int.stringColor(): String = HttpTools.getStringColor(this)

/**
 * Convert [Int] to binary form grouped by 4 bits
 *
 */
fun Int.binary(): String {
    if (this == 0) {
        return "0"
    }

    val sb = StringBuilder(this.toString(2))
    while (sb.length < 32) {
        sb.insert(0, "0")
    }
    (1 until 8).forEach { i ->
        sb.insert((4 * i) + (i - 1), "-")
    }
    return sb.toString()
}

/**
 * Take list of classes representing inheritance of the object
 */
fun Any.inheritance(): String {
    val sb = StringBuilder()
    var clz: Class<*>? = javaClass
    while (clz != null) {
        sb.append(clz.name).append(" > ")
        clz = clz.superclass
    }
    val len = sb.length
    if (len > 3) {
        sb.setLength(len - 3)
    }
    return sb.toString()
}

/**
 * Strip last [amount] of chars if possible.
 * Otherwise return original stringbuilder
 */
fun StringBuilder.stripLastChars(amount: Int): StringBuilder {
    if (length > amount) {
        setLength(length - amount)
    }
    return this
}

/**
 * Extract [Bundle] as key/value collection
 */
fun Bundle.extract(context: ExtractingContext) {
    keySet().forEach { key ->
        context.data[key] = get(key) ?: "null"
    }
}

fun IntArray.extractRelativeLayoutRules(context: ExtractingContext) {
    val relativeLayoutRules = arrayOf("leftOf", "rightOf", "above", "below", "alignBaseline",
            "alignLeft", "alignTop", "alignRight", "alignBottom", "alignParentLeft", "alignParentTop", "alignParentRight",
            "alignParentBottom", "center", "centerHorizontal", "centerVertical")

    fun relativeLayoutParamRuleName(index: Int): String {
        return "layoutParams_" + if (index < relativeLayoutRules.size) relativeLayoutRules[index] else index
    }

    forEachIndexed { i, rule ->
        if (rule != 0) {
            context.data[relativeLayoutParamRuleName(i)] =
                    when (rule) {
                        RelativeLayout.TRUE -> true
                        0 -> "false/NO_ID"
                        else -> IdsHelper.getNameForId(rule)
                    }
        }
    }
}

fun IntArray.drawableStates(): String {
    val sb = StringBuilder()
    var i = 0
    val n = this.size
    while (i < n) {
        if (sb.isNotEmpty()) {
            sb.append(", ")
        }
        if (this[i] == 0) {
            sb.append("default")
        } else {
            sb.append(IdsHelper.getNameForId(this[i]))
        }
        i++
    }
    return sb.toString()
}

fun View.getHitRect(): Rect {
    val rect = Rect()
    getHitRect(rect)
    return rect
}

fun Rect.stringSizes(): String {
    return StringBuilder()
            .append(left).append(",")
            .append(top).append(",")
            .append(right).append(",")
            .append(bottom).toString()
}

/**
 * Get a value using reflection
 */
fun Any.reflection(name: String): Any? {
    return try {
        Reflector.callMethodByReflection<Any>(javaClass, this, name)
    } catch (e: Throwable) {
        return try {
            Reflector.getFieldValue<Any>(this, name)
        } catch (e: Throwable) {
            "Unable to find method/field: '$name'"
        }
    }
}

/**
 * Get an [Int] value using reflection
 */
fun Any.reflectionInt(name: String): Int {
    return reflection(name) as Int? ?: Int.MIN_VALUE
}

/**
 * Escape most commonly used special chars.
 * For example '\n' => '\\n'
 */
fun CharSequence.escaped(): String {
    val sb = StringBuilder(length)
    for (c in toString().toCharArray()) {
        if (Character.isWhitespace(c)) {
            when (c) {
                ' ' -> sb.append(" ")
                '\n' -> sb.append("\\n")
                '\t' -> sb.append("\\t")
                '\r' -> sb.append("\\r")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                else -> sb.append("{0x")
                        .append(Integer.toHexString(c.toInt()))
                        .append("}")
            }
        } else {
            sb.append(c)
        }
    }
    return sb.toString()
}

/**
 * Get activity from [Context] or [ContextWrapper]
 */
fun Context.getActivity(): Activity? {
    var context: Context = this
    var activity: Activity? = context as? Activity?
    while (activity == null && context is ContextWrapper) {
        context = context.baseContext
        activity = context as? Activity
    }
    return activity
}

/**
 * Find an activity for View.
 * This might return null if the view is using [Application] as context
 */
fun View.getActivity(): Activity? {
    var activity = context.getActivity()
    if (activity == null) {
        //view might have Application context, so try to search it in whole hierarchy
        (this as? ViewGroup)?.apply {
            (0 until childCount).forEach { i ->
                activity = getChildAt(i).getActivity()
                if (activity != null) {
                    return activity
                }
            }
        }
    }
    return activity
}

fun Context.getApplication(): Application {
    return applicationContext as Application
}

fun View.components(): ViewComponents {
    val result = mutableListOf<IFragmentDelegate>()

    fun saveChildAndroidXFragments(fragment: Fragment) {
        if (fragment.view != null) {
            result.add(AndroidXFragmentDelegate(fragment))
        }
        fragment.childFragmentManager.fragments
                .filter { it.view != null }
                .forEach { f -> saveChildAndroidXFragments(f) }
    }

    return ViewComponents(context.getApplication(), getActivity()).apply {
        (activity as? FragmentActivity)?.also { activity ->
            activity.supportFragmentManager.fragments.forEach { f -> saveChildAndroidXFragments(f) }
        }
        atLeastApi(Build.VERSION_CODES.O) {
            fun saveChildFragments(fragment: android.app.Fragment) {
                if (fragment.view != null) {
                    result.add(AndroidFragmentDelegate(fragment))
                }
                fragment.childFragmentManager?.fragments
                        ?.filter { it.view != null }
                        ?.forEach { f -> saveChildFragments(f) }
            }
            activity?.fragmentManager?.fragments?.forEach { saveChildFragments(it) }
        }

        //keep child fragments closer to start so any iteration will rather pick them
        //instead of parent fragment
        fragments.addAll(result.reversed())
        fragments
                .filter { it.view != null }
                .forEach { f ->
                    fragmentsPerRootView[f.view!!] = f
                }
    }
}

/**
 * Get all defined methods on the class including parent classes
 */
fun Any.allMethods(): Collection<Method> {
    val result = mutableListOf<Method>()
    var clazz: Class<*>? = javaClass
    while (clazz != null && clazz != Object::class.java) {
        result.addAll(clazz.declaredMethods)
        clazz = clazz.superclass
    }
    return result
}

/**
 * Get All fields defined on the class including parent classes
 */
fun Any.allFields(): Collection<Field> {
    val result = mutableListOf<Field>()
    var clazz: Class<*>? = javaClass
    while (clazz != null && clazz != Object::class.java) {
        result.addAll(clazz.declaredFields)
        clazz = clazz.superclass
    }
    return result
}

/**
 * Check if the reference is an [Array]
 */
fun Any.isArray(): Boolean {
    return when (this) {
        is Array<*>,
        is IntArray,
        is LongArray,
        is ByteArray,
        is ShortArray,
        is BooleanArray,
        is CharArray -> return true
        else -> false
    }
}

fun Any.toArrayString(): String? {
    return when (this) {
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is BooleanArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is Array<*> -> Arrays.toString(this)
        else -> null
    }
}

inline fun <V> filling(v: V, action: (V) -> Unit): V {
    action(v)
    return v
}
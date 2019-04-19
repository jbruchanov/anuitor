package com.scurab.android.anuitor.extract2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.scurab.android.anuitor.hierarchy.IdsHelper
import com.scurab.android.anuitor.reflect.Reflector
import com.scurab.android.anuitor.tools.HttpTools
import com.scurab.android.anuitor.tools.atLeastApi
import com.scurab.android.anuitor.tools.ise
import java.lang.reflect.Field
import java.lang.reflect.Method

fun Int.idName() = IdsHelper.getNameForId(this)
fun View.idName() = IdsHelper.getNameForId(id)
fun Int.stringColor(): String = HttpTools.getStringColor(this)

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

fun StringBuilder.stripLastChars(amount: Int): StringBuilder {
    if (length > amount) {
        setLength(length - amount)
    }
    return this
}

fun Bundle.extract(data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?): MutableMap<String, Any> {
    keySet().forEach { key ->
        data[key] = get(key) ?: "null"
    }
    return data
}

fun IntArray.extractRelativeLayoutRules(data: MutableMap<String, Any>): MutableMap<String, Any> {
    val relativeLayoutRules = arrayOf("leftOf", "rightOf", "above", "below", "alignBaseline",
            "alignLeft", "alignTop", "alignRight", "alignBottom", "alignParentLeft", "alignParentTop", "alignParentRight",
            "alignParentBottom", "center", "centerHorizontal", "centerVertical")

    fun relativeLayoutParamRuleName(index: Int): String {
        return "layoutParams_" + if (index < relativeLayoutRules.size) relativeLayoutRules[index] else index
    }

    forEachIndexed{ i, rule ->
        if(rule != 0) {
            data[relativeLayoutParamRuleName(i)] =
                    when (rule) {
                        RelativeLayout.TRUE -> true
                        0 -> "false/NO_ID"
                        else -> IdsHelper.getNameForId(rule)
                    }
        }
    }
    return data
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

fun Any.reflectionInt(name: String): Int {
    return reflection(name) as Int? ?: Int.MIN_VALUE
}

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

fun Context.getActivity(): Activity? {
    var context: Context = this
    var activity: Activity? = context as? Activity?
    while (activity == null && context is ContextWrapper) {
        context = context.baseContext
        activity = context as? Activity
    }
    return activity
}

fun Context.getApplication(): Application {
    return applicationContext as Application
}


fun View.components() : ViewComponents {
    return ViewComponents(context.getApplication(), context.getActivity()).apply {
        (activity as? FragmentActivity)?.also {activity ->
            val xFragments: Map<View, Pair<View, IFragmentDelegate>> = activity.supportFragmentManager.fragments
                    .filter { it.view != null }
                    .map { Pair(it.view ?: ise("Null view")/*false positive, filtered*/, AndroidXFragmentDelegate(it)) }
                    .associateBy { it.first }
                    .toMutableMap()
            var allFragments = xFragments

            atLeastApi(Build.VERSION_CODES.O) {
                val fragments: Map<View, Pair<View, IFragmentDelegate>> = activity.fragmentManager.fragments
                        .filter { it.view != null }
                        .map { Pair(it.view ?: ise("Null view")/*false positive, filtered*/, AndroidFragmentDelegate(it)) }
                        .associateBy { it.first }
                allFragments = allFragments + fragments
            }
            fragments.addAll(allFragments.values.map { it.second })
            allFragments.values.forEach { (view, fragment) ->
                fragmentsPerRootView[view] = fragment
            }
        }
    }
}

fun Any.allMethods() : Collection<Method> {
    val result = mutableListOf<Method>()
    var clazz: Class<*>? = javaClass
    while(clazz != null && clazz != Object::class.java) {
        result.addAll(clazz.declaredMethods)
        clazz = clazz.superclass
    }
    return result
}

fun Any.allFields() : Collection<Field> {
    val result = mutableListOf<Field>()
    var clazz: Class<*>? = javaClass
    while(clazz != null && clazz != Object::class.java) {
        result.addAll(clazz.declaredFields)
        clazz = clazz.superclass
    }
    return result
}
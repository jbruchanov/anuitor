package com.scurab.android.anuitor.hierarchy

import android.app.Application
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import java.lang.reflect.Field

enum class RefType {
    anim, animator, array, attr,
    bool,
    color,
    dimen, drawable,
    fraction, font,
    id, integer, interpolator,
    layout,
    menu, mipmap,
    navigation,
    plurals,
    raw,
    string, style, styleable,
    transition,
    unknown,
    xml
}

object IdsHelper {
    @JvmStatic
    internal var data = mutableMapOf<String, Map<Int, String>>()
    @JvmStatic
    var RClass: Class<*>? = null
    @JvmStatic
    fun loadValues(Rclass: Class<*>) {
        if (data.isNotEmpty()) {
            data.clear()
        }
        Rclass.classes.forEach { fillClass(it, false) }
        android.R::class.java.classes.forEach { fillClass(it, true) }
        RClass = Rclass
    }

    /**
     * Returns name for particular id<br/>
     * If id is not defined {@link android.view.View#getId()} returns -1, this method returns "undefined".
     * If the id is not found (maybe android.internal stuff) null is returned.
     * @param id
     * @return
     */
    @JvmStatic
    fun getNameForId(id: Int): String {
        if (id == -1) {
            return "View.NO_ID"
        }
        return data.values
                .firstOrNull { it.contains(id) }
                ?.let { it[id] }
                //this is weird, TODO why is something missing ?
                ?: Resources.getSystem().getResourceName(id)
    }

    /**
     * Get type of particular id. [com.scurab.android.anuitor.hierarchy.IdsHelper.RefType.unknown] is returned if not found.
     * @param id
     * @return
     */
    @JvmStatic
    fun getType(id: Int): RefType {
        return data
                .filter { it.value.containsKey(id) }
                .entries.firstOrNull()
                ?.let { (type, _) -> type.split(".").lastOrNull() }
                ?.let { RefType.valueOf(it) }
                ?: RefType.unknown
    }

    /**
     * Convert whole dataset of ids into JSON.
     * If @param res is passed, dataset will contain source of xml files for drawables, layouts, colors
     * @param res optional value, can be null
     * @return
     */
    @JvmStatic
    fun getAllResources(res: Resources): Map<String, List<ResourceDTO>> {
        val tv = TypedValue()
        return data.mapValues { (group, values) ->
            val showValue = group == "R.drawable" || group == "R.layout" || group == "R.color"
            values.map { (resId, name) ->
                var location = if (showValue) {
                    res.getValue(resId, tv)
                } else null
                ResourceDTO(resId, name, location)
            }
        }
    }

    private fun fillClass(containerClass: Class<*>, vanillaAndroid: Boolean) {
        val values = mutableMapOf<Int, String>()
        val name = containerClass.simpleName
        var v = containerClass.canonicalName
                ?: throw NullPointerException("containerClass.canonicalName is null?!")
        val pckg = containerClass.`package`?.name
        if (!vanillaAndroid) {
            require(pckg != null) { "Containerclass.package:$containerClass has null name ?!" }
            v = v.replace(pckg, "").substring(1)
        }
        IdsHelper.data[v] = values
        fillFields(name, values, containerClass.fields, vanillaAndroid)
    }

    private fun fillFields(type: String, container: MutableMap<Int, String>, fields: Array<Field>, vanillaAndroid: Boolean) {
        for (field in fields) {
            field.isAccessible = true
            if (field.type == Int::class.javaPrimitiveType) {
                val name = field.name
                try {
                    val key = field.getInt(null)
                    val tag = if ("attr" == type) "?" else "@"
                    val prefix = if (vanillaAndroid) "android:$type" else type
                    container[key] = "$tag$prefix/$name"
                } catch (e: Throwable) { //this should never happen if we have setAccessible
                    continue
                }
            }
        }
    }

    private fun Resources.getValue(resId: Int, typedValue: TypedValue): CharSequence? {
        return try {
            getValue(resId, typedValue, false);
            typedValue.string
        } catch (e: Throwable) {
            //just for sure
            Log.e("IdsHelper", "Name:${getResourceName(resId)} Err:${e.message ?: "Null"}")
            e.printStackTrace()
            null
        }
    }
}

class ResourceDTO(
        val key: Int,
        val value: String,
        val contextValue: CharSequence?
)
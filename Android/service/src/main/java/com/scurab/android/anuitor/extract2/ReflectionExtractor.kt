package com.scurab.android.anuitor.extract2

import android.util.Log
import java.lang.reflect.Modifier
import java.util.regex.Pattern

private const val MAX_DEPTH = 4
class ReflectionExtractor(private val useFields: Boolean = false) : BaseExtractor() {
    companion object {
        /**
         * Fill this with regexp patterns to ignore methods
         */
        private var IGNORE_PATTERNS = arrayOf(
                Pattern.compile("add.*", 0),
                Pattern.compile("begin.*", 0),
                Pattern.compile("call.*", 0),
                Pattern.compile("clear.*", 0),
                Pattern.compile("commit.*", 0),
                Pattern.compile("create.*", 0),
                Pattern.compile("dispatch.*", 0),
                Pattern.compile("exec.*", 0),
                Pattern.compile("find.*", 0),
                Pattern.compile("gen.*", 0),
                Pattern.compile("mutate.*", 0),
                Pattern.compile("on.*", 0),
                Pattern.compile("open.*", 0),
                Pattern.compile("perform.*", 0),
                Pattern.compile("pop.*", 0),
                Pattern.compile("post.*", 0),
                Pattern.compile("request.*", 0),
                Pattern.compile("resolve.*", 0),
                Pattern.compile("save.*", 0),
                Pattern.compile("select.*", 0),
                Pattern.compile("show.*", 0),
                Pattern.compile("will.*", 0))
    }

    override fun onFillValues(item: Any, data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?, depth: Int): MutableMap<String, Any> {
        super.onFillValues(item, data, contextData, depth)
        return this.fillValues(item, data, contextData ?: mutableMapOf(), mutableSetOf(), depth)
    }

    private fun fillValues(item: Any, data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?, cycleHandler: MutableSet<Any>, depth: Int): MutableMap<String, Any> {
        if (depth >= MAX_DEPTH) {
            data["depth"] = "max:$depth reached"
            return data
        }
        item.allMethods()
                .filter { !it.name.shouldIgnore() }
                .filter { it.parameterTypes.isEmpty() /*&& it.returnType.isPrimitive*/ && it.returnType != Void.TYPE }
                .forEach { m ->
                    try {
                        m.isAccessible = true
                        m.invoke(item)?.let { v ->
                            storeItem(m.name, v, data, cycleHandler, depth)
                        }
                    } catch (e: Throwable) {
                        Log.e("Extractor", String.format("Name:%s Exception:%s", m.name, e.javaClass.simpleName))
                    }
                }

        if (useFields) {
            item.allFields()
                    .filter { !(Modifier.isStatic(it.modifiers) || it.name.startsWith("shadow$")) }
                    .forEach { f ->
                        try {
                            f.isAccessible = true
                            f.get(item)?.let { v ->
                                storeItem(f.name, v, data, cycleHandler, depth)
                            }
                        } catch (e: Throwable) {
                            Log.e("Extractor", String.format("Name:%s Exception:%s", f.name, e.javaClass.simpleName))
                        }
                    }
        }
        return data
    }

    private fun storeItem(name: String, v: Any, data: MutableMap<String, Any>, cycleHandler: MutableSet<Any>, depth: Int) {
        if (v.javaClass.isPrimitiveType()) {
            data[name] = v
        } else if (!cycleHandler.contains(v)) {
            cycleHandler.add(v)
            data[name] = v.asCollection()?.let { collection ->
                collection.map {item ->
                    item?.let {
                        (DetailExtractor.findExtractor(it.javaClass)
                                ?.fillValues(it, mutableMapOf(), data, depth + 1))
                                ?: this.fillValues(it, mutableMapOf(), data, cycleHandler, depth + 1)
                    }
                }
            } ?: (DetailExtractor.findExtractor(v.javaClass)
                    ?.fillValues(v, mutableMapOf(), data, depth + 1))
                    ?: this.fillValues(v, mutableMapOf(), data, cycleHandler, depth + 1)
        } else {
            data[name] = v.toString()
        }
    }

    private fun String.shouldIgnore(): Boolean {
        IGNORE_PATTERNS.forEach { p ->
            if (p.matcher(this).matches()) {
                return true
            }
        }
        return false
    }

    private fun Class<*>.isPrimitiveType(): Boolean {
        return this.isPrimitive or when (this) {
            Int::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaObjectType,
            Boolean::class.java,
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaObjectType,
            Short::class.java,
            Short::class.javaPrimitiveType,
            Short::class.javaObjectType,
            Char::class.java,
            Char::class.javaPrimitiveType,
            Char::class.javaObjectType,
            Byte::class.java,
            Byte::class.javaPrimitiveType,
            Byte::class.javaObjectType,
            Long::class.java,
            Long::class.javaPrimitiveType,
            Long::class.javaObjectType,
            Double::class.java,
            Double::class.javaPrimitiveType,
            Double::class.javaObjectType,
            Float::class.java,
            Float::class.javaPrimitiveType,
            Float::class.javaObjectType,
            String::class.java,
            String::class.javaPrimitiveType,
            String::class.javaObjectType -> true
            else -> false
        }
    }

    private fun Any.asCollection() : Collection<*>? {
        return when {
            this is Array<*> -> return this.toList()
            this is Collection<*> -> return this
            else -> null
        }
    }
}
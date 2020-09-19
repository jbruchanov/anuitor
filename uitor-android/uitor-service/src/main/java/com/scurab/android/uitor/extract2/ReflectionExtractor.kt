package com.scurab.android.uitor.extract2

import android.os.Handler
import android.os.MessageQueue
import android.util.Log
import java.lang.reflect.Modifier
import java.util.regex.Pattern

private const val MAX_DEPTH = 2

class ReflectionExtractor(
    private val useFields: Boolean = false,
    private val maxDepth: Int = MAX_DEPTH
) : BaseExtractor() {
    override val parent: Class<*>? = null

    companion object {
        /**
         * Fill this with regexp patterns to ignore methods
         */
        private val IGNORE_PATTERNS = arrayOf(
            Pattern.compile("add.*", 0),
            Pattern.compile("as.*", 0),
            Pattern.compile("begin.*", 0),
            Pattern.compile("call.*", 0),
            Pattern.compile("clear.*", 0),
            Pattern.compile("clone.*", 0),
            Pattern.compile("commit.*", 0),
            Pattern.compile("create.*", 0),
            Pattern.compile("dispatch.*", 0),
            Pattern.compile("exec.*", 0),
            Pattern.compile("find.*", 0),
            Pattern.compile("gen.*", 0),
            Pattern.compile("lock.*", 0),
            Pattern.compile("mutate.*", 0),
            Pattern.compile("on.*", 0),
            Pattern.compile("open.*", 0),
            Pattern.compile("obtain.*", 0),
            Pattern.compile("perform.*", 0),
            Pattern.compile("pop.*", 0),
            Pattern.compile("post.*", 0),
            Pattern.compile("request.*", 0),
            Pattern.compile("resolve.*", 0),
            Pattern.compile("save.*", 0),
            Pattern.compile("select.*", 0),
            Pattern.compile("show.*", 0),
            Pattern.compile("will.*", 0)
        )

        private val IGNORE_CLASSES = setOf<Class<*>>(MessageQueue::class.java, Handler::class.java)
    }

    override fun onFillValues(item: Any, context: ExtractingContext) {
        if (context.depth >= maxDepth) {
            return
        }
        item.allMethods()
            .filter { !it.name.shouldIgnore() }
            .filter { it.parameterTypes.isEmpty() /*&& it.returnType.isPrimitive*/ && it.returnType != Void.TYPE }
            .forEach { m ->
                try {
                    m.isAccessible = true
                    m.invoke(item)?.let { v ->
                        storeItem(m.name, v, context)
                    }
                } catch (e: Throwable) {
                    Log.e("ReflectionExtractor", "Name:${m.name} Object:$item Exception:${e.javaClass.simpleName}")
                }
            }

        if (useFields) {
            item.allFields()
                .filter { !(Modifier.isStatic(it.modifiers) || it.name.startsWith("shadow$")) }
                .forEach { f ->
                    try {
                        f.isAccessible = true
                        f.get(item)?.let { v ->
                            storeItem(f.name, v, context)
                        }
                    } catch (e: Throwable) {
                        Log.e("ReflectionExtractor", "Name:${f.name} Object:$item Exception:${e.javaClass.simpleName}")
                    }
                }
        }
    }

    private fun storeItem(name: String, v: Any, context: ExtractingContext) {
        if (context.depth >= maxDepth) {
            return
        }
        if (IGNORE_CLASSES.contains(v.javaClass)) {
            return
        }
        context.apply {
            if (v.javaClass.isPrimitiveType()) {
                data[name] = v
            } else if (!cycleHandler.contains(v)) {
                cycleHandler.add(v)
                data[name] = v.asCollection()
                    ?.let { collection ->
                        var result: Any? = null
                        if (depth < maxDepth) {
                            result = collection.map { item ->
                                item?.let {
                                    if (it.javaClass.isPrimitiveType()) {
                                        it
                                    } else {
                                        it.toString()
                                    }
                                }
                            }
                        }
                        result
                    }
                    // no specific extractors, that could potentially create circular reference
                    ?: this@ReflectionExtractor.fillValues(v, ExtractingContext(contextData = data, depth = depth + 1))
            } else {
                data[name] = v.toString()
            }
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

    private fun Any.asCollection(): Collection<*>? {
        return when {
            this is Array<*> -> return this.toList()
            this is IntArray -> return this.toList()
            this is LongArray -> return this.toList()
            this is ByteArray -> return this.toList()
            this is ShortArray -> return this.toList()
            this is BooleanArray -> return this.toList()
            this is CharArray -> return this.toList()
            this is Collection<*> -> return this
            else -> null
        }
    }
}

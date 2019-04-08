package com.scurab.extractorbuilder

import java.lang.StringBuilder

class RegisterBuilder {
    fun build(map: Map<String, String>, packageName: String): String {
        val templateClass = """
            package $packageName

            import android.util.Log

            object ExtractorsRegister {
                fun register() {
            %GENERATED_CODE%
                }

                private fun register(androidApiClass: String, func: () -> BaseExtractor) {
                    try {
                        val clz = Class.forName(androidApiClass)
                        DetailExtractor.registerExtractor(clz, func())
                    } catch (e: Throwable) {
                        Log.e("ExtractorsRegister", "Unable to register '${'$'}androidApiClass'")
                    }
                }
            }
        """.trimIndent()

        val escape = String(charArrayOf('$', '{', '\'', '$', '\'', '}'))
        val sb = StringBuilder().apply {
            map.toSortedMap().forEach { (androidClass, extractor) ->
                val className = androidClass.replace("$", escape)
                append("        ")
                append("""register("$className") {$extractor()}""")
                append("\n")
            }
        }
        return templateClass.replace("%GENERATED_CODE%", sb.toString())
    }
}
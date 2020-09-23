package com.scurab.extractorbuilder

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
                        val clz = classForName(androidApiClass)
                        DetailExtractor.registerExtractor(clz, func())
                    } catch (e: Throwable) {
                        Log.e("ExtractorsRegister", "Unable to register '${'$'}androidApiClass'")
                    }
                }
            
                private fun classForName(androidApiClass: String): Class<*> {
                    return try {
                        Class.forName(androidApiClass)
                    } catch (e: ClassNotFoundException) {
                        //try check if it's internal class like for layoutParams
                        val startIndex = androidApiClass.lastIndexOf(".")
                        Class.forName(androidApiClass.replaceRange(startIndex, startIndex +1 , "${'$'}"))
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

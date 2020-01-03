package com.scurab.extractorbuilder

class ReflectionHelperBuilder {
    fun build(structure: Structure, targetPackage: String): String {
        val template = """
            package $targetPackage;

            import java.util.Map;
            import java.util.HashMap;
            import java.util.Collections;

            /* Auto generated class, do not update! */

            public final class ReflectionHelper {

                public static final Map<String, Item> ITEMS;

                static {
                    HashMap<String, Item> items = new HashMap<>();

            %GENERATED_CODE%

                    ITEMS = Collections.unmodifiableMap(items);
                }

                public static class Item {
                    public final String methodName;
                    public final int arrayIndex;

                    Item(String methodName, int arrayIndex) {
                        this.methodName = methodName;
                        this.arrayIndex = arrayIndex;
                    }
                }
            }
            """.trimIndent()

        val regexp = "get\\((\\d)\\)".toPattern()

        val code = (structure.components + structure.views)
            .flatMap { it.value.items.values }
            .toTypedArray()
            .flatten()
            .filter { it.name.endsWith(":") }
            .associateBy { it.name }
            .toSortedMap()
            .map { (_, it) ->
                val name = it.name.substring(0, it.name.length - 1)
                val arrayIndex = it.customCode?.let { code ->
                    regexp.matcher(code).takeIf { it.find() }?.run { group(1).toInt() }
                } ?: -1
                "\t\titems.put(\"$name\", new Item(\"${it.methodName}\", $arrayIndex));"
            }.joinToString("\n")

        return template.replace("%GENERATED_CODE%", code)
    }
}
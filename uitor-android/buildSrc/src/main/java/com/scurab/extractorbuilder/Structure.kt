package com.scurab.extractorbuilder

import com.google.gson.annotations.SerializedName

class Structure {

    @SerializedName("views")
    lateinit var views: Map<String, StructureItem>
    lateinit var components: Map<String, StructureItem>

    fun allItems(): Map<String, StructureItem> {
        return views + components
    }

    class StructureItem {
        @SerializedName("parentExtractor")
        var parentExtractor: String? = null
        @SerializedName("parent")
        var parent: String? = null
        @SerializedName("className")
        var className: String? = null
        @SerializedName("usingGenerics")
        var usingGenerics: Boolean = false
        @SerializedName("items")
        lateinit var items: Map<String, Array<MethodInfo>>
    }

    class MethodInfo {
        @SerializedName("m")
        lateinit var methodName: String
        @SerializedName("n")
        private val _name: String? = null
        @SerializedName("t")
        val translatorMethod: String? = null
        @SerializedName("c")
        val customCode: String? = null
        @SerializedName("r")
        var useReflection: Boolean = false
        @SerializedName("i")
        val isId: Boolean = false
        @SerializedName("e")
        val useExtractor: Boolean = false
        @SerializedName("p")
        val isProperty: Boolean = false

        val name: String by lazy {
            _name ?: methodName.run {
                if (startsWith("get")) {
                    substring(3)
                } else substring(0, 1).toUpperCase() + substring(1)
            }
        }
    }
}

package com.scurab.extractorbuilder

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class ExtractorsBuilder {
    fun build(receiverClass: String,
              structureItem: Structure.StructureItem,
              packageName: String): FileSpec {

        val className = (structureItem.className ?: receiverClass.simpleClassName())
        val parentClass =
                structureItem.parent?.let {
                    ClassName.bestGuess("$packageName.${it.simpleClassName()}Extractor")
                } ?: structureItem.parentExtractor?.let { ClassName.bestGuess(it) }
                ?: ClassName.bestGuess("$packageName.BaseExtractor")
        val inFileClassName = "${className}Extractor"
        val file = FileSpec.builder(packageName, inFileClassName)
                .addType(TypeSpec.classBuilder(inFileClassName)
                        .superclass(parentClass)
                        .addModifiers(KModifier.OPEN)
                        .addFunction(FunSpec.builder("onFillValues")
                                .addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("android.annotation.SuppressLint")).addMember("%S", "NewApi").build())
                                .addModifiers(KModifier.OVERRIDE)
                                .addParameter("item", Any::class.asClassName())
                                .addParameter(
                                        ParameterSpec.builder("data", ClassName.bestGuess("kotlin.collections.MutableMap")
                                                .parameterizedBy(String::class.asTypeName(), Any::class.asTypeName()))
                                                .build())
                                .addParameter(
                                        ParameterSpec.builder("contextData", ClassName.bestGuess("MutableMap")
                                                .parameterizedBy(String::class.asTypeName(), Any::class.asTypeName())
                                                .copy(nullable = true)
                                        ).build())
                                .addParameter("depth", Int::class.asClassName())
                                .returns(ClassName.bestGuess("MutableMap").parameterizedBy(String::class.asTypeName(),Any::class.asTypeName()))
                                .addStatement("super.onFillValues(item, data, contextData, depth)")
                                //no escape, as we reference it as ViewGroup.LayoutParams
                                .addStatement("val v = item as " + (receiverClass.replace("$", ".") + if (structureItem.usingGenerics) "<*>" else ""))
                                .apply {
                                    structureItem.items.forEach { (minApi, methodInfoList) ->
                                        val minApiInt = minApi.toInt()
                                        methodInfoList.forEach { mi ->
                                            addStatement(createPutStatement(mi, minApiInt))
                                        }
                                        addCode("\n")
                                    }
                                }
                                .addStatement("return data")
                                .build()
                        ).build()
                ).build()
        return file
    }

    private fun String.simpleClassName() = substring(lastIndexOf(".") + 1).replace("$", "")

    private fun createPutStatement(mi: Structure.MethodInfo, minApi: Int): String {
        var convertToString = ""
        val call = when {
            mi.customCode != null && mi.isId -> mi.customCode
            mi.customCode != null -> mi.customCode
            mi.useReflection && mi.translatorMethod != null -> "reflectionInt(\"${mi.methodName}\")"
            mi.useReflection -> "reflection(\"${mi.methodName}\")"
            mi.isId -> "${mi.methodName}().idName()"
            mi.useExtractor -> {
                convertToString = ", false"
                "${mi.methodName}().extract(depth)"
            }
            else -> {
                var methodName = mi.methodName
                if (!(methodName == "this" || mi.isProperty)) {
                    methodName += "()"
                }
                methodName
            }
        }
        return when {
            mi.translatorMethod != null ->
                "data.put(\"${mi.name}\", $minApi, v) { Translators[TranslatorName.${mi.translatorMethod}].translate($call) }"
            mi.name.isEmpty() -> call
            else -> "data.put(\"${mi.name}\", $minApi, v$convertToString) { $call }"
        }
    }
}
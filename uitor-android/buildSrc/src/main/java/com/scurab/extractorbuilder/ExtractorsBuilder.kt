package com.scurab.extractorbuilder

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName


class ExtractorsBuilder {
    fun build(receiverClass: String,
              structureItem: Structure.StructureItem,
              packageName: String): FileSpec {

        val className = (structureItem.className ?: receiverClass.simpleClassName())
        val parentClass =
                structureItem.parentExtractor?.let { ClassName.bestGuess(it) }
                ?: ClassName.bestGuess("$packageName.BaseExtractor")
        val inFileClassName = "${className}Extractor"
        val file = FileSpec.builder(packageName, inFileClassName)
                .addType(TypeSpec.classBuilder(inFileClassName)
                        .superclass(parentClass)
                        .addModifiers(KModifier.OPEN)
                        .addProperty(PropertySpec.builder("parent",
                                ClassName("java.lang", "Class")
                                        .parameterizedBy(WildcardTypeName.producerOf(Any::class))
                                        .copy(nullable = true),
                                KModifier.OVERRIDE)
                                .initializer("%L", structureItem.parent?.let { "${structureItem.parent}::class.java" } ?: "null")
                                .build())
                        .addFunction(FunSpec.builder("onFillValues")
                                .addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("androidx.annotation.CallSuper")).build())
                                .addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("android.annotation.SuppressLint")).addMember("%S", "NewApi").build())
                                .addModifiers(KModifier.OVERRIDE)
                                .addParameter("item", Any::class.asClassName())
                                .addParameter(
                                        ParameterSpec
                                                .builder("context", ClassName.bestGuess("com.scurab.android.uitor.extract2.ExtractingContext"))
                                                .build())
                                .apply {
                                    if (!parentClass.toString().endsWith(".BaseExtractor")) {
                                        addStatement("super.onFillValues(item, context)")
                                    }
                                }
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
                                .build()
                        ).build()
                ).build()
        return file
    }

    private fun String.simpleClassName() = substring(lastIndexOf(".") + 1).replace("$", "")

    @Suppress("ConstantConditionIf")
    private fun createPutStatement(mi: Structure.MethodInfo, minApi: Int): String {
        var convertToString = ""
        val call = when {
            mi.customCode != null && mi.isId -> mi.customCode
            mi.customCode != null -> {
                if(mi.useExtractor) convertToString = ", false"
                mi.customCode
            }
            mi.useReflection && mi.translatorMethod != null -> "reflectionInt(\"${mi.methodName}\")"
            mi.useReflection -> "reflection(\"${mi.methodName}\")"
            mi.isId -> "${mi.methodName}().idName()"
            mi.useExtractor -> {
                convertToString = ", false"
                var methodName = mi.methodName
                if (!(methodName == "this" || mi.isProperty)) {
                    methodName += "()"
                }
                "${methodName}.extract(ExtractingContext(contextData = context.contextData, depth = context.depth + 1))"
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
                "context.put(\"${mi.name}\", $minApi, v) { Translators[TranslatorName.${mi.translatorMethod}].translate($call) }"
            mi.name.isEmpty() -> call
            else -> "context.put(\"${mi.name}\", $minApi, v$convertToString) { $call }"
        }
    }
}

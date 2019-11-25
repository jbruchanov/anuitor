package com.scurab.android.anuitor.provider

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.extract2.BaseExtractor
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.extract2.ExtractingContext
import com.scurab.android.anuitor.extract2.IFragmentDelegate
import com.scurab.android.anuitor.extract2.ReflectionExtractor
import com.scurab.android.anuitor.extract2.ReflectionHelper
import com.scurab.android.anuitor.extract2.components
import com.scurab.android.anuitor.model.DataResponse
import com.scurab.android.anuitor.model.OutRef
import com.scurab.android.anuitor.reflect.ViewReflector
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.Executor
import com.scurab.android.anuitor.tools.base64
import com.scurab.android.anuitor.tools.ise
import com.scurab.android.anuitor.tools.render
import com.scurab.android.anuitor.tools.renderWithBounds
import com.scurab.android.anuitor.tools.save

class ViewPropertyProvider(private val windowManager: WindowManager) {

    private var reflectionExtractor: ReflectionExtractor? = null

    fun getViewProperty(screenIndex: Int,
                        viewIndex: Int,
                        property: String?,
                        reflection: Boolean = false,
                        maxDepth: Int = 3): DataResponse? {

        return windowManager.getRootView(screenIndex)
                ?.let { DetailExtractor.findViewByPosition(it, viewIndex) }
                ?.let { view ->
                    if (property != null) {
                        getPropertyValue(property, view, reflection, maxDepth)
                    } else {
                        Executor.runInMainThreadBlocking(30000) {
                            handleObject(view, reflection, view.javaClass.name, "", "", maxDepth)
                        }
                    }
                }
    }

    private fun getPropertyValue(property: String, view: View, reflection: Boolean, maxDepth: Int): DataResponse {
        var propertyValue: Any?
        val methodName: String
        if (Constants.OWNER.substringBefore(":") == property) {
            propertyValue = view.components().findOwnerComponent(view)
            if (propertyValue is IFragmentDelegate) {
                propertyValue = propertyValue.fragment
            }
            methodName = ""
        } else {
            val item = ReflectionHelper.ITEMS[property]

            val reflector = ViewReflector(view)
            if (item != null) {
                propertyValue = Executor.runInMainThreadBlocking { reflector.callMethod(item.methodName) }
                if (item.arrayIndex >= 0) {
                    propertyValue = (propertyValue as Array<*>)[item.arrayIndex]
                }
                methodName = item.methodName
            } else {
                val oMethodName = OutRef<String>()
                propertyValue = tryGetValue(reflector, property, oMethodName)
                methodName = oMethodName.value
            }
        }
        return handleObject(propertyValue, reflection, view.javaClass.name, property, methodName, maxDepth)
    }

    private fun handleObject(item: Any?, reflection: Boolean, parentType: String, name: String, methodName: String, maxDepth: Int): DataResponse {
        val response = DataResponse()
        if (item != null) {
            var extractor: BaseExtractor? = if (reflection) null else DetailExtractor.findExtractor(item.javaClass)
            if (extractor == null) {
                reflectionExtractor = ReflectionExtractor(true, maxDepth)
                extractor = reflectionExtractor
            }
            val data = extractor!!.fillValues(item, ExtractingContext())
            //owner of owner is not wanted
            data.remove("Owner")
            //renaming key
            data["0Type"] = data.remove("Type")
                    ?: throw NullPointerException("Type is not present?!")
            data["1ParentType"] = parentType
            data["2Name"] = name
            data["3MethodName"] = methodName
            data["4Extractor"] = extractor.javaClass.name
            response.Context = data

            when (item) {
                is BitmapDrawable -> {
                    response.Data = item.bitmap.save(recycle = false).base64()
                    response.DataType = BASE64_PNG
                }
                is Drawable -> {
                    response.Data = item.renderWithBounds().base64()
                    response.DataType = BASE64_PNG
                }
                is View -> {
                    response.Data = item.render(false).save().base64()
                    response.DataType = BASE64_PNG
                }
            }
        } else {
            response.Context = "Null object"
        }
        return response
    }

    companion object {

        private fun tryGetValue(reflector: ViewReflector, property: String, outMethodName: OutRef<String>): Any {
            val subName = Character.toUpperCase(property[0]) + property.substring(1)
            val methods = arrayOf(property, "get$subName", subName)
            for (s in methods) {
                try {
                    outMethodName.value = s
                    return reflector.callMethod(s)
                } catch (t: Throwable) {
                    outMethodName.setValue(null)
                    //ignore
                }
            }
            ise("Not found methods for property:'$property' tried:'${methods.contentToString()}")
        }
    }
}
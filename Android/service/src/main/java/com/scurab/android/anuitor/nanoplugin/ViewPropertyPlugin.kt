package com.scurab.android.anuitor.nanoplugin

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.extract2.*
import com.scurab.android.anuitor.model.DataResponse
import com.scurab.android.anuitor.model.OutRef
import com.scurab.android.anuitor.reflect.ViewReflector
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.*
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

private const val PROPERTY = "property"
private const val MAX_DEPTH = "maxDepth"
private const val REFLECTION = "reflection"
private const val FILE = "viewproperty.json"
private const val PATH = "/$FILE"

class ViewPropertyPlugin(windowManager: WindowManager) : ActivityPlugin(windowManager) {

    private var reflectionExtractor: ReflectionExtractor? = null

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = HttpTools.MimeType.APP_JSON
    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri

    override fun onRequest(uri: String,
                           headers: Map<String, String>,
                           session: NanoHTTPD.IHTTPSession,
                           file: File,
                           mimeType: String): NanoHTTPD.Response {

        var response: NanoHTTPD.Response =
                NanoHTTPD.Response(NanoHTTPD.Response.Status.NO_CONTENT, mimeType,
                        "Missing/Invalid 'position' and/or 'property' query string arguments ")
        try {
            val queryString = session.queryParameterString
            val qsValue = HttpTools.parseQueryString(queryString)
            if (qsValue.containsKey(POSITION)) {

                val reflection = qsValue.containsKey(REFLECTION)
                val position = qsValue[POSITION]?.toInt() ?: -1
                var property: String? = qsValue[PROPERTY]
                if ("undefined".equals(property, ignoreCase = true)) {
                    property = null
                }

                val maxDepth = (qsValue[MAX_DEPTH] ?: "1").toInt()
                qsValue.currentRootView()
                        ?.let {
                            DetailExtractor.findViewByPosition(it, position)
                        }?.let {view ->
                            val result = if (property != null) {
                                getPropertyValue(property, view, reflection, maxDepth)
                            } else {
                                Executor.runInMainThreadBlocking(30000)
                                { handleObject(view, reflection, view.javaClass.name, "", "", maxDepth) }
                            }
                            response = OKResponse(HttpTools.MimeType.APP_JSON, BasePlugin.JSON.toJson(result))
                        }
            }
        } catch (e: Throwable) {
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))
            response = OKResponse(HttpTools.MimeType.TEXT_PLAIN, e.message + "\n" + stringWriter.toString())
        }
        return response
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
            throw IllegalStateException(String.format("Not found methods for property:'%s' tried:%s", property, Arrays.toString(methods)))
        }
    }
}
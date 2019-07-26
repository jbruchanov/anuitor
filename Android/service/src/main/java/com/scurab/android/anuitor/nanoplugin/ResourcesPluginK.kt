package com.scurab.android.anuitor.nanoplugin

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.NinePatchDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import com.scurab.android.anuitor.extract2.TranslatorName
import com.scurab.android.anuitor.extract2.Translators
import com.scurab.android.anuitor.hierarchy.IdsHelper
import com.scurab.android.anuitor.model.ResourceResponse
import com.scurab.android.anuitor.reflect.ColorStateListReflector
import com.scurab.android.anuitor.reflect.ResourcesReflector
import com.scurab.android.anuitor.tools.DOM2XmlPullBuilder
import com.scurab.android.anuitor.tools.HttpTools
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import com.scurab.android.anuitor.tools.renderNinePatchDrawable
import com.scurab.android.anuitor.tools.renderWithSize
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File

private const val MAX_9PATCH_SIZE = 600
private const val MIN_9PATCH_SIZE = 100
private const val INC_9PATCH_CONST = 3
private const val SIZE = 150
private const val FILE = "resources.json"
private const val PATH = "/$FILE"
private const val ARRAY = "array"
private const val XML = "xml"
private const val ID = "id"
private const val NUMBER = "number"
private const val MAX_RAW_SIZE_FOR_STRING = 8 * 1024

class ResourcesPluginK(private val res: Resources) : BasePlugin() {

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = APP_JSON
    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri

    private var helper: ResourcesReflector = ResourcesReflector(res)

    private val mClearPaint = Paint()

    fun ResourcesPlugin(res: Resources) {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun serveFile(uri: String,
                           headers: Map<String, String>,
                           session: NanoHTTPD.IHTTPSession,
                           file: File,
                           mimeType: String): NanoHTTPD.Response {

        val queryString = session.queryParameterString
        val qs = HttpTools.parseQueryString(queryString)
        val type = APP_JSON
        var response: NanoHTTPD.Response
        if (qs.containsKey(ID)) {
            try {
                val id = qs[ID]?.toInt()
                        ?: throw NullPointerException("containsKey('$ID') and value is null?!")
                val result = dispatchIdRequest(id)
                response = OKResponse(type, result)
            } catch (e: Throwable) {
                val rr = ResourceResponse().apply {
                    Data = e.message ?: "Null message"
                    Context = e.javaClass.name
                    DataType = STRING_DATA_TYPE
                    Type = IdsHelper.RefType.unknown
                }
                val result = ByteArrayInputStream(JSON.toJson(rr).toByteArray())
                response = NanoHTTPD.Response(NanoHTTPD.Response.Status.INTERNAL_ERROR, APP_JSON, result)
            }
        } else {
            val s = IdsHelper.toJson(res)
            response = OKResponse(type, ByteArrayInputStream(s.toByteArray()))
        }
        return response
    }

    private fun dispatchIdRequest(id: Int): ByteArrayInputStream {
        val type = IdsHelper.getType(id)
        val response: ResourceResponse =
                when (type) {
                    IdsHelper.RefType.anim, IdsHelper.RefType.animator, IdsHelper.RefType.interpolator -> {
                        response(XML) { res.extractAnimation(id) }
                    }
                    IdsHelper.RefType.array -> {
                        TODO()
                        //response.Data = res.extractAnimation(id)
                    }
                    IdsHelper.RefType.bool -> response("boolean") { res.getBoolean(id) }
                    IdsHelper.RefType.color -> res.extractColor(id)
                    IdsHelper.RefType.dimen -> response(NUMBER) { res.getDimension(id) }
                    IdsHelper.RefType.drawable, IdsHelper.RefType.mipmap -> res.extractDrawable(id)
                    else -> {
                        response(STRINGS_DATA_TYPE) { "Type '$type' is not supported." }
                    }
                }

//            IdsHelper.RefType.fraction -> {
//                val tv = TypedValue()
//                mRes.getValue(id, tv, true)
//                response.DataType = NUMBER
//                if (TypedValue.TYPE_FRACTION == tv.type) {
//                    response.Data = mRes.getFraction(id, 100, 100)
//                    response.Context = "Base=100"
//                } else if (TypedValue.TYPE_FLOAT == tv.type) {
//                    response.Data = tv.float
//                } else {
//                    response.Data = "Not implemented franction for TypedValue.type = " + tv.type
//                    response.DataType = BasePlugin.STRING_DATA_TYPE
//                }
//            }
//            IdsHelper.RefType.id -> {
//                response.Data = id
//                response.DataType = Int::class.javaPrimitiveType!!.simpleName
//            }
//            IdsHelper.RefType.integer -> {
//                response.Data = mRes.getInteger(id)
//                response.DataType = Int::class.javaPrimitiveType!!.simpleName
//            }
//            IdsHelper.RefType.menu, IdsHelper.RefType.layout, IdsHelper.RefType.transition -> {
//                val load = mHelper.load(id)
//                response.Data = load
//                response.DataType = XML
//            }
//            IdsHelper.RefType.plurals -> handlePlurals(id, response)
//            IdsHelper.RefType.string -> {
//                response.Data = mRes.getString(id)
//                response.DataType = BasePlugin.STRING_DATA_TYPE
//            }
//            IdsHelper.RefType.xml -> {
//                response.Data = DOM2XmlPullBuilder.transform(mRes.getXml(id))
//                response.DataType = XML
//            }
//            IdsHelper.RefType.raw -> {
//                response.DataType = BasePlugin.STRING_DATA_TYPE
//                try {
//                    val `is` = mRes.openRawResource(id)
//                    val available = `is`.available()
//                    if (available < MAX_RAW_SIZE_FOR_STRING) {
//                        val data = ByteArray(available)
//                        `is`.read(data)
//                        response.Data = String(data)
//                    } else {
//                        response.Data = "Skipped raw resources content because of size:$available"
//                    }
//                    `is`.close()
//                } catch (e: Throwable) {
//                    response.Data = e.message
//                    e.printStackTrace()
//                }
//
//            }
//            IdsHelper.RefType.attr, IdsHelper.RefType.style, IdsHelper.RefType.styleable, IdsHelper.RefType.unknown -> {
//                response.Data = String.format("Type '%s' is not supported.", type)
//                response.DataType = BasePlugin.STRING_DATA_TYPE
//            }
//            else -> {
//                response.Data = String.format("Type '%s' is not supported.", type)
//                response.DataType = BasePlugin.STRING_DATA_TYPE
//            }
        response.Type = type
        response.id = id
        response.Name = IdsHelper.getNameForId(id)
        val json = BasePlugin.JSON.toJson(response)
        return ByteArrayInputStream(json.toByteArray())
    }

    private inline fun response(dataType: String, op: (ResourceResponse) -> Any): ResourceResponse {
        return ResourceResponse().apply {
            DataType = dataType
            Data = op(this)
        }
    }

    private fun Int.resolveTypedValue(): TypedValue {
        return TypedValue().apply {
            res.getValue(this@resolveTypedValue, this, true)
        }
    }

    private fun Resources.extractAnimation(resId: Int): String {
        return DOM2XmlPullBuilder.transform(res.getAnimation(resId))
    }

    private fun Resources.extractColor(id: Int): ResourceResponse {
        val tv: TypedValue = id.resolveTypedValue()

        return if (tv.string?.toString()?.endsWith(".xml") == true) {
            response(ARRAY) {
                arrayOfNulls<ResourceResponse>(2).let {
                    it[0] = response(XML) { r ->
                        r.id = id
                        helper.load(id)
                    }
                    it[1] = response(ARRAY) {
                        res.extractColorStateList(id)
                    }
                }
            }
        } else {
            response("color") { HttpTools.getStringColor(getColor(id)) }
        }
    }

    private fun Resources.extractColorStateList(id: Int) : ResourceResponse {
        val colorStateList = res.getColorStateList(id)
        val reflector = ColorStateListReflector(colorStateList)

        return response(ARRAY) {
            it.id = id
            arrayOfNulls<ResourceResponse>(reflector.stateCount)
                    .mapIndexed { i, _ ->
                        response("color") {r ->
                            r.id = id
                            val colorState = reflector.getColorState(i)
                            r.Context = Translators[TranslatorName.DrawableState]
                                    .translate(colorState)

                            val x = colorStateList.getColorForState(colorState, Integer.MIN_VALUE)
                            val y = colorStateList.getColorForState(colorState, Integer.MAX_VALUE)
                            //just ask twice and compare values, if they are same, default value wasn't involved
                            if (x == y) {
                                HttpTools.getStringColor(x)
                            } else {
                                "Unable to get Color for state"
                            }
                        }
                    }
        }
    }

    private fun Resources.extractDrawable(id: Int): ResourceResponse {
        val tv = id.resolveTypedValue()

        val drawable = res.getDrawable(id)
        return when {
            drawable is NinePatchDrawable -> response("9patch") {
                it.id = id
                drawable.renderNinePatchDrawable()
            }
            tv.string?.toString()?.endsWith(".xml") == true -> {
                response(ARRAY) { ra ->
                    ra.id = id
                    arrayOfNulls<ResourceResponse>(2).let {
                        it[0] = response(XML) { r ->
                            r.id = id
                            helper.load(id)
                        }
                        //TODO: Array
                        it[1] = response(ARRAY) {
                            when (drawable) {
                                is StateListDrawable -> TODO("handleStateListDrawable")
                                is AnimationDrawable -> TODO("handleAnimationDrawable")
                                else -> drawable.renderWithSize(SIZE, SIZE)
                            }
                        }
                    }
                }
            }
            else -> response(BASE64_PNG) {
                it.id = id
                drawable.renderWithSize(SIZE, SIZE)
            }
        }
    }
}
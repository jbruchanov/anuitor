package com.scurab.android.anuitor.nanoplugin

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import com.scurab.android.anuitor.extract2.TranslatorName
import com.scurab.android.anuitor.extract2.Translators
import com.scurab.android.anuitor.hierarchy.IdsHelper
import com.scurab.android.anuitor.model.ResourceResponse
import com.scurab.android.anuitor.reflect.ColorStateListReflector
import com.scurab.android.anuitor.reflect.ResourcesReflector
import com.scurab.android.anuitor.reflect.StateListDrawableReflector
import com.scurab.android.anuitor.tools.*
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.math.max
import kotlin.math.min

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
private val QUANTITIES = intArrayOf(-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 50, 80, 99, 100, 1000, 10000)

class ResourcesPlugin(private val res: Resources) : BasePlugin() {

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
                e.printStackTrace()
                val rr = ResourceResponse().apply {
                    Data = e.message ?: "Null message"
                    Context = e.javaClass.name
                    DataType = STRING_DATA_TYPE
                    Type = IdsHelper.RefType.unknown
                }
                val result = ByteArrayInputStream(JSON.toJson(rr).toByteArray())
                response = OKResponse(APP_JSON, result)
            }
        } else {
            val s = IdsHelper.toJson(res)
            response = OKResponse(type, ByteArrayInputStream(s.toByteArray()))
        }
        return response
    }

    private fun dispatchIdRequest(resId: Int): ByteArrayInputStream {
        val type = IdsHelper.getType(resId)
        val response: ResourceResponse =
                when (type) {
                    IdsHelper.RefType.anim, IdsHelper.RefType.animator, IdsHelper.RefType.interpolator ->
                        res.extractAnimation(resId)
                    IdsHelper.RefType.array -> res.extractArray(resId)
                    IdsHelper.RefType.bool -> response("boolean") { res.getBoolean(resId) }
                    IdsHelper.RefType.color -> res.extractColor(resId)
                    IdsHelper.RefType.dimen -> response(NUMBER) { res.getDimension(resId) }
                    IdsHelper.RefType.drawable, IdsHelper.RefType.mipmap -> res.extractDrawable(resId)
                    IdsHelper.RefType.fraction -> res.extractFraction(resId)
                    IdsHelper.RefType.id, IdsHelper.RefType.integer ->
                        response(Int::class.javaPrimitiveType?.simpleName ?: STRING_DATA_TYPE) { resId }
                    IdsHelper.RefType.menu, IdsHelper.RefType.layout, IdsHelper.RefType.transition ->
                        response(XML) { helper.load(resId) }
                    IdsHelper.RefType.plurals -> res.extractPlurals(resId)
                    IdsHelper.RefType.string -> response(STRING_DATA_TYPE) { res.getString(resId) }
                    IdsHelper.RefType.xml -> response(XML) { DOM2XmlPullBuilder.transform(res.getXml(resId)) }
                    IdsHelper.RefType.raw -> res.extractRaw(resId)
                    IdsHelper.RefType.attr, IdsHelper.RefType.style, IdsHelper.RefType.styleable, IdsHelper.RefType.unknown ->
                        response(STRING_DATA_TYPE) { "Type '$type' is not supported." }
                    else -> {
                        response(STRING_DATA_TYPE) { "Type '$type' is not supported." }
                    }
                }

        response.Type = type
        response.id = resId
        response.Name = IdsHelper.getNameForId(resId)
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

    private fun Resources.extractAnimation(resId: Int): ResourceResponse {
        return response(XML) { DOM2XmlPullBuilder.transform(res.getAnimation(resId)) }
    }

    private fun Resources.extractArray(resId: Int) : ResourceResponse {
        //TODO:test it more
        val stringArray = res.getStringArray(resId)
        return if (stringArray.all { it == null }) {
            val intArray = res.getIntArray(resId)
            if (intArray.all { it == 0 }) {
                res.extractTypedArray(resId)
            } else {
                response(IntArray::class.java.simpleName) { intArray }
            }
        } else {
            response(STRINGS_DATA_TYPE) { stringArray }
        }
    }

    @SuppressLint("Recycle")
    private fun Resources.extractTypedArray(resId: Int): ResourceResponse {
        return res.obtainTypedArray(resId).use { typedArray ->
            response(STRINGS_DATA_TYPE) {
                val tv = TypedValue()
                arrayOfNulls<String>(typedArray.length()).mapIndexed { i, _ ->
                    typedArray.getValue(i, tv)
                    if (tv.type == TypedValue.TYPE_REFERENCE) {
                        IdsHelper.getNameForId(tv.data)
                    } else {
                        tv.data.toString()
                    }
                }
            }
        }
    }

    private fun Resources.extractColor(resId: Int): ResourceResponse {
        val tv: TypedValue = resId.resolveTypedValue()

        return if (tv.string?.toString()?.endsWith(".xml") == true) {
            response(ARRAY) {
                arrayOfNulls<ResourceResponse>(2).also {
                    it[0] = response(XML) { r ->
                        r.id = resId
                        helper.load(resId)
                    }
                    it[1] = res.extractColorStateList(resId)
                }
            }
        } else {
            response("color") { HttpTools.getStringColor(getColor(resId)) }
        }
    }

    private fun Resources.extractColorStateList(resId: Int) : ResourceResponse {
        val colorStateList = res.getColorStateList(resId)
        val reflector = ColorStateListReflector(colorStateList)

        return response(ARRAY) {
            it.id = resId
            arrayOfNulls<ResourceResponse>(reflector.stateCount)
                    .mapIndexed { i, _ ->
                        response("color") {r ->
                            r.id = resId
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

    private fun Resources.extractDrawable(resId: Int): ResourceResponse {
        val tv = resId.resolveTypedValue()

        val drawable = res.getDrawable(resId)
        return when {
            drawable is NinePatchDrawable -> drawable.extractNine9PatchDrawable()
            tv.string?.toString()?.endsWith(".xml") == true -> {
                response(ARRAY) { ra ->
                    ra.id = resId
                    arrayOfNulls<ResourceResponse>(2).also {
                        it[0] = response(XML) { r ->
                            r.id = resId
                            helper.load(resId)
                        }
                        it[1] = when (drawable) {
                            is StateListDrawable -> drawable.extractStateListDrawable()
                            is AnimationDrawable -> drawable.extractAnimationDrawable()
                            else -> drawable.extractDrawable()
                        }
                    }
                }
            }
            else -> response(BASE64_PNG) {
                it.id = resId
                drawable.render(SIZE, SIZE)
            }
        }
    }

    private fun Resources.extractFraction(resId:Int): ResourceResponse {
        val tv = resId.resolveTypedValue()
        return when (tv.type) {
            TypedValue.TYPE_FRACTION -> response(NUMBER) { res.getFraction(resId, 100, 100) }
            TypedValue.TYPE_FLOAT -> response(NUMBER) { tv.float }
            else -> response(STRING_DATA_TYPE) { "Not implemented fraction for TypedValue.type='${tv.type}'" }
        }
    }

    private fun Resources.extractPlurals(resId:Int): ResourceResponse {
        return response(STRINGS_DATA_TYPE) {
            arrayOfNulls<String>(QUANTITIES.size).mapIndexed { i, _ ->
                "${res.getQuantityString(resId, i, i)}\t($i)"
            }
        }
    }

    private fun Resources.extractRaw(resId:Int): ResourceResponse {
        return response(STRING_DATA_TYPE) {
            try {
                res.openRawResource(resId).use { stream ->
                    stream.takeIf { it.available() < MAX_RAW_SIZE_FOR_STRING }
                            ?.let { String(it.readBytes()) }
                            ?: "Skipped raw resources content because of size:${stream.available()}"
                }
            } catch (e: Throwable) {
                e.message ?: "Null exception message"
            }
        }
    }

    private fun AnimationDrawable.extractAnimationDrawable(): ResourceResponse {
        return response(ARRAY) {
            arrayOfNulls<ResourceResponse>(numberOfFrames).mapIndexed { i, _ ->
                response(BASE64_PNG) {
                    //it.id = resId ?
                    it.Context = "Frame:$i"
                    getFrame(i).render(SIZE, SIZE).base64()
                }
            }
        }
    }

    private fun StateListDrawable.extractStateListDrawable(): ResourceResponse {
        return response(ARRAY) {
            StateListDrawableReflector(this).let { reflector ->
                arrayOfNulls<ResourceResponse>(reflector.stateCount).mapIndexed { i, _ ->
                    val state = reflector.getStateDrawable(i)
                    val stateSet = reflector.getStateSet(i) ?: intArrayOf()
                    state.state = stateSet
                    response(BASE64_PNG) {
                        it.Context = Translators[TranslatorName.DrawableState].translate(stateSet)
                        state.render(SIZE, SIZE).base64()
                    }
                }
            }
        }
    }

    private fun NinePatchDrawable.extractNine9PatchDrawable(): ResourceResponse {
        val width = intrinsicWidth
        val height = intrinsicHeight
        val sizes = intArrayOf(
                //1
                width, height, max(MIN_9PATCH_SIZE, min(INC_9PATCH_CONST * width, MAX_9PATCH_SIZE)),
                //2
                height, width, max(MIN_9PATCH_SIZE, min(INC_9PATCH_CONST * height, MAX_9PATCH_SIZE)),
                //3
                max(MIN_9PATCH_SIZE, min(INC_9PATCH_CONST * width, MAX_9PATCH_SIZE)),
                max(MIN_9PATCH_SIZE, min(INC_9PATCH_CONST * height, MAX_9PATCH_SIZE)))

        return response(ARRAY) {
            arrayOfNulls<ResourceResponse>(4).mapIndexed { i, _ ->
                val tw = sizes[i * 2]
                val th = sizes[i * 2 + 1]
                response(BASE64_PNG) {
                    it.Context = "Size: ${tw}x$th " + (if(i == 0) "original" else "")
                    renderWithSize(tw, th).base64()
                }
            }
        }
    }

    private fun Drawable.extractDrawable(): ResourceResponse {
        return response(BASE64_PNG) { render(SIZE, SIZE).base64() }
    }
}

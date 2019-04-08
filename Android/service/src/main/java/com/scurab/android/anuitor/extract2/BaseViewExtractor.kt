package com.scurab.android.anuitor.extract2

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.scurab.android.anuitor.tools.atLeastApi

abstract class BaseViewExtractor : BaseExtractor() {

    companion object {
        val mandatoryKeys = arrayOf("_ScaleX", "_ScaleY", "_Visibility", "_RenderViewContent", "Position", "LocationScreenX", "LocationScreenY", "Height", "Width", "Type")
    }

    @CallSuper
    override fun onFillValues(item: Any, data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?): MutableMap<String, Any> {
        val v = item as View
        data["LayoutParams:"] = v.layoutParams?.javaClass?.name ?: "null"
        fillMandatoryValues(v, data, contextData)
        return super.onFillValues(item, data, contextData)
    }

    private fun fillMandatoryValues(v: View, data: MutableMap<String, Any>, contextData: MutableMap<String, Any>?): MutableMap<String, Any> {
        data["Extractor"] = javaClass.name
        data["Width"] = v.width
        data["Height"] = v.height

        val isViewGroup = v is ViewGroup && !DetailExtractor.isExcludedViewGroup(v.javaClass.name)
        val isParentVisible = contextData?.get("_Visibility") as? Int? ?: View.VISIBLE
        val isVisible = v.visibility == View.VISIBLE && (View.VISIBLE == isParentVisible)
        val hasBackground = v.background != null
        val shouldRender = isVisible && v.isShown && (isViewGroup && hasBackground || !isViewGroup)
        data["_RenderViewContent"] = shouldRender
        data["_Visibility"] = v.visibility

        //TODO:remove later
        data["RenderViewContent"] = shouldRender
        fillScale(v, data, contextData)
        fillLocationValues(v, data, contextData)
        return data
    }

    private fun fillScale(v: View, data: MutableMap<String, Any>, parentData: Map<String, Any>?): MutableMap<String, Any> {
        var sx = 1f
        var sy = 1f
        atLeastApi(Build.VERSION_CODES.HONEYCOMB) {
            data["ScaleX"] = v.scaleX
            data["ScaleY"] = v.scaleY

            var fx = 1f
            var fy = 1f
            parentData?.let {
                fx = it["_ScaleX"] as? Float ?: fx
                fy = it["_ScaleY"] as? Float ?: fy
            }
            sx = v.scaleX * fx
            sy = v.scaleY * fy
        }

        data["_ScaleX"] = sx
        data["_ScaleY"] = sy
        data["ScaleAbsoluteX"] = sx
        data["ScaleAbsoluteY"] = sy
        return data
    }

    private fun fillLocationValues(v: View, data: MutableMap<String, Any>, parentData: Map<String, Any>?): MutableMap<String, Any> {
        val position = IntArray(2)
        v.getLocationOnScreen(position)
        data["LocationScreenX"] = position[0]
        data["LocationScreenY"] = position[1]
        position[1] = 0
        position[0] = position[1]

        v.getLocationInWindow(position)
        data["LocationWindowX"] = position[0]
        data["LocationWindowY"] = position[1]
        return data
    }
}
package com.scurab.android.uitor.extract2

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.scurab.android.uitor.Constants.COMPONENTS
import com.scurab.android.uitor.Constants.OWNER
import com.scurab.android.uitor.tools.atLeastApi
import com.scurab.android.uitor.tools.hasSize

/**
 * Base extractor for any subclass of [android.view.View]
 */
abstract class BaseViewExtractor : BaseExtractor() {

    final override fun fillValues(item: Any, context: ExtractingContext): MutableMap<String, Any> {
        val viewComponents = context.contextData[COMPONENTS] as? ViewComponents
        if (viewComponents?.activity == null) { // root component's context is an app not an activity
            val components = (item as View).components()
            context.contextData[COMPONENTS] = components
        }
        return super.fillValues(item, context)
    }

    @CallSuper
    override fun onFillValues(item: Any, context: ExtractingContext) {
        val v = item as View
        context.apply {
            data["LayoutParams:"] = v.layoutParams?.javaClass?.name ?: "null"
            (v.layoutParams as? ViewGroup.MarginLayoutParams)?.run {
                data["LayoutParams_margins"] = "[$leftMargin, $topMargin, $rightMargin, $bottomMargin]"
            }
            data[OWNER] = (contextData?.get(COMPONENTS) as? ViewComponents)?.let {
                it.findOwnerComponent(v)
            } ?: "Unknown"
            DetailExtractor.getRenderArea(v)?.let {
                val rect = Rect()
                it.getRenderArea(v, rect)
                val value = rect.stringSizes()
                data["_RenderAreaRelative"] = value
                data["RenderAreaRelative"] = value
            }

            fillMandatoryValues(v, this)
        }
    }

    private fun fillMandatoryValues(v: View, context: ExtractingContext) {
        context.apply {
            data["Width"] = v.width
            data["Height"] = v.height

            val isViewGroup = v is ViewGroup && !DetailExtractor.isExcludedViewGroup(v.javaClass.name)
            val isParentVisible = contextData["_Visibility"] as? Int? ?: View.VISIBLE
            val isVisible = v.visibility == View.VISIBLE && (View.VISIBLE == isParentVisible)
            val hasBackground = v.background != null
            // isShown is ignored, otherwise it wouldn't be rendered app is in background or activity inactive
            val shouldRender = v.hasSize() && isVisible /*&& v.isShown */ && (isViewGroup && hasBackground || !isViewGroup)
            data["_RenderViewContent"] = shouldRender
            data["_Visibility"] = v.visibility

            // TODO:remove later
            data["RenderViewContent"] = shouldRender
            fillScale(v, context)
            fillLocationValues(v, context)
        }
    }

    private fun fillScale(v: View, context: ExtractingContext) {
        context.apply {
            var sx = 1f
            var sy = 1f
            atLeastApi(Build.VERSION_CODES.HONEYCOMB) {
                data["ScaleX"] = v.scaleX
                data["ScaleY"] = v.scaleY

                val fx = contextData["_ScaleX"] as? Float ?: 1f
                val fy = contextData["_ScaleY"] as? Float ?: 1f
                sx = v.scaleX * fx
                sy = v.scaleY * fy
            }

            data["_ScaleX"] = sx
            data["_ScaleY"] = sy
            data["ScaleAbsoluteX"] = sx
            data["ScaleAbsoluteY"] = sy
        }
    }

    private fun fillLocationValues(v: View, context: ExtractingContext) {
        context.apply {
            val position = IntArray(2)
            v.getLocationOnScreen(position)
            data["LocationScreenX"] = position[0]
            data["LocationScreenY"] = position[1]
            position[1] = 0
            position[0] = position[1]

            v.getLocationInWindow(position)
            data["LocationWindowX"] = position[0]
            data["LocationWindowY"] = position[1]
        }
    }
}

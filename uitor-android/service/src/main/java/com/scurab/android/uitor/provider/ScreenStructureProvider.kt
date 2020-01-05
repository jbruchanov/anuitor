package com.scurab.android.uitor.provider

import android.app.Activity
import android.content.ContextWrapper
import com.scurab.android.uitor.Constants
import com.scurab.android.uitor.extract2.DetailExtractor
import com.scurab.android.uitor.extract2.ExtractingContext
import com.scurab.android.uitor.reflect.ActivityThreadReflector
import com.scurab.android.uitor.reflect.WindowManager
import com.scurab.android.uitor.tools.ise
import java.util.*

private const val ROOT_NAME = "RootName"
private const val OWNER_ACTIVITY = "OwnerActivity"

class ScreenStructureProvider(private val windowManager: WindowManager) {
    private val activityThread = ActivityThreadReflector()

    fun getStructure(): List<Map<String, Any>> {
        val resultDataSet = mutableListOf<Map<String, Any>>()

        windowManager.viewRootNames?.let { viewRootNames ->
            for (rootName in viewRootNames) {
                val v = windowManager.getRootView(rootName)
                        ?: ise("View in windowManager and not having a context?!")
                var c = v.context
                val data = TreeMap<String, Any>()
                data[ROOT_NAME] = rootName
                resultDataSet.add(data)
                var activity = activityThread.activities
                        .firstOrNull { it.window.decorView.rootView == v.rootView }
                if (activity == null && c is Activity) {
                    activity = c
                }

                if (activity != null) {
                    DetailExtractor.getExtractor(activity.javaClass).apply {
                        fillValues(activity, ExtractingContext(data))
                    }
                } else {
                    DetailExtractor
                            .getExtractor(v)
                            .fillValues(v, ExtractingContext(data))

                    if (c is ContextWrapper) {
                        c = c.baseContext
                        if (c is Activity) {
                            val sub = TreeMap<String, Any>()
                            data[OWNER_ACTIVITY] = sub
                            DetailExtractor.getExtractor(c.javaClass).apply {
                                fillValues(c, ExtractingContext(sub))
                            }
                        }
                    }
                }
                data.remove(Constants.OWNER)
            }
        }

        resultDataSet.reverse()//stack order
        return resultDataSet
    }
}
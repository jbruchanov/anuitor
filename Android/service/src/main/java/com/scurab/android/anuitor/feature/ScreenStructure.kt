package com.scurab.android.anuitor.feature

import android.app.Activity
import android.content.ContextWrapper
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.extract2.ExtractingContext
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.reflect.ActivityThreadReflector
import com.scurab.android.anuitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.util.*

class ScreenStructure(windowManager: WindowManager,
                      private val json: JsonSerializer) : FeaturePlugin {

    private val dataProvider = ScreenStructureProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/screenstructure") {
            val json = json.toJson(dataProvider.getStructure())
            call.respondText(json, ContentTypes.json, HttpStatusCode.OK)
        }
    }
}

internal class ScreenStructureProvider(private val windowManager: WindowManager) {
    private val activityThread = ActivityThreadReflector()

    fun getStructure(): List<Map<String, Any>> {
        val viewRootNames = windowManager.viewRootNames
        val resultDataSet = mutableListOf<Map<String, Any>>()

        for (rootName in viewRootNames) {
            val v = windowManager.getRootView(rootName)
            var c = v.context
            val data = TreeMap<String, Any>()
            data["RootName"] = rootName
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
                        data["OwnerActivity"] = sub
                        DetailExtractor.getExtractor(c.javaClass).apply {
                            fillValues(c, ExtractingContext(sub))
                        }
                    }
                }
            }
            data.remove(Constants.OWNER)
        }

        resultDataSet.reverse()//stack order
        return resultDataSet
    }
}
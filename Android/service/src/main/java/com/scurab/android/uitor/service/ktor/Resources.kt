package com.scurab.android.uitor.service.ktor

import android.util.Log
import com.scurab.android.uitor.ContentTypes
import com.scurab.android.uitor.FeaturePlugin
import com.scurab.android.uitor.catching
import com.scurab.android.uitor.hierarchy.IdsHelper
import com.scurab.android.uitor.json.JsonSerializer
import com.scurab.android.uitor.provider.ResourcesProvider
import com.scurab.android.uitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class Resources(windowManager: WindowManager,
                private val json: JsonSerializer) : FeaturePlugin {

    private val dataProvider = ResourcesProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/resources/all") {
            catching {
                val data = IdsHelper.getAllResources(dataProvider.resources)
                var groupIndex = 0
                var groups = data.keys.size
                val result = IdsHelper.getAllResources(dataProvider.resources).mapValues { (group, items) ->
                    val result = items.mapIndexed { index, item ->
                        try {
                            Log.d("Resources", "GroupIndex:$groupIndex/$groups ($group), item:$index/${items.size}")
                            dataProvider.createResourceResponse(item.key, 0)
                        } catch (e: Throwable) {
                            ResourcesProvider.errorResponse(e)
                        }
                    }
                    groupIndex++
                    result
                }
                call.respondText(json.toJson(result), ContentTypes.json, HttpStatusCode.OK)
            }
        }
        routing.get("/resources/list") {
            catching {
                val result = IdsHelper.getAllResources(dataProvider.resources)
                call.respondText(json.toJson(result), ContentTypes.json, HttpStatusCode.OK)
            }
        }
        routing.get("/resources/{screenIndex}/{resId}") {
            catching {
                val resId = call.parameters["resId"]?.toIntOrNull()
                val screenIndex = call.parameters["screenIndex"]?.toIntOrNull() ?: 0
                val result: Any = try {
                    resId?.let {
                        dataProvider.createResourceResponse(it, screenIndex)
                    } ?: IdsHelper.getAllResources(dataProvider.resources)
                } catch (e: Throwable) {
                    ResourcesProvider.errorResponse(e)
                }

                call.respondText(json.toJson(result), ContentTypes.json, HttpStatusCode.OK)
            }
        }
    }
}
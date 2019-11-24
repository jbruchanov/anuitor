package com.scurab.android.anuitor.service.ktor

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.hierarchy.IdsHelper
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.model.ResourceResponse
import com.scurab.android.anuitor.provider.ResourcesProvider
import com.scurab.android.anuitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class Resources(windowManager: WindowManager,
                private val json: JsonSerializer) : FeaturePlugin {

    private val dataProvider = ResourcesProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/resources/{screenIndex?}/{resId?}") {
            val resId = call.parameters["resId"]?.toIntOrNull()
            val screenIndex = call.parameters["screenIndex"]?.toIntOrNull() ?: 0
            val json: String = try {
                resId?.let {
                    val response = dataProvider.createResourceResponse(it, screenIndex)
                    json.toJson(response)
                } ?: IdsHelper.toJson(dataProvider.resources)
            } catch (e: Throwable) {
                json.toJson(ResourcesProvider.errorResponse(e))
            }
            call.respondText(json, ContentTypes.json, HttpStatusCode.OK)
        }
    }
}
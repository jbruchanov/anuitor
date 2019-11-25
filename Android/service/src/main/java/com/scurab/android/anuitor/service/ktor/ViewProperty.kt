package com.scurab.android.anuitor.service.ktor

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.provider.ViewPropertyProvider
import com.scurab.android.anuitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class ViewProperty(windowManager: WindowManager,
                   private val json: JsonSerializer) : FeaturePlugin {

    private val dataProvider = ViewPropertyProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/view/{screen}/{id}/{property}/{reflection?}/{maxDepth?}/") {
            val screenIndex = call.parameters["screen"]?.toIntOrNull()
            val viewIndex = call.parameters["id"]?.toIntOrNull()
            val property = call.parameters["property"]?.takeIf { it.isNotEmpty() }
            val reflection = call.parameters["reflection"]?.toBoolean() ?: false
            val maxDepth = call.parameters["maxDepth"]?.toIntOrNull() ?: 3

            if (screenIndex != null && viewIndex != null && property != null) {
                val response = dataProvider.getViewProperty(screenIndex, viewIndex, property, reflection, maxDepth)
                call.respondText(json.toJson(response), ContentTypes.json, HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
package com.scurab.android.anuitor.service.ktor

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.provider.ScreenStructureProvider
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

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
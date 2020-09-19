package com.scurab.android.uitor.service.ktor

import com.scurab.android.uitor.ContentTypes
import com.scurab.android.uitor.FeaturePlugin
import com.scurab.android.uitor.catching
import com.scurab.android.uitor.json.JsonSerializer
import com.scurab.android.uitor.provider.ScreenStructureProvider
import com.scurab.android.uitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class ScreenStructure(
    windowManager: WindowManager,
    private val json: JsonSerializer
) : FeaturePlugin {

    private val dataProvider = ScreenStructureProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/screenstructure") {
            catching {
                val json = json.toJson(dataProvider.getStructure())
                call.respondText(json, ContentTypes.json, HttpStatusCode.OK)
            }
        }
    }
}

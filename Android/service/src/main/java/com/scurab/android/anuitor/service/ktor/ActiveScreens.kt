package com.scurab.android.anuitor.service.ktor

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class ActiveScreens(private val windowManager: WindowManager,
                    private val json: JsonSerializer) : FeaturePlugin {

    override fun registerRoute(routing: Routing) {
        routing.get("/screens") {
            val json = json.toJson(windowManager.viewRootNames)
            call.respondText(json, ContentTypes.json, HttpStatusCode.OK)
        }
    }
}
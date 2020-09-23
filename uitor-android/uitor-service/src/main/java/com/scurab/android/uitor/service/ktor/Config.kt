package com.scurab.android.uitor.service.ktor

import com.scurab.android.uitor.ContentTypes
import com.scurab.android.uitor.FeaturePlugin
import com.scurab.android.uitor.catching
import com.scurab.android.uitor.json.JsonSerializer
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class Config(
    private val configs: Map<String, Any>,
    private val json: JsonSerializer
) : FeaturePlugin {
    override fun registerRoute(routing: Routing) {
        routing.get("/config") {
            catching {
                call.respondText(json.toJson(configs), ContentTypes.json)
            }
        }
    }
}

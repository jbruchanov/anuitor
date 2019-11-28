package com.scurab.android.anuitor.service.ktor

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.catching
import com.scurab.android.anuitor.tools.LogCatProvider
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class LogCat : FeaturePlugin {
    override fun registerRoute(routing: Routing) {
        routing.get("/logcat/{type?}") {
            val type = call.parameters["type"]
            catching {
                call.respondText(LogCatProvider.dumpLogcat(type), ContentTypes.text, HttpStatusCode.OK)
            }
        }
    }

}
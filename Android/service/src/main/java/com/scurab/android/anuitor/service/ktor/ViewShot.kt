package com.scurab.android.anuitor.service.ktor

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.provider.ViewShotProvider
import com.scurab.android.anuitor.reflect.WindowManager
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import io.ktor.routing.get

class ViewShot(windowManager: WindowManager) : FeaturePlugin {

    private val dataProvider = ViewShotProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/view/{screen}/{id}") {
            val screenIndex = call.parameters["screen"]?.toIntOrNull()
            val viewIndex = call.parameters["id"]?.toIntOrNull()
            val result = if (screenIndex != null && viewIndex != null) {
                dataProvider.getViewShot(screenIndex, viewIndex)
            } else null

            if (result != null) {
                call.respondBytes(result, ContentTypes.png, HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
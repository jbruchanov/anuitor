package com.scurab.android.anuitor.feature

import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.Executor
import com.scurab.android.anuitor.tools.render
import com.scurab.android.anuitor.tools.save
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import io.ktor.routing.get

class Screen(private val windowManager: WindowManager) : FeaturePlugin {
    override fun registerRoute(routing: Routing) {
        routing.get("/screen/{index}") {
            val index = call.parameters["index"]?.toIntOrNull() ?: 0
            val rootView = windowManager.getRootView(index)
            if (rootView != null) {
                val data = Executor.runInMainThreadBlockingOnlyIfCrashing {
                    rootView.render(true).save()
                }
                call.respondBytes(data, ContentTypes.png, HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
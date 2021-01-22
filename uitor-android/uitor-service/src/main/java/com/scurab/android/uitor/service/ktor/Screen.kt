package com.scurab.android.uitor.service.ktor

import com.scurab.android.uitor.ContentTypes
import com.scurab.android.uitor.FeaturePlugin
import com.scurab.android.uitor.catching
import com.scurab.android.uitor.reflect.WindowManager
import com.scurab.android.uitor.tools.Executor
import com.scurab.android.uitor.tools.render
import com.scurab.android.uitor.tools.save
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import io.ktor.routing.get

class Screen(private val windowManager: WindowManager) : FeaturePlugin {
    override fun registerRoute(routing: Routing) {
        routing.get("/api/screen/{index}") {
            catching {
                val index = call.parameters["index"]?.toIntOrNull() ?: 0
                val rootView = windowManager.getRootView(index)
                if (rootView != null) {
                    val data = Executor.runInMainThreadBlockingOnlyIfCrashing {
                        rootView.render(true).save()
                    }
                    call.respondBytes(data ?: ByteArray(0), ContentTypes.png, HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}

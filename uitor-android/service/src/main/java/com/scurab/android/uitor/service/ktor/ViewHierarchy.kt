package com.scurab.android.uitor.service.ktor

import com.scurab.android.uitor.ContentTypes
import com.scurab.android.uitor.FeaturePlugin
import com.scurab.android.uitor.catching
import com.scurab.android.uitor.extract2.DetailExtractor
import com.scurab.android.uitor.reflect.WindowManager
import com.scurab.android.uitor.tools.Executor
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

private const val DEFAULT_TIMEOUT = 20000//20s

class ViewHierarchy(private val windowManager: WindowManager) : FeaturePlugin {
    override fun registerRoute(routing: Routing) {
        routing.get("/viewhierarchy/{screenIndex}") {
            catching {
                val screenIndex = call.parameters["screenIndex"]?.toIntOrNull()
                val rootView = screenIndex?.let { windowManager.getRootView(it) }
                if (rootView != null) {
                    val node = Executor.runInMainThreadBlocking(DEFAULT_TIMEOUT) {
                        DetailExtractor.parse(rootView, false)
                    }
                    val json = node.toJson().toString()
                    call.respondText(json, ContentTypes.json, HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}


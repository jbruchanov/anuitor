package com.scurab.android.anuitor.service.ktor

import android.content.Context
import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.tools.FileSystemTools
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.util.pipeline.PipelineInterceptor
import java.io.File

class Storage(
        context: Context,
        private val json: JsonSerializer) : FeaturePlugin {

    private val context = context.applicationContext

    override fun registerRoute(routing: Routing) {
        val handler: PipelineInterceptor<Unit, ApplicationCall> = {
            val path = call.request.queryParameters["path"]?.takeIf { it.isNotEmpty() }
            val file = path?.let { File(it) }

            if (file?.isFile == true) {
                call.respondFile(file)
            } else {
                val files = file
                        ?.let { FileSystemTools.get(it) }
                        ?: FileSystemTools.get(this@Storage.context)
                call.respondText(json.toJson(files), ContentTypes.json, HttpStatusCode.OK)
            }
        }

        routing.get("/storage", handler)
        routing.get("/storage?path={path?}", handler)
    }
}
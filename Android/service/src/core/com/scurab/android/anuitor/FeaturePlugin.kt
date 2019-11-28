package com.scurab.android.anuitor

import com.scurab.android.anuitor.tools.HttpTools
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.util.pipeline.PipelineContext

interface FeaturePlugin {
    fun registerRoute(routing: Routing)
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.catching(block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        call.respond(HttpStatusCode.InternalServerError, e.message ?: "Null exception mess")
    }
}

object ContentTypes {
    val text = io.ktor.http.ContentType.parse(HttpTools.MimeType.TEXT_PLAIN)
    val png = io.ktor.http.ContentType.parse(HttpTools.MimeType.IMAGE_PNG)
    val json = io.ktor.http.ContentType.parse(HttpTools.MimeType.APP_JSON)
}
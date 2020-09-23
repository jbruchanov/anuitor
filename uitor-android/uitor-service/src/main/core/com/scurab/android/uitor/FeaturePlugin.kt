package com.scurab.android.uitor

import com.scurab.android.uitor.tools.HttpTools
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
        val msg = e.message ?: "Null exception msg"
        System.err.println(msg)
        e.printStackTrace(System.err)
        call.respond(HttpStatusCode.InternalServerError, msg)
    }
}

object ContentTypes {
    val text = io.ktor.http.ContentType.parse(HttpTools.MimeType.TEXT_PLAIN)
    val png = io.ktor.http.ContentType.parse(HttpTools.MimeType.IMAGE_PNG)
    val json = io.ktor.http.ContentType.parse(HttpTools.MimeType.APP_JSON)
    val html = io.ktor.http.ContentType.parse(HttpTools.MimeType.TEXT_HTML)
}

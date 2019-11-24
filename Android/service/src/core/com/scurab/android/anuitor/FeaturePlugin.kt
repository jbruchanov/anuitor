package com.scurab.android.anuitor

import com.scurab.android.anuitor.tools.HttpTools
import io.ktor.routing.Routing

interface FeaturePlugin {
    fun registerRoute(routing: Routing)
}

object ContentTypes {
    val text = io.ktor.http.ContentType.parse(HttpTools.MimeType.TEXT_PLAIN)
    val png = io.ktor.http.ContentType.parse(HttpTools.MimeType.IMAGE_PNG)
    val json = io.ktor.http.ContentType.parse(HttpTools.MimeType.APP_JSON)
}
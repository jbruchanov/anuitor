package com.scurab.android.anuitor.service.ktor

import groovy.lang.GrooidShell
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import io.ktor.routing.post
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

//no FeaturePlugin,
//looks like kt compiler can't handle that if :core is not fully independent module
//core src are part of the service to make simply fat aar
class Groovy(tempDir: File) {

    private val contentType = ContentType.parse("application/octet-stream")
    private val shell = GrooidShell(tempDir, Groovy::class.java.classLoader)
    fun registerRoute(routing: Routing) {
        routing.post("/groovy") {
            val result = try {
                val code = call.receiveText()
                shell.evaluateOnMainThread(code).result?.toString() ?: ""
            } catch (t: Throwable) {
                val sw = StringWriter()
                t.printStackTrace(PrintWriter(sw))
                "${t.message}\n${sw}"
                String.format("%s\n%s", t.message, sw)
            }
            call.respondBytes(result.toByteArray(), contentType, HttpStatusCode.OK)
        }
    }
}
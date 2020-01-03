package com.scurab.android.uitor.service.ktor

import groovy.lang.GrooidShellKt
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

//no FeaturePlugin,
//looks like kt compiler can't handle that if :core is not fully independent module
//core src are part of the service to make simply fat aar
class Groovy(tempDir: File) {

    private val contentType = ContentType.parse("application/octet-stream")
    private val shell = GrooidShellKt(tempDir, Groovy::class.java.classLoader
            ?: throw NullPointerException("Missing classloader?!"))

    fun registerRoute(routing: Routing) {
        routing.post("/groovy") {
            val result: String = try {
                call.receiveText().execute()
            } catch (t: Throwable) {
                val sw = StringWriter()
                t.printStackTrace(PrintWriter(sw))
                "${t.message}\n${sw}"
            }
            call.respondBytes(result.toByteArray(), contentType, HttpStatusCode.OK)
        }
    }

    private suspend fun String.execute(): String {
        System.currentTimeMillis()
        return withTimeout(10000) {
            shell.executeAsync(this@execute).await()?.toString() ?: ""
        }
    }
}
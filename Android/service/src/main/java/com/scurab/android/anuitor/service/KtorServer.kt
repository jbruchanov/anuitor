package com.scurab.android.anuitor.service

import android.content.Context
import android.os.Build
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.json.JsonRef
import com.scurab.android.anuitor.reflect.WindowManagerProvider
import com.scurab.android.anuitor.service.ktor.ActiveScreens
import com.scurab.android.anuitor.service.ktor.Config
import com.scurab.android.anuitor.service.ktor.Groovy
import com.scurab.android.anuitor.service.ktor.LogCat
import com.scurab.android.anuitor.service.ktor.Resources
import com.scurab.android.anuitor.service.ktor.Screen
import com.scurab.android.anuitor.service.ktor.ScreenComponents
import com.scurab.android.anuitor.service.ktor.ScreenStructure
import com.scurab.android.anuitor.service.ktor.Storage
import com.scurab.android.anuitor.service.ktor.ViewHierarchy
import com.scurab.android.anuitor.service.ktor.ViewProperty
import com.scurab.android.anuitor.service.ktor.ViewShot
import com.scurab.android.anuitor.tools.ise
import io.ktor.application.call
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respondFile
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File
import java.util.concurrent.TimeUnit

class KtorServer(context: Context) {

    //lambda for lateInit
    private val contextRef = { context.applicationContext }
    private var engine: ApplicationEngine? = null
    private var windowManager = WindowManagerProvider.getManager()
    private var featurePlugins: List<FeaturePlugin>? = null

    var port: Int? = null; private set
    val isRunning get() = engine != null

    fun start(root: String, port: Int = 8080) {
        if (engine != null) {
            ise("Server already running")
        }
        this.port = port
        engine = embeddedServer(Netty, port) {
            routing {
                get("/") { call.respondFile(File(root, "index.html")) }
                static { files(File(root)) }
                val hasGroovySupport = tryRegisterGroovy()
                val plugins = createFeaturePlugins(hasGroovySupport)
                featurePlugins = plugins
                plugins.forEach { it.registerRoute(this) }
            }
        }.start(false)
    }

    private fun Routing.tryRegisterGroovy(): Boolean {
        var hasGroovySupport = false
        //workaround for GroovyPlugin, need for having common module with FeaturePlugin iface
        //as it's single module, let's treat it alone
        kotlin.runCatching {
            //TODO: check more cacheDir sometimes fails on permission_denied exception
            val context = contextRef()
            val tmp: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) context.codeCacheDir else context.cacheDir
            Groovy(tmp).registerRoute(this)
            hasGroovySupport = true
        }
        return hasGroovySupport
    }

    fun stop() {
        kotlin.runCatching {
            featurePlugins = null
            val e = engine
            engine = null
            e?.stop(2000, 2000, TimeUnit.MILLISECONDS)
        }
    }

    private fun createFeaturePlugins(hasGroovySupport: Boolean): List<FeaturePlugin> {
        val json = JsonRef.initJson()

        return mutableListOf<FeaturePlugin>().apply {
            val context = contextRef()
            add(ActiveScreens(windowManager, json))
            add(Config(AnUitorClientConfig.init(context, hasGroovySupport), json))
            add(LogCat())
            add(Resources(windowManager, json))
            add(Screen(windowManager))
            add(ScreenComponents(windowManager, json))
            add(ScreenStructure(windowManager, json))
            add(Storage(context, json))
            add(ViewHierarchy(windowManager))
            add(ViewShot(windowManager))
            add(ViewProperty(windowManager, json))
        }
    }
}
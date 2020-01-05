package com.scurab.android.uitor.service

import android.content.Context
import android.os.Build
import com.scurab.android.uitor.ContentTypes
import com.scurab.android.uitor.FeaturePlugin
import com.scurab.android.uitor.json.JsonRef
import com.scurab.android.uitor.reflect.WindowManagerProvider
import com.scurab.android.uitor.service.ktor.*
import com.scurab.android.uitor.tools.ise
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.combineSafe
import java.io.File

/**
 * Default implementation of Web server
 */
class KtorServer(context: Context) {

    companion object {
        private var engine: ApplicationEngine? = null
    }

    //lambda for lateInit
    private val contextRef = { context.applicationContext }
    private var windowManager = WindowManagerProvider.getManager()
    private var featurePlugins: List<FeaturePlugin>? = null

    var port: Int? = null; private set
    val isRunning get() = engine != null

    /**
     * Start web server
     * [root] root folder of the web server
     * [port] port to start the webserver on, default is 8080
     */
    fun start(root: String, port: Int = 8080) {
        if (engine != null) {
            stop()
        }
        this.port = port
        engine = embeddedServer(Netty, port) {
            //debug logging, need to enable dep in gradle script
            //install(CallLogging) { level = Level.DEBUG }
            routing {
                get("/") {
                    call.respondText(File(root, "index.html").readText(), ContentTypes.html, HttpStatusCode.OK)
                }
                static { supportFiles(File(root)) }
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
            e?.stop(2000, 2000)
        }
    }

    private fun createFeaturePlugins(hasGroovySupport: Boolean): List<FeaturePlugin> {
        val json = JsonRef.initJson()

        return mutableListOf<FeaturePlugin>().apply {
            val context = contextRef()
            add(ActiveScreens(windowManager, json))
            add(Config(UitorClientConfig.init(context, hasGroovySupport), json))
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

/**
 * Necessary workaround for jvm1.6 versions, because of dependency on java's Path API
 */
fun Route.supportFiles(folder: File) {
    fun File?.combine(file: File) = when {
        this == null -> file
        else -> resolve(file)
    }
    val pathParameterName = "static-content-path-parameter"

    val dir = staticRootFolder.combine(folder)
    get("{$pathParameterName...}") {
        val relativePath = call.parameters.getAll(pathParameterName)?.joinToString(File.separator) ?: return@get
        val file = dir.combineSafe(relativePath)
        if (file.isFile) {
            call.respond(file.readBytes())
        }
    }
}
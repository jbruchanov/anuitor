package com.scurab.android.anuitor.feature

import android.graphics.Bitmap
import android.view.ViewGroup
import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Routing
import io.ktor.routing.get

class ViewShot(windowManager: WindowManager) : FeaturePlugin {

    private val dataProvider = ViewShotProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/view/{screen}/{id}") {
            val screenIndex = call.parameters["screen"]?.toIntOrNull()
            val viewIndex = call.parameters["id"]?.toIntOrNull()
            val result = if (screenIndex != null && viewIndex != null) {
                dataProvider.getViewShot(screenIndex, viewIndex)
            } else null

            if (result != null) {
                call.respondBytes(result, ContentTypes.png, HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

class ViewShotProvider(private val windowManager: WindowManager) {
    fun getViewShot(screenIndex: Int, viewIndex: Int): ByteArray? {
        return windowManager.getRootView(screenIndex)?.let { rootView ->
            DetailExtractor.findViewByPosition(rootView, viewIndex)
                    ?.let { view ->
                        synchronized(LOCK) {
                            Executor.runInMainThreadBlockingOnlyIfCrashing {
                                view.takeIf { it is ViewGroup && !DetailExtractor.isExcludedViewGroup(it.javaClass.name) }
                                        ?.let { view.renderBackground() }
                                        ?: view.render()
                            }
                        }?.let {
                            var bitmap = it
                            //TODO: add scaling directly to render
                            val (scaleX, scaleY) = view.absoluteScale()
                            if (scaleX != 1f || scaleY != 1f && it.width > 0 && it.height > 0) {
                                val scaledW = ((it.width * scaleX) + 0.5f).toInt()
                                val scaledH = ((it.height * scaleY) + 0.5f).toInt()
                                if (scaledW > 0 && scaledH > 0) {//just prevention about some nonsense
                                    val scaled = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, false)
                                    it.recycle()
                                    bitmap = scaled
                                }
                            }
                            bitmap.save()
                        } ?: EMPTY
                    }
        }
    }

    companion object {
        //lock rendering to 1 view a time
        //hammering this crashing the app
        private val LOCK = Object()
        private val EMPTY = ByteArray(0)
    }
}
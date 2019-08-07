package com.scurab.android.anuitor.nanoplugin

import android.app.Activity
import android.view.View
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.HttpTools
import fi.iki.elonen.NanoHTTPD
import java.io.File

abstract class ActivityPlugin protected constructor(private val windowManager: WindowManager) : BasePlugin() {

    val currentActivity: Activity
        get() = windowManager.currentActivity

    val currentRootView: View
        get() = getCurrentRootView(-1)

    override fun serveFile(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        val viewRootNames = windowManager.viewRootNames
        return if (viewRootNames == null || viewRootNames.isEmpty()) {
            if (mimeType == HttpTools.MimeType.APP_JSON) {
                OKResponse(HttpTools.MimeType.APP_JSON, "[]")
            } else {
                OKResponse(HttpTools.MimeType.TEXT_PLAIN, APPLICATION_IS_NOT_ACTIVE)
            }
        } else {
            onRequest(uri, headers, session, file, mimeType)
        }
    }

    abstract fun onRequest(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response

    fun getCurrentRootView(index: Int): View {
        return if (index < 0) windowManager.currentRootView else windowManager.getRootView(index)
    }

    protected fun String.currentRootView(): View? {
        return try {
            this
                    .let { HttpTools.parseQueryString(it) }
                    .let { it.currentRootView() }
        } catch (e: Throwable) {
            null
        }
    }

    protected fun Map<String, String>.currentRootView(): View? {
        return try {
            return this
                    .let { (it[SCREEN_INDEX] ?: "-1").toInt() }
                    .let { getCurrentRootView(it) }
        } catch (e: Throwable) {
            null
        }
    }

    companion object {
        const val POSITION = "position"
        const val SCREEN_INDEX = "screenIndex"
        const val APPLICATION_IS_NOT_ACTIVE = "Application is not active"
    }
}

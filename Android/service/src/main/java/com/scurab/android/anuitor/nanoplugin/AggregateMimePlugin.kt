package com.scurab.android.anuitor.nanoplugin

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.lang.NullPointerException
import java.util.HashSet

class AggregateMimePlugin(vararg plugins: BasePlugin) : BasePlugin() {

    private val files: Array<String>
    private val plugins: Array<out BasePlugin>
    private val mimeType: String

    override fun files(): Array<String> = files
    override fun mimeType(): String = mimeType

    init {
        if (plugins.isEmpty()) {
            throw IllegalArgumentException("Plugins can't be empty!")
        }
        this.plugins = plugins
        checkMimeTypes()
        files = mergeFiles(plugins)
        mimeType = plugins.first().mimeType()
    }

    override fun canServeUri(uri: String, rootDir: File): Boolean {
        return getServeCandidate(uri, rootDir) != null
    }

    override fun serveFile(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        //null should never happened, because it's not called if canServerUri returns false
        return (getServeCandidate(uri, file /*TODO: check if it's fine */)
                ?: throw NullPointerException("Unable to handle uri:'$uri'"))
                .serveFile(uri, headers, session, file, mimeType)
    }

    fun getServeCandidate(uri: String, rootDir: File): BasePlugin? {
        return plugins.firstOrNull { it.canServeUri(uri, rootDir) }
    }

    private fun mergeFiles(plugins: Array<out BasePlugin>): Array<String> {
        val files = HashSet<String>()
        for (b in plugins) {
            for (file in b.files()) {
                if (files.contains(file)) {
                    throw IllegalStateException("File:'$file' is already defined from previous plugin")
                }
                files.add(file)
            }
        }
        return files.toTypedArray()
    }

    private fun checkMimeTypes() {
        plugins.map { it.mimeType() }
                .toSet()
                .let {
                    if (it.size != 1) {
                        throw IllegalStateException("Plugins have to have same mime type. MimeType:$it")
                    }
                }

    }
}
package com.scurab.android.anuitor.nanoplugin

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.extract2.ExtractingContext
import com.scurab.android.anuitor.extract2.getActivity
import com.scurab.android.anuitor.reflect.ActivityThreadReflector
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.HttpTools.MimeType.*
import com.scurab.android.anuitor.tools.atLeastApi
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

private const val FILE_SIMPLE = "screencomponents.html"
private typealias Node = Map<String, List<Any>>

class ScreenComponentsPlugin(private val windowManager: WindowManager) : BasePlugin() {

    private val activityThread = ActivityThreadReflector()
    private val files = arrayOf(FILE_SIMPLE)
    private val path = "/$FILE_SIMPLE"

    override fun files(): Array<String> = files
    override fun mimeType(): String = TEXT_HTML
    override fun canServeUri(uri: String, rootDir: File): Boolean = path == uri

    override fun serveFile(uri: String, headers: Map<String, String>,
                           session: NanoHTTPD.IHTTPSession,
                           file: File,
                           mimeType: String): NanoHTTPD.Response {

        return try {
            val simpleStructure = simpleStructure(activityThread.application)
            val sb = StringBuilder()
            val textFormatter = htmlTextFormatError
            simpleStructure.toSimpleString(sb, textFormatter)
            val result = textFormatter.wrap(sb.toString()).toByteArray()
            return OKResponse(textFormatter.mime, ByteArrayInputStream(result))
        } catch (e: Throwable) {
            Response(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    ByteArrayInputStream((e.message ?: "Null exception message").toByteArray()))
        }
    }

    private fun simpleStructure(item: Any): Node {
        val items = mutableListOf<Any>()
        val name = "${item.javaClass.name}@0x${Integer.toHexString(item.hashCode())}"
        val node: Node = mutableMapOf(name to items)
        when (item) {
            is View -> {
                //nothing, just keep name of view
            }
            is Application -> {
                windowManager.viewRootNames
                        .mapNotNull { Pair(it, windowManager.getRootView(it)) }
                        .forEach { (name, item) ->
                            //naive activity detection => app/activity/view
                            if (name.indexOfFirst { it == '/' } != name.indexOfLast { it == '/' }) {
                                //view without activity as a context ?
                                //might be the app or some very unusual case
                                val activity = item.getActivity()
                                if (activity != null) {
                                    items.add(simpleStructure(activity))
                                } else {
                                    items.add(simpleStructure(item))
                                }
                            } else {
                                items.add(simpleStructure(item))
                            }
                        }
            }
            is Activity -> {
                atLeastApi(Build.VERSION_CODES.O) {
                    items.addAll(item.fragmentManager.fragmentsAsNodes())
                }
                if (item is FragmentActivity) {
                    items.addAll(item.supportFragmentManager.fragmentsAsNodes())
                }
            }
            is android.app.Fragment -> {
                atLeastApi(Build.VERSION_CODES.O) {
                    items.addAll(item.childFragmentManager.fragmentsAsNodes())
                }
            }
            is Fragment -> items.addAll(item.childFragmentManager.fragmentsAsNodes())
            else -> throw IllegalArgumentException("Unsupported type:${item.javaClass.name}")
        }
        return node
    }

    private fun FragmentManager.fragmentsAsNodes(): List<Node> {
        return fragments
                .filterNotNull()
                .map { f -> simpleStructure(f) }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun android.app.FragmentManager.fragmentsAsNodes(): List<Node> {
        return fragments
                .filterNotNull()
                .map { f -> simpleStructure(f) }
    }

    private fun Node.toSimpleString(sb: StringBuilder, textFormat: TextFormatter, depth: Int = 0) {
        this.forEach { (name, list) ->
            sb.append(textFormat.space(depth)).append(textFormat.text(name)).append(textFormat.newLine)
            if (list.isNotEmpty()) {
                list.forEach { n ->
                    (n as Node).toSimpleString(sb, textFormat, depth + 1)
                }
            }
        }
    }

    private interface TextFormatter {
        val space: (Int) -> String
        val text: (String) -> String
        val newLine: String
        val mime: String
        val wrap: (String) -> String
    }

    private val simpleTextFormatError = object : TextFormatter {
        override val newLine = "\n"
        override val space: (Int) -> String = { "   ".repeat(it) }
        override val text: (String) -> String = { it }
        override val mime: String = TEXT_PLAIN
        override val wrap: (String) -> String = { it }
    }

    private val htmlTextFormatError = object : TextFormatter {
        private val template = """
            <html><head><style>
            body {
                font: 16px/normal 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
                color: #777;
                font-weight: lighter;
            }
            span {
                color: #000;
                font-weight: bold;
            }
            </style></head>
            <body>%s</body></html>
        """.trimIndent()

        override val newLine = "<br/>"
        override val space: (Int) -> String = { "&nbsp;".repeat(it * 4) }
        override val text: (String) -> String = { it.formattedClass() }
        override val mime: String = TEXT_HTML
        override val wrap: (String) -> String = { template.format(it) }

        private fun String.formattedClass(): String {
            return try {
                val lastDot = this.lastIndexOf(".")
                val hash = this.indexOf("@")
                this.substring(0, lastDot + 1) +
                        "<span>" + this.substring(lastDot + 1, hash) + "</span>" +
                        substring(hash)
            } catch (e: Throwable) {
                this
            }
        }
    }
}
package com.scurab.android.anuitor.feature

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.scurab.android.anuitor.ContentTypes
import com.scurab.android.anuitor.FeaturePlugin
import com.scurab.android.anuitor.extract2.getActivity
import com.scurab.android.anuitor.json.JsonSerializer
import com.scurab.android.anuitor.reflect.ActivityThreadReflector
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.atLeastApi
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

class ScreenComponents(windowManager: WindowManager,
                       private val json: JsonSerializer) : FeaturePlugin {

    private val dataProvider = ScreenComponentsProvider(windowManager)

    override fun registerRoute(routing: Routing) {
        routing.get("/screencomponents") {
            val node = dataProvider.getStructure()
            val json = json.toJson(node)
            call.respondText(json, ContentTypes.json, HttpStatusCode.OK)
        }
    }
}


internal class ScreenComponentsProvider(private val windowManager: WindowManager) {

    private val activityThread = ActivityThreadReflector()

    fun getStructure(): Node {
        return simpleStructure(activityThread.application)
    }

    private fun simpleStructure(item: Any): Node {
        val items = mutableListOf<Node>()
        val name = "${item.javaClass.name}@0x${Integer.toHexString(item.hashCode())}"
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
        return SimpleViewNode(name, items)
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
}

private typealias Node = SimpleViewNode

internal class SimpleViewNode(val name: String, val children: List<SimpleViewNode>)
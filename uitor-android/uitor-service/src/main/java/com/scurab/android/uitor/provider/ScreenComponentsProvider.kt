package com.scurab.android.uitor.provider

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.scurab.android.uitor.extract2.getActivity
import com.scurab.android.uitor.reflect.ActivityThreadReflector
import com.scurab.android.uitor.reflect.WindowManager
import com.scurab.android.uitor.tools.atLeastApi
import com.scurab.android.uitor.tools.ise

internal class ScreenComponentsProvider(private val windowManager: WindowManager) {

    private val activityThread = ActivityThreadReflector()

    fun getStructure(): SimpleViewNode {
        return simpleStructure(activityThread.application)
    }

    private fun simpleStructure(item: Any): Node {
        val items = mutableListOf<Node>()
        val name = "${item.javaClass.name}@0x${Integer.toHexString(item.hashCode())}"
        when (item) {
            is View -> {
                // nothing, just keep name of view
            }
            is Application -> {
                windowManager.viewRootNames
                    ?.mapNotNull { Pair(it, windowManager.getRootView(it)) }
                    ?.filter { it.second != null }
                    ?.forEach { (name, item) ->
                        val item = item ?: ise("Filtered `!= null` and it's null!?")
                        // naive activity detection => app/activity/view
                        if (name.indexOfFirst { it == '/' } != name.indexOfLast { it == '/' }) {
                            // view without activity as a context ?
                            // might be the app or some very unusual case
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

package com.scurab.android.anuitor.extract2

import android.app.Activity
import android.app.Application
import android.view.View
import androidx.fragment.app.Fragment

class ViewComponents(val app: Application, val activity: Activity?) {
    val fragments = mutableListOf<IFragmentDelegate>()
    val fragmentsPerRootView = mutableMapOf<View, IFragmentDelegate>()

    fun findOwnerComponent(view: View): Any? {
        var v: View? = view
        while (v != null) {
            if (fragmentsPerRootView.containsKey(v)) {
                return fragmentsPerRootView[v]
            }
            v = v.parent as? View
        }
        return activity ?: app
    }
}

interface IFragmentDelegate {
    val view: View?
    val name: String
    val fragment: Any
}

class AndroidXFragmentDelegate(override val fragment: Fragment) : IFragmentDelegate {
    override val view: View? get() = fragment.view
    override val name: String = "${fragment.javaClass.name}@${fragment.hashCode().toString(16)}[Tag=${fragment.tag}]"
    override fun toString(): String {
        return name
    }
}

class AndroidFragmentDelegate(override val fragment: android.app.Fragment) : IFragmentDelegate {
    override val view: View? get() = fragment.view
    override val name: String = "${fragment.javaClass.name}@${fragment.hashCode().toString(16)}[Tag=${fragment.tag}]"
    override fun toString(): String {
        return name
    }
}
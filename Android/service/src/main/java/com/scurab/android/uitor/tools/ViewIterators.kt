package com.scurab.android.uitor.tools

import android.view.View
import android.view.ViewGroup

fun View.parentViews(): Iterator<ViewGroup> {
    return ParentViewIterator(this)
}

internal class ParentViewIterator(val view: View) : Iterator<ViewGroup> {
    private var actualView = view
    override fun hasNext(): Boolean {
        return (actualView.parent as? ViewGroup) != null
    }

    override fun next(): ViewGroup {
        val result = actualView.parent as ViewGroup
        actualView = result
        return result
    }
}
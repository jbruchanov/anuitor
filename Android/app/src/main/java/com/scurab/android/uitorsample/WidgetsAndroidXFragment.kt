package com.scurab.android.uitorsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scurab.android.uitorsample.common.BaseFragment

class WidgetsAndroidXFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.widgets_ax, container, false)
    }
}
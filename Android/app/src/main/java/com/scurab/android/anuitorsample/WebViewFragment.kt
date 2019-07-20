package com.scurab.android.anuitorsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.scurab.android.anuitorsample.common.BaseFragment

class WebViewFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return WebView(inflater.context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (view as WebView).apply {
            settings.javaScriptEnabled = true
            loadUrl("https://www.google.com/")
        }
    }
}
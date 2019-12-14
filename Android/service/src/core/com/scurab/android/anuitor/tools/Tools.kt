package com.scurab.android.anuitor.tools

import android.os.Build

inline fun atLeastApi(minApi: Int, function: () -> (Unit)) {
    if (minApi <= Build.VERSION.SDK_INT) {
        function()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ise(msg: String): Nothing {
    throw IllegalStateException(msg)
}
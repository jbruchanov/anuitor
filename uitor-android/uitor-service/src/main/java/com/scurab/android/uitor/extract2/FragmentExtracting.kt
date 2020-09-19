package com.scurab.android.uitor.extract2

import android.app.FragmentManager
import androidx.fragment.app.FragmentManager as AndroidXFragmentManager
import androidx.navigation.NavController
import androidx.navigation.internalExtractBackStack

fun AndroidXFragmentManager.extractBackStack(): List<String?> {
    return (0 until backStackEntryCount)
        .map { getBackStackEntryAt(it) }
        .map { it.toString() }
}

fun FragmentManager.extractBackStack(): List<String?> {
    return (0 until backStackEntryCount)
        .map { getBackStackEntryAt(it) }
        .map { it.toString() }
}

fun NavController.extractBackStack(): List<String>? = internalExtractBackStack(this)

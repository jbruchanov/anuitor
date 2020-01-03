package com.scurab.android.uitorsample.common

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    //internal value to show add own extractor to see interesting values
    val fakePresenter = "Presenter-${this.javaClass.name}"
}
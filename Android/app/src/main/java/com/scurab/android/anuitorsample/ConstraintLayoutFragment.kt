package com.scurab.android.anuitorsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scurab.android.anuitorsample.common.BaseFragment

class ConstraintLayoutFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_constraintlayout, container, false)
    }
}
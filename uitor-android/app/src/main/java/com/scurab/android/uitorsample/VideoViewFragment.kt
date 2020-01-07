package com.scurab.android.uitorsample

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import com.scurab.android.uitorsample.common.BaseFragment


class VideoViewFragment : BaseFragment() {

    private lateinit var videoView: VideoView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoView = view.findViewById(R.id.video)
        view.findViewById<View>(R.id.play).setOnClickListener { onPlayClicked() }
    }

    private fun onPlayClicked() {
        val path = "android.resource://${requireActivity().packageName}/${R.raw.sample_video}"
        videoView.setVideoURI(Uri.parse(path))
        videoView.start()
    }
}
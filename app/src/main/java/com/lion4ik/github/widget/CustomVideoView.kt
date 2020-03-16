package com.lion4ik.github.widget

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr) {

    var uri: Uri? = null

    override fun setVideoURI(uri: Uri?, headers: MutableMap<String, String>?) {
        super.setVideoURI(uri, headers)
        this.uri = uri
    }
}
package com.lion4ik.github.ui.main

import androidx.lifecycle.ViewModel
import com.lion4ik.github.DownloadHelper

class MainViewModel(private val downloadHelper: DownloadHelper) : ViewModel() {

    fun onDownloadClicked(url: String) {
        downloadHelper.downloadFile(url)
    }
}

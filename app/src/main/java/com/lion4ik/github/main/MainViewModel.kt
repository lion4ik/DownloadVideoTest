package com.lion4ik.github.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion4ik.github.download.DownloadErrorHandler
import com.lion4ik.github.download.DownloadHelper
import com.lion4ik.github.download.DownloadStorage

class MainViewModel(
    private val downloadHelper: DownloadHelper,
    private val downloadStorage: DownloadStorage,
    private val downloadErrorHandler: DownloadErrorHandler
) : ViewModel() {

    private val videoUrlMutable: MutableLiveData<String> = MutableLiveData()
    val videoUrl: LiveData<String> = videoUrlMutable

    private val videoUriMutable: MutableLiveData<Uri> = MutableLiveData()
    val videoUri: LiveData<Uri> = videoUriMutable

    private val downloadResultMutable: MutableLiveData<DownloadErrorHandler.DownloadResult> = MutableLiveData()
    val downloadResult: LiveData<DownloadErrorHandler.DownloadResult> = downloadResultMutable

    init {
        downloadErrorHandler.subscribeOnDownload(object : DownloadErrorHandler.DownloadCallback {

            override fun onDownloadCompleted(downloadResult: DownloadErrorHandler.DownloadResult) {
                Log.d("DEBUG", "download id = ${downloadResult.downloadId}")
                downloadResultMutable.value = downloadResult
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        downloadErrorHandler.unsubscribeFromDownload()
    }

    fun onVideoUrlChanged(videoUrl: String) {
        videoUrlMutable.value = videoUrl
    }

    fun onDownloadClicked(url: String) {
        // only http or https urls are valid for downloading
        if (url.startsWith("http://") || url.startsWith("https://")) {
            val downloadId = downloadHelper.downloadFile(url)
            downloadStorage.putDownloadIfAbsent(url, downloadId)
        }
    }

    fun onPlayPauseClicked(url: String) {
        if (downloadStorage.hasDownload(url)) {
            val downloadId = downloadStorage.getDownload(url)
            downloadHelper.getDownload(downloadId)?.let {
                videoUriMutable.value = it
            } ?: run {
                downloadStorage.removeDownload(url)
                videoUriMutable.value = Uri.parse(url)
            }
        } else {
            videoUriMutable.value = Uri.parse(url)
        }
    }
}

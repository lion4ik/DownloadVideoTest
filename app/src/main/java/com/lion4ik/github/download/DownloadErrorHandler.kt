package com.lion4ik.github.download

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter

class DownloadErrorHandler(
    private val downloadBroadcastReceiver: DownloadBroadcastReceiver,
    private val appContext: Context
) {

    fun subscribeOnDownload(downloadCallback: DownloadCallback) {
        downloadBroadcastReceiver.downloadCallback = downloadCallback
        appContext.registerReceiver(downloadBroadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun unsubscribeFromDownload() {
        downloadBroadcastReceiver.downloadCallback = null
        appContext.unregisterReceiver(downloadBroadcastReceiver)
    }

    interface DownloadCallback {
        fun onDownloadCompleted(downloadResult: DownloadResult)
    }

    data class DownloadResult(val status: Int, val reason: Int, val downloadId: Long)
}
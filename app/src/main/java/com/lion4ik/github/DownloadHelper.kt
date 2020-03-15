package com.lion4ik.github

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment

class DownloadHelper(private val downloadManager: DownloadManager) {

    fun downloadFile(url: String): Long {
        val uri = Uri.parse(url)
        val request =
            DownloadManager.Request(uri)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    uri.lastPathSegment
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        return downloadManager.enqueue(request)
    }

    fun getDownload(downloadId: Long): Uri? =
        downloadManager.getUriForDownloadedFile(downloadId)
}
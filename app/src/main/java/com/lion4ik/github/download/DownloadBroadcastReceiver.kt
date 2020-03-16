package com.lion4ik.github.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor

class DownloadBroadcastReceiver(private val downloadManager: DownloadManager) :
    BroadcastReceiver() {

    var downloadCallback: DownloadErrorHandler.DownloadCallback? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
            val downloadId = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, 0
            )
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor: Cursor = downloadManager.query(query)
            cursor.use {
                if (it.moveToFirst()) {
                    val columnStatus: Int = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(columnStatus)
                    val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                    val reason = cursor.getInt(columnReason)
                    downloadCallback?.onDownloadCompleted(
                        DownloadErrorHandler.DownloadResult(
                            status,
                            reason,
                            downloadId
                        )
                    )
                }
            }
        }
    }
}
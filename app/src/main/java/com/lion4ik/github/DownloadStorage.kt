package com.lion4ik.github

import android.content.SharedPreferences

class DownloadStorage(private val sharedPreferences: SharedPreferences) {

    fun putDownload(url: String, id: Long) =
        sharedPreferences.edit().putLong(url, id).apply()

    fun getDownload(url: String): Long =
        sharedPreferences.getLong(url, -1)

    fun removeDownload(url: String) =
        sharedPreferences.edit().remove(url).apply()

    fun hasDownload(url: String): Boolean =
        sharedPreferences.contains(url)
}
package com.lion4ik.github.di

import android.app.DownloadManager
import android.content.Context
import com.lion4ik.github.download.DownloadHelper
import com.lion4ik.github.download.DownloadStorage
import com.lion4ik.github.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val baseModule = module{
    single { androidContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
    single { DownloadHelper(get()) }
    single {
        DownloadStorage(
            androidContext().getSharedPreferences(
                "download_prefs",
                Context.MODE_PRIVATE
            )
        )
    }
    viewModel { MainViewModel(get(), get()) }
}
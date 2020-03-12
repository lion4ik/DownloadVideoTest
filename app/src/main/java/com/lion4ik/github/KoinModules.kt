package com.lion4ik.github

import android.app.DownloadManager
import android.content.Context
import com.lion4ik.github.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val baseModule = module{
    single { androidContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
    single { DownloadHelper(get()) }
    viewModel { MainViewModel(get()) }
}
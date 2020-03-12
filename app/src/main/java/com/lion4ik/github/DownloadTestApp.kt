package com.lion4ik.github

import android.app.Application
import com.lion4ik.github.baseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DownloadTestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Android context
            androidContext(this@DownloadTestApp)
            // modules
            modules(baseModule)
        }
    }
}
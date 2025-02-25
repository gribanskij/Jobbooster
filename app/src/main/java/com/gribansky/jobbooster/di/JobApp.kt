package com.gribansky.jobbooster.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JobApp:Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidLogger()
            androidContext(this@JobApp)
            modules(appModule)
        }

    }

}
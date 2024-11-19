package com.crowd.client.application

import android.app.Application
import android.content.Context
import com.crowd.client.MainVM

class MainApplication: Application() {
    interface AppCompanion {
        fun initialize(context: Context) {}
        fun finish() {}
    }

    override fun onCreate() {
        AppData.initialize(this)
        MainVM().initialize(this)
        super.onCreate()
    }
}
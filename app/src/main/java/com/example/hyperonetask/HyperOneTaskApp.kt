package com.example.hyperonetask

import android.annotation.SuppressLint
import android.content.Context
import com.example.hyperonetask.base.CoreApp
import dagger.hilt.android.HiltAndroidApp

@SuppressLint("StaticFieldLeak")
@HiltAndroidApp
class HyperOneTaskApp : CoreApp() {
    companion object {
        var context: Context? = null
            private set
        var instance: HyperOneTaskApp? = null
            private set

        operator fun get(context: Context): HyperOneTaskApp {
            return context.applicationContext as HyperOneTaskApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = instance?.applicationContext
    }
}
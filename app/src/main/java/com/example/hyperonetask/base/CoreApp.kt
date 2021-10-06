package com.example.hyperonetask.base

import android.content.Context
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.example.hyperonetask.BuildConfig
import es.dmoral.toasty.Toasty
import timber.log.Timber
import java.util.*

open class CoreApp: LocalizationApplication() {

    override fun getDefaultLanguage(base: Context): Locale {
        return Locale.getDefault()
    }

    override fun onCreate() {
        super.onCreate()

        Toasty.Config.getInstance()
                .allowQueue(false)
                .apply()


        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}
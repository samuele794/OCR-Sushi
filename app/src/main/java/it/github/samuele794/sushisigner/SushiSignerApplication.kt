package it.github.samuele794.sushisigner

import android.app.Application
import it.github.samuele794.sushisigner.di.appModule
import it.github.samuele794.sushisigner.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SushiSignerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SushiSignerApplication)
            modules(appModule, dataModule)
        }
    }
}
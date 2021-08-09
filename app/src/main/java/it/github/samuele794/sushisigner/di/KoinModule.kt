package it.github.samuele794.sushisigner.di

import androidx.room.Room
import it.github.samuele794.sushisigner.BuildConfig
import it.github.samuele794.sushisigner.data.repository.AppDatabase
import it.github.samuele794.sushisigner.data.repository.SignerRepository
import it.github.samuele794.sushisigner.data.repository.SignerRepositoryImpl
import it.github.samuele794.sushisigner.viewmodel.camera.CameraDrawViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CameraDrawViewModel(get()) }
}

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            BuildConfig.DB_NAME
        ).build()
    }

    single<SignerRepository> {
        SignerRepositoryImpl(get())
    }
}
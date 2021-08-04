package it.github.samuele794.sushisigner.di

import it.github.samuele794.sushisigner.viewmodel.camera.CameraDrawViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CameraDrawViewModel() }
}
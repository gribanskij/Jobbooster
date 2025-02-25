package com.gribansky.jobbooster.di


import com.gribansky.jobbooster.datastore.IPrefManager
import com.gribansky.jobbooster.datastore.PrefManager
import com.gribansky.jobbooster.net.HhApiImpl
import com.gribansky.jobbooster.net.IhhApi
import com.gribansky.jobbooster.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    factory<IPrefManager> { PrefManager(get()) }
    factory <IhhApi> { HhApiImpl(get()) }
    viewModel { HomeViewModel(get()) }

}
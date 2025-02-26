package com.politefish.expresswaymapdemo

import com.politefish.expresswaymapdemo.domain.GetLocationMatchUseCase
import com.politefish.expresswaymapdemo.domain.locationsearch.LocationSearchTree
import com.politefish.expresswaymapdemo.viewmodel.MainActivityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainActivityViewModel(get()) }
    factory { LocationSearchTree() }
    factory { GetLocationMatchUseCase(get()) }
}
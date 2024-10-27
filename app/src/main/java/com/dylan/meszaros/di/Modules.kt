package com.dylan.meszaros.di

import com.dylan.meszaros.data.ContactRepository
import com.dylan.meszaros.data.ContactRepositoryImpl
import com.dylan.meszaros.viewmodel.ContactViewModel
import org.koin.dsl.module

val appModules = module {
    single<ContactRepository> { ContactRepositoryImpl() }
    single { ContactViewModel(get()) }
}
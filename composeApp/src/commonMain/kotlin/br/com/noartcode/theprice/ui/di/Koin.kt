package br.com.noartcode.theprice.ui.di

import org.koin.core.module.Module

expect fun platformModule() : Module

expect class KoinInitializer {
    fun init()
}
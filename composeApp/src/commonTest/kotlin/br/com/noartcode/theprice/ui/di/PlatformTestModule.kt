package br.com.noartcode.theprice.ui.di

import org.koin.core.module.Module

expect fun platformTestModule() : Module

expect abstract class RobolectricTests()

expect var currentTestFileName: String?
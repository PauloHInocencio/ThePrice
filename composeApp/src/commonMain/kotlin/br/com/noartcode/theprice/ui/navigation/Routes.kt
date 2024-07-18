package br.com.noartcode.theprice.ui.navigation

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.home
import theprice.composeapp.generated.resources.new_bill

@OptIn(ExperimentalResourceApi::class)
enum class Routes(val title: StringResource) {
    HOME(title = Res.string.home),
    NEW_BILL(title = Res.string.new_bill);
}

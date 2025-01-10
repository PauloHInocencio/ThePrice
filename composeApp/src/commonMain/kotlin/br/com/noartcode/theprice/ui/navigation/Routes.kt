package br.com.noartcode.theprice.ui.navigation

import org.jetbrains.compose.resources.StringResource
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.account
import theprice.composeapp.generated.resources.edit_bill
import theprice.composeapp.generated.resources.edit_payment
import theprice.composeapp.generated.resources.home
import theprice.composeapp.generated.resources.new_bill

enum class Routes(val title: StringResource) {
    HOME(title = Res.string.home),
    NEW_BILL(title = Res.string.new_bill),
    EDIT_PAYMENT(title = Res.string.edit_payment),
    EDIT_BILL(title = Res.string.edit_bill),
    ACCOUNT(title = Res.string.account),
}

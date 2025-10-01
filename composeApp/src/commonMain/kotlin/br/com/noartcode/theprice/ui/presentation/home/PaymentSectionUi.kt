package br.com.noartcode.theprice.ui.presentation.home

data class PaymentSectionUi(
    val title:String,
    val dueDay: Int,
    val paymentUis: List<PaymentUi>
)

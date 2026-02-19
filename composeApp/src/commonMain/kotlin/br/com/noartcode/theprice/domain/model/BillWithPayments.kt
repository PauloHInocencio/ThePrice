package br.com.noartcode.theprice.domain.model

data class BillWithPayments(
    val bill: Bill,
    val payments: List<Payment>
)

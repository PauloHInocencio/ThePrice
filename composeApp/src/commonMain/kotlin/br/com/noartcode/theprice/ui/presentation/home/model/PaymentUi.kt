package br.com.noartcode.theprice.ui.presentation.home.model

data class PaymentUi(
    val id:String,
    val statusDescription:String,
    val billName:String,
    val price: String,
    val status: Status

) {
    enum class Status {
        OVERDUE,
        PENDING,
        PAYED,
    }
}


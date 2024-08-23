package br.com.noartcode.theprice.ui.presentation.home.model

data class PaymentUi(
    val id:Long,
    val statusDescription:String,
    val billName:String,
    val price: String,
    val status: Status

) {
    enum class Status {
        PAYED,
        PENDING,
        OVERDUE,
    }
}


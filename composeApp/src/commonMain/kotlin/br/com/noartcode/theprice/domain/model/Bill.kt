package br.com.noartcode.theprice.domain.model

data class Bill(
    val id:Int,
    val title:String,
    val description:String?,
    val value:Float,
    val type: Type,
    val status: Status,
    val invoiceDueDate:String
) {
    enum class Status {
        ACTIVE,
        CANCELLED,
        FINISHED
    }

    enum class Type {
        UNIQUE,
        MONTHLY,
    }
}

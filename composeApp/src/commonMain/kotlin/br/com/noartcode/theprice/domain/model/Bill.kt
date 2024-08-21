package br.com.noartcode.theprice.domain.model

data class Bill(
    val id:Long = -1,
    val name:String = "",
    val description:String? = null,
    val price:Int = 0,
    val type: Type = Type.MONTHLY,
    val status: Status = Status.ACTIVE,
    val invoiceDueDay:Int = 1
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

package br.com.noartcode.theprice.domain.model

data class Bill(
    val id:String = "",
    val name:String = "",
    val description:String? = null,
    val price:Long = 0,
    val type: Type = Type.MONTHLY,
    val status: Status = Status.ACTIVE,
    val billingStartDate: DayMonthAndYear,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced:Boolean,
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

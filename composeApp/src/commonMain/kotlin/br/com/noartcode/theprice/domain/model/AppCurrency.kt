package br.com.noartcode.theprice.domain.model

enum class AppCurrency(
    val code: String,
    val localeTag: String
) {
    USD("USD", "en-US"),
    BRL("BRL", "pt-BR");

    companion object {
        fun fromCode(code:String?) : AppCurrency =
            entries.firstOrNull { it.code == code } ?: USD
    }
}
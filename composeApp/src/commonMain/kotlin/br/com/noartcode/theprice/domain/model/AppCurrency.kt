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

        // Accepts both "pt-BR" and "pt_BR" (iOS locale identifiers use '_')
        fun fromLocaleTag(tag: String?) : AppCurrency {
            val normalized = tag?.replace('_', '-') ?: return USD
            return entries.firstOrNull { it.localeTag.equals(normalized, ignoreCase = true) }
                ?: entries.firstOrNull { it.localeTag.substringBefore('-').equals(normalized.substringBefore('-'), ignoreCase = true) }
                ?: USD
        }
    }
}
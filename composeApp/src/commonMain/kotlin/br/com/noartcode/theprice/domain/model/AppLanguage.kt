package br.com.noartcode.theprice.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    PORTUGUESE("pt");

    companion object {
        fun fromCode(code: String?) : AppLanguage =
            entries.firstOrNull() { it.code == code } ?: ENGLISH
    }
}

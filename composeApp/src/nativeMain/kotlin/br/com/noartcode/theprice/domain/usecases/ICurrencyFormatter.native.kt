package br.com.noartcode.theprice.domain.usecases

import platform.Foundation.NSNumberFormatter

actual class CurrencyFormatter(
    private val formatter: NSNumberFormatter
) : ICurrencyFormatter {

    override fun format(value: Int): String {
        val decimalDigits = 2
        val text = value.toString()
        val thousandsSeparator = formatter.groupingSeparator
        val decimalSeparator = formatter.decimalSeparator
        val currencySymbol = formatter.currencySymbol
        val zero = formatter.zeroSymbol
        /**
        Algorithm overview:

        '1929990' // original input
        '19299' // drop the 2 decimal digits
        '99291' // revert the string so that we can chunk the integer value in the correctly thousands.
        [992][91] // chunk the string into lists of size 3 or less
        '992.91' // join the lists into a string with the current separator
        19.299 // revert the string back
         */
        val intPart = text
            .dropLast(decimalDigits)
            .reversed()
            .chunked(3)
            .joinToString(separator = thousandsSeparator.toString())
            .reversed()
            .ifEmpty {
               zero ?: "0"
            }

        /**
        Algorithm overview:
        '1' // original input
        '1' // take the last 2 digits
        '1' // if the result is not equal to 2
        [0]  // create a list of size (result-size - expected-size) and fill it with zeros
        '01' // then, join the list with de current result
        // if thr result is equal to 2, just return it.
         */
        val fractionPart = text
            .takeLast(decimalDigits)
            .let { result ->
                if (result.length != decimalDigits) {
                    List(decimalDigits - result.length) {
                        zero ?: "0"
                    }.joinToString(separator = "") + result
                } else {
                    result
                }
            }

        return "$currencySymbol $intPart$decimalSeparator$fractionPart"
    }

    override fun clenup(value: String): Int {
        val sb = StringBuilder()
        for(char in value) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
        }

        val s = sb.toString()
        return if (s.isEmpty()) -1  else s.toInt()
    }
}
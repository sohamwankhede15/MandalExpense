package com.example.util

import java.text.NumberFormat
import java.util.Locale

object IndianCurrencyFormatter {
    fun format(amount: Double): String = formatRupees(amount)

    fun format(amount: Number): String = formatRupees(amount.toDouble())

    fun formatRupees(amount: Double): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            formatter.maximumFractionDigits = 0
            val formatted = formatter.format(amount)
            formatted.replace("INR", "₹").trim()
        } catch (_: Exception) {
            "₹%,.0f".format(Locale("en", "IN"), amount)
        }
    }

    fun formatLakhCrore(amount: Double): String {
        return when {
            amount >= 10000000 -> "₹%.2f कोटी".format(amount / 10000000)
            amount >= 100000 -> "₹%.2f लाख".format(amount / 100000)
            amount >= 1000 -> "₹%.1f हजार".format(amount / 1000)
            else -> formatRupees(amount)
        }
    }

    fun formatMarathiNumerals(numberString: String): String {
        val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val marathiDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
        var result = numberString
        for (i in englishDigits.indices) {
            result = result.replace(englishDigits[i], marathiDigits[i])
        }
        return result
    }
}

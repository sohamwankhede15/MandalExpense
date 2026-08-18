package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale("en", "IN"))
    private val displayDateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("en", "IN"))
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val marathiNumericFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val marathiDateTimeFormat = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale("en", "IN"))

    fun formatDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return displayDateTimeFormat.format(Date(timestamp))
    }

    fun formatMarathiDateTime(timestamp: Long): String {
        return marathiDateTimeFormat.format(Date(timestamp))
    }

    fun formatNumericDate(timestamp: Long): String {
        return marathiNumericFormat.format(Date(timestamp))
    }

    fun formatIsoDate(timestamp: Long): String {
        return isoDateFormat.format(Date(timestamp))
    }

    fun getTodayIsoDate(): String {
        return isoDateFormat.format(Date())
    }

    fun isToday(timestamp: Long): Boolean {
        return formatIsoDate(timestamp) == getTodayIsoDate()
    }

    fun isYesterday(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return formatIsoDate(timestamp) == isoDateFormat.format(cal.time)
    }

    fun isThisWeek(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
        val currentYear = cal.get(Calendar.YEAR)
        cal.timeInMillis = timestamp
        return cal.get(Calendar.WEEK_OF_YEAR) == currentWeek && cal.get(Calendar.YEAR) == currentYear
    }

    fun isThisMonth(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        cal.timeInMillis = timestamp
        return cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
    }

    private val marathiMonths = arrayOf(
        "जानेवारी", "फेब्रुवारी", "मार्च", "एप्रिल", "मे", "जून",
        "जुलै", "ऑगस्ट", "सप्टेंबर", "ऑक्टोबर", "नोव्हेंबर", "डिसेंबर"
    )

    fun toMarathiDigits(number: Int): String {
        return toMarathiDigits(number.toString())
    }

    fun toMarathiDigits(input: String): String {
        val marathiDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(marathiDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun parseIsoDate(dateString: String): Date? {
        return try {
            isoDateFormat.parse(dateString)
        } catch (_: Exception) {
            null
        }
    }

    fun formatToMarathiDisplayDate(dateString: String): String {
        return try {
            val date = isoDateFormat.parse(dateString) ?: return dateString
            val cal = Calendar.getInstance()
            cal.time = date
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val monthIndex = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            val monthName = if (monthIndex in 0..11) marathiMonths[monthIndex] else ""
            "${toMarathiDigits(day)} $monthName ${toMarathiDigits(year)}"
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatToShortMarathiDate(dateString: String): String {
        return try {
            val date = isoDateFormat.parse(dateString) ?: return dateString
            val cal = Calendar.getInstance()
            cal.time = date
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val monthIndex = cal.get(Calendar.MONTH)
            val monthName = if (monthIndex in 0..11) marathiMonths[monthIndex] else ""
            "${toMarathiDigits(day)} $monthName"
        } catch (_: Exception) {
            dateString
        }
    }

    fun getDaysUntil(targetDateIso: String): Int {
        return try {
            val today = isoDateFormat.parse(getTodayIsoDate()) ?: return 0
            val target = isoDateFormat.parse(targetDateIso) ?: return 0
            val diffMs = target.time - today.time
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } catch (_: Exception) {
            0
        }
    }

    fun getFestivalDayIndex(dateString: String, festivalStartIso: String): Int {
        return try {
            val start = isoDateFormat.parse(festivalStartIso) ?: return 1
            val current = isoDateFormat.parse(dateString) ?: return 1
            val diffMs = current.time - start.time
            val daysDiff = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            daysDiff + 1
        } catch (_: Exception) {
            1
        }
    }
}

package com.example.util.excel

import android.content.Context
import com.example.data.model.AuditLog
import com.example.data.model.CashReconciliation
import com.example.data.model.DailyClosing
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.MandalSettings
import com.example.data.model.VarganiTransaction
import com.example.util.DateUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object ExcelReportGenerator {

    fun generateVarganiExcel(
        context: Context,
        varganiList: List<VarganiTransaction>,
        settings: MandalSettings,
        filterTitle: String = "सर्व वर्गणी"
    ): File? {
        val file = File(context.cacheDir, "AKGMM_Vargani_Report_${System.currentTimeMillis()}.csv")

        return try {
            val fos = FileOutputStream(file)
            // UTF-8 BOM so MS Excel opens Marathi Devanagari characters cleanly
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

            // Mandal Header
            writer.append("अखिल गणेशनगर मित्र मंडळ (AKGMM) - वर्गणी संकलन यादी\n")
            writer.append("महोत्सव वर्ष: ${settings.festivalYear},नोंदणी क्र.: ${settings.registrationNumber},अहवाल प्रकार: $filterTitle\n\n")

            // Column Headers in Marathi
            writer.append("पावती क्रमांक,नाव,व्यक्तीचा प्रकार,प्रवर्ग / पद,संबंधित घरमालकाचे नाव,मोबाईल क्रमांक,पत्ता / विभाग,रक्कम (₹),पेमेंट पद्धत,दिनांक व वेळ,टीप / संदर्भ\n")

            val activeList = varganiList.filter { !it.isCancelled }

            for (item in activeList) {
                val subcategory = when {
                    item.isOther && item.customCategoryName.isNotBlank() -> item.customCategoryName
                    item.isOther -> item.otherPersonType
                    else -> "-"
                }
                val ownerField = if (item.isTenant && item.ownerName.isNotBlank()) item.ownerName else "-"

                val line = buildString {
                    append("\"${escapeCsv(item.pavtiNumber)}\",")
                    append("\"${escapeCsv(item.contributorName)}\",")
                    append("\"${escapeCsv(item.displayPersonType)}\",")
                    append("\"${escapeCsv(subcategory)}\",")
                    append("\"${escapeCsv(ownerField)}\",")
                    append("\"${escapeCsv(item.mobileNumber)}\",")
                    append("\"${escapeCsv(item.address)}\",")
                    append("${item.amount.toLong()},")
                    append("\"${escapeCsv(item.paymentMode)}\",")
                    append("\"${DateUtils.formatDateTime(item.timestamp)}\",")
                    append("\"${escapeCsv(item.notes)}\"\n")
                }
                writer.append(line)
            }

            // Summary Row
            val totalAmt = activeList.sumOf { it.amount }.toLong()
            val ownerAmt = activeList.filter { it.isOwner }.sumOf { it.amount }.toLong()
            val tenantAmt = activeList.filter { it.isTenant }.sumOf { it.amount }.toLong()
            val otherAmt = activeList.filter { it.isOther }.sumOf { it.amount }.toLong()

            writer.append("\n,,एकूण जमा,,,,,$totalAmt,,,\n")
            writer.append(",,घरमालक जमा,,,,,$ownerAmt,,,\n")
            writer.append(",,भाडेकरू जमा,,,,,$tenantAmt,,,\n")
            writer.append(",,इतर जमा,,,,,$otherAmt,,,\n")

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    fun generateFullAccountingExcel(
        context: Context,
        varganiList: List<VarganiTransaction>,
        expenseList: List<ExpenseTransaction>,
        settings: MandalSettings,
        dailyClosingList: List<DailyClosing> = emptyList(),
        reconciliationList: List<CashReconciliation> = emptyList(),
        auditLogs: List<AuditLog> = emptyList()
    ): File? {
        val file = File(context.cacheDir, "AKGMM_Final_Accounting_Report_${settings.festivalYear}.csv")

        return try {
            val fos = FileOutputStream(file)
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

            val activeVargani = varganiList.filter { !it.isCancelled }
            val activeExpenses = expenseList.filter { !it.isCancelled }

            // Split active expenses into cash/upi vs free
            val cashPaidExpenses = activeExpenses.filter { !it.isFree }
            val freeExpenses = activeExpenses.filter { it.isFree || it.expenseType == "FREE_SPONSORED" }
            val advanceExpenses = activeExpenses.filter { it.expenseType == "ADVANCE" }
            val settledExpenses = activeExpenses.filter { it.expenseType == "FINAL_SETTLEMENT" }
            val mahaprasadExpenses = activeExpenses.filter { it.isMahaprasad || it.category.contains("महाप्रसाद") }

            val totalVargani = activeVargani.sumOf { it.amount }.toLong()
            val ownerVargani = activeVargani.filter { it.isOwner }.sumOf { it.amount }.toLong()
            val tenantVargani = activeVargani.filter { it.isTenant }.sumOf { it.amount }.toLong()
            val otherVargani = activeVargani.filter { it.isOther }.sumOf { it.amount }.toLong()
            val totalExpense = cashPaidExpenses.sumOf { it.amount }.toLong()
            val netBalance = totalVargani - totalExpense

            val totalFreeValue = freeExpenses.sumOf { if (it.totalEstimatedCost > 0) it.totalEstimatedCost else it.amount }.toLong()
            val totalAdvancePaid = advanceExpenses.sumOf { it.amount }.toLong()
            val totalUnsettledAdvance = advanceExpenses.filter { !it.isSettled }.sumOf { it.amount }.toLong()

            val cashVargani = activeVargani.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }.toLong()
            val upiVargani = activeVargani.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }.toLong()
            val cashExpense = cashPaidExpenses.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }.toLong()
            val upiExpense = cashPaidExpenses.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }.toLong()

            // 1. EXECUTIVE SUMMARY
            writer.append("========================================================================\n")
            writer.append("अखिल गणेशनगर मित्र मंडळ (AKGMM) - सर्वंकष अंतिम ताळेबंद अहवाल २०२६\n")
            writer.append("महोत्सव वर्ष: ${settings.festivalYear},नोंदणी क्र.: ${settings.registrationNumber},पत्ता: ${settings.address}\n")
            writer.append("अहवाल दिनांक: ${DateUtils.formatIsoDate(System.currentTimeMillis())}\n")
            writer.append("========================================================================\n\n")

            writer.append("=== १. अंतिम ताळेबंद सारांश (EXECUTIVE SUMMARY) ===\n")
            writer.append("तपशील,संख्या,रक्कम (₹)\n")
            writer.append("एकूण वर्गणी संकलन,${activeVargani.size} पावत्या,$totalVargani\n")
            writer.append("घरमालकांकडून वर्गणी जमा,${activeVargani.count { it.isOwner }} घरमालक,$ownerVargani\n")
            writer.append("भाडेकरूंकडून वर्गणी जमा,${activeVargani.count { it.isTenant }} भाडेकरू,$tenantVargani\n")
            writer.append("इतर देणगीदारांकडून जमा,${activeVargani.count { it.isOther }} इतर,$otherVargani\n")
            writer.append("एकूण प्रत्यक्ष मंडळ खर्च (Outflow),${cashPaidExpenses.size} व्हाउचर्स,$totalExpense\n")
            writer.append("अंतिम शिल्लक निव्वळ रक्कम (Net Balance),,$netBalance\n")
            writer.append("एकूण आगाऊ अदा रक्कम (Advances Paid),${advanceExpenses.size} नोंदी,$totalAdvancePaid\n")
            writer.append("प्रलंबित आगाऊ हिशोब (Unsettled Advances),${advanceExpenses.count { !it.isSettled }} नोंदी,$totalUnsettledAdvance\n")
            writer.append("एकूण मोफत / प्रायोजित मूल्य (Free/Sponsored Value),${freeExpenses.size} प्रायोजक,$totalFreeValue\n\n")

            // 2. PAYMENT MODE RECONCILIATION
            writer.append("=== २. रोख व UPI शिल्लक विभाजन ===\n")
            writer.append("पेमेंट प्रकार,जमा रक्कम (₹),खर्च रक्कम (₹),निव्वळ शिल्लक (₹)\n")
            writer.append("रोख रक्कम (Cash),$cashVargani,$cashExpense,${cashVargani - cashExpense}\n")
            writer.append("बँक / UPI खात्यात,$upiVargani,$upiExpense,${upiVargani - upiExpense}\n\n")

            // 3. ALL VARGANI
            writer.append("=== ३. सर्व वर्गणी संकलन नोंदी ===\n")
            writer.append("पावती क्र.,नाव,प्रकार,प्रवर्ग/पद,संबंधित घरमालक,मोबाईल,रक्कम (₹),पेमेंट,दिनांक,टीप\n")
            for (v in activeVargani) {
                val subcategory = when {
                    v.isOther && v.customCategoryName.isNotBlank() -> v.customCategoryName
                    v.isOther -> v.otherPersonType
                    else -> "-"
                }
                val ownerField = if (v.isTenant && v.ownerName.isNotBlank()) v.ownerName else "-"
                writer.append("\"${escapeCsv(v.pavtiNumber)}\",\"${escapeCsv(v.contributorName)}\",\"${escapeCsv(v.displayPersonType)}\",\"${escapeCsv(subcategory)}\",\"${escapeCsv(ownerField)}\",\"${escapeCsv(v.mobileNumber)}\",${v.amount.toLong()},\"${escapeCsv(v.paymentMode)}\",\"${DateUtils.formatNumericDate(v.timestamp)}\",\"${escapeCsv(v.notes)}\"\n")
            }
            writer.append("\n")

            // 4. EXPENSES (All expenses with new types)
            writer.append("=== ४. सर्व मंडळ खर्च तपशील (सर्व व्हाउचर्स) ===\n")
            writer.append("व्हाउचर क्र.,खर्चाचे नाव,प्रवर्ग,खर्च प्रकार,दुकानदार/प्रायोजक,जबाबदार पदाधिकारी,बिल क्र.,पेमेंट,प्रत्यक्ष अदा रक्कम (₹),आगाऊ वजावट (₹),एकूण खर्च मूल्य (₹),हिशोब स्थिती,दिनांक,टीप\n")
            for (e in activeExpenses) {
                val typeDesc = when (e.expenseType) {
                    "ADVANCE" -> "आगाऊ (Advance)"
                    "FINAL_SETTLEMENT" -> "अंतिम समायोजन (Final Settlement)"
                    "FREE_SPONSORED" -> "मोफत / प्रायोजित (Free)"
                    else -> "सामान्य खर्च"
                }
                val nameParty = if (e.isFree) e.sponsorName else e.paidTo
                val statusDesc = if (e.expenseType == "ADVANCE") (if (e.isSettled) "समायोजित ✓" else "प्रलंबित ⏳") else "-"
                val grossValue = if (e.isFree) (if (e.totalEstimatedCost > 0) e.totalEstimatedCost else e.amount) else (if (e.expenseType == "FINAL_SETTLEMENT") e.totalEstimatedCost else e.amount)

                writer.append("\"${escapeCsv(e.voucherNumber)}\",\"${escapeCsv(e.title)}\",\"${escapeCsv(e.category)}\",\"$typeDesc\",\"${escapeCsv(nameParty)}\",\"${escapeCsv(e.memberAttribution)}\",\"${escapeCsv(e.billReceiptNumber)}\",\"${escapeCsv(e.paymentMode)}\",${if (e.isFree) 0 else e.amount.toLong()},${e.advancePaidAmount.toLong()},${grossValue.toLong()},\"$statusDesc\",\"${DateUtils.formatNumericDate(e.timestamp)}\",\"${escapeCsv(e.notes)}\"\n")
            }
            writer.append("\n")

            // 5. ADVANCE PAYMENTS SPECIAL REPORT
            if (advanceExpenses.isNotEmpty()) {
                writer.append("=== ५. आगाऊ रकमा अहवाल (ADVANCE PAYMENTS) ===\n")
                writer.append("व्हाउचर क्र.,खर्चाचे नाव,प्रवर्ग,दुकानदार/व्यक्ती,जबाबदार पदाधिकारी,आगाऊ रक्कम (₹),अंदाजे एकूण खर्च (₹),हिशोब स्थिती,दिनांक,टीप\n")
                for (a in advanceExpenses) {
                    writer.append("\"${escapeCsv(a.voucherNumber)}\",\"${escapeCsv(a.title)}\",\"${escapeCsv(a.category)}\",\"${escapeCsv(a.paidTo)}\",\"${escapeCsv(a.memberAttribution)}\",${a.amount.toLong()},${a.totalEstimatedCost.toLong()},\"${if (a.isSettled) "समायोजित (Settled ✓)" else "प्रलंबित (Unsettled ⏳)"}\",\"${DateUtils.formatNumericDate(a.timestamp)}\",\"${escapeCsv(a.notes)}\"\n")
                }
                writer.append("\n")
            }

            // 6. FREE & SPONSORED CONTRIBUTIONS
            if (freeExpenses.isNotEmpty()) {
                writer.append("=== ६. मोफत व प्रायोजित योगदान (FREE / SPONSORED) ===\n")
                writer.append("व्हाउचर क्र.,वस्तू / सेवेचे नाव,प्रवर्ग,देणगीदार / प्रायोजक नाव,जबाबदार पदाधिकारी,अंदाजे मूल्य (₹),दिनांक,टीप\n")
                for (f in freeExpenses) {
                    val valEst = if (f.totalEstimatedCost > 0) f.totalEstimatedCost else f.amount
                    writer.append("\"${escapeCsv(f.voucherNumber)}\",\"${escapeCsv(f.title)}\",\"${escapeCsv(f.category)}\",\"${escapeCsv(f.sponsorName)}\",\"${escapeCsv(f.memberAttribution)}\",${valEst.toLong()},\"${DateUtils.formatNumericDate(f.timestamp)}\",\"${escapeCsv(f.notes)}\"\n")
                }
                writer.append("\n")
            }

            // 7. MAHAPRASAD SPECIAL REPORT
            if (mahaprasadExpenses.isNotEmpty()) {
                writer.append("=== ७. महाप्रसाद व अन्नदान विशेष हिशोब (MAHAPRASAD) ===\n")
                writer.append("व्हाउचर क्र.,तपशील / वस्तू,प्रकार,दुकानदार / प्रायोजक,जबाबदार,खर्च रक्कम (₹),प्रायोजित मूल्य (₹),दिनांक,टीप\n")
                for (m in mahaprasadExpenses) {
                    writer.append("\"${escapeCsv(m.voucherNumber)}\",\"${escapeCsv(m.title)}\",\"${if (m.isFree) "प्रायोजित" else "मंडळ खर्च"}\",\"${escapeCsv(if (m.isFree) m.sponsorName else m.paidTo)}\",\"${escapeCsv(m.memberAttribution)}\",${if (m.isFree) 0 else m.amount.toLong()},${if (m.isFree) m.amount.toLong() else 0},\"${DateUtils.formatNumericDate(m.timestamp)}\",\"${escapeCsv(m.notes)}\"\n")
                }
                writer.append("\n")
            }

            // 8. EXPENSE CATEGORY BREAKDOWN
            writer.append("=== ८. खर्च वर्गवारी सारांश (प्रत्यक्ष खर्च) ===\n")
            writer.append("खर्च वर्गवारी,रक्कम (₹),टक्केवारी (%)\n")
            val catMap = cashPaidExpenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount }.toLong() }
            for ((cat, amt) in catMap) {
                val pct = if (totalExpense > 0) (amt.toDouble() / totalExpense * 100).toInt() else 0
                writer.append("\"${escapeCsv(cat)}\",$amt,$pct%\n")
            }
            writer.append("\n")

            // 9. DAILY CLOSINGS
            if (dailyClosingList.isNotEmpty()) {
                writer.append("=== ९. दैनिक हिशोब क्लोजिंग नोंदी ===\n")
                writer.append("तारीख,एकूण जमा (₹),एकूण खर्च (₹),दिवसाची शिल्लक (₹),बंद करणारा,शेरा\n")
                for (d in dailyClosingList) {
                    writer.append("\"${d.dateString}\",${d.totalIncome.toLong()},${d.totalExpenses.toLong()},${d.closingBalance.toLong()},\"${escapeCsv(d.closedBy)}\",\"${escapeCsv(d.notes)}\"\n")
                }
                writer.append("\n")
            }

            // 10. CASH RECONCILIATIONS
            if (reconciliationList.isNotEmpty()) {
                writer.append("=== १०. रोख ताळमेळ नोंदी (CASH RECONCILIATIONS) ===\n")
                writer.append("तारीख व वेळ,सिस्टम रोख (₹),प्रत्यक्ष रोख (₹),तफावत (₹),स्थिती,पडताळणी करणारा,शेरा\n")
                for (r in reconciliationList) {
                    writer.append("\"${DateUtils.formatDateTime(r.timestamp)}\",${r.systemCash.toLong()},${r.physicalCash.toLong()},${r.difference.toLong()},\"${r.status}\",\"${escapeCsv(r.performedBy)}\",\"${escapeCsv(r.notes)}\"\n")
                }
                writer.append("\n")
            }

            // 11. AUDIT HISTORY
            if (auditLogs.isNotEmpty()) {
                writer.append("=== ११. ऑडिट लॉग इतिहास (AUDIT TRAIL) ===\n")
                writer.append("तारीख व वेळ,कृती,प्रकार,नोंद क्र.,कर्ता,तपशील\n")
                for (a in auditLogs) {
                    writer.append("\"${DateUtils.formatDateTime(a.timestamp)}\",\"${escapeCsv(a.action)}\",\"${escapeCsv(a.recordType)}\",\"${escapeCsv(a.recordIdentifier)}\",\"${escapeCsv(a.performedBy)}\",\"${escapeCsv(a.details)}\"\n")
                }
                writer.append("\n")
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    fun generateEventScheduleExcel(
        context: Context,
        eventList: List<FestivalEvent>,
        settings: MandalSettings
    ): File? {
        val file = File(context.cacheDir, "AKGMM_Event_Schedule_${settings.festivalYear}.csv")

        return try {
            val fos = FileOutputStream(file)
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

            writer.append("अखिल गणेशनगर मित्र मंडळ (AKGMM) - ${settings.festivalName} कार्यक्रम वेळापत्रक\n")
            writer.append("महोत्सव वर्ष: ${settings.festivalYear},उत्सव कालावधी: ${settings.festivalStartDate} ते ${settings.festivalEndDate}\n\n")

            writer.append("तारीख,दिवस क्र.,वेळ,कार्यक्रमाचे नाव,कार्यक्रमाचा प्रकार,ठिकाण,जबाबदार सदस्य,मोबाईल,आरतीचे यजमान,यजमान प्रवर्ग,महाप्रसाद कोणाकडून,हार व फुले व्यवस्था,स्थिती,टीप\n")

            for (event in eventList) {
                val dayIdx = DateUtils.getFestivalDayIndex(event.dateString, settings.festivalStartDate)
                val line = buildString {
                    append("\"${DateUtils.formatToMarathiDisplayDate(event.dateString)}\",")
                    append("\"दिवस $dayIdx\",")
                    append("\"${escapeCsv(event.eventTime)}\",")
                    append("\"${escapeCsv(event.eventName)}\",")
                    append("\"${escapeCsv(if (event.programType == "इतर" && event.customProgramType.isNotBlank()) event.customProgramType else event.programType)}\",")
                    append("\"${escapeCsv(event.location)}\",")
                    append("\"${escapeCsv(event.responsibleMember)}\",")
                    append("\"${escapeCsv(event.responsibleMobile)}\",")
                    append("\"${escapeCsv(event.aartiContributorName)}\",")
                    append("\"${escapeCsv(event.aartiContributorType)}\",")
                    append("\"${escapeCsv(event.mahaprasadContributorName)}\",")
                    append("\"${escapeCsv(event.flowerArrangementType)}\",")
                    append("\"${escapeCsv(event.status)}\",")
                    append("\"${escapeCsv(event.notes)}\"\n")
                }
                writer.append(line)
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"")
    }
}

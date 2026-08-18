package com.example.util.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.IncomeTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.PendingVargani
import com.example.data.model.VarganiTransaction
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter
import com.example.util.NumberToWordsConverter
import java.io.File
import java.io.FileOutputStream

object PdfReceiptGenerator {

    private fun drawAkgmmLogo(context: Context, canvas: Canvas, left: Float, top: Float, size: Float) {
        try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_akgmm_logo)
            if (drawable != null) {
                val sizeInt = size.toInt().coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(sizeInt, sizeInt, Bitmap.Config.ARGB_8888)
                val bmpCanvas = Canvas(bitmap)
                drawable.setBounds(0, 0, sizeInt, sizeInt)
                drawable.draw(bmpCanvas)
                canvas.drawBitmap(bitmap, left, top, null)
                return
            }
        } catch (e: Exception) {
            // Fallback
        }

        // Fallback Vector Circle
        val logoCenterX = left + size / 2
        val logoCenterY = top + size / 2
        val logoRadius = size / 2

        val logoBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD600")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(logoCenterX, logoCenterY, logoRadius, logoBorderPaint)

        val whiteRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(logoCenterX, logoCenterY, logoRadius - 2.2f, whiteRingPaint)

        val maroonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4A0E17")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(logoCenterX, logoCenterY, logoRadius - 4.2f, maroonPaint)

        val logoGaneshPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFE082")
            textSize = size * 0.35f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("ॐ", logoCenterX, logoCenterY + size * 0.12f, logoGaneshPaint)
    }

    fun generateVarganiReceiptPdf(
        context: Context,
        vargani: VarganiTransaction,
        settings: MandalSettings
    ): File? {
        return generateReceiptPdf(context, vargani, settings)
    }

    fun generateReceiptPdf(
        context: Context,
        vargani: VarganiTransaction,
        settings: MandalSettings
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 pts)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.parseColor("#E65100") // Warm Orange Accent
        }

        // Draw Outer Border
        val margin = 28f
        val rect = RectF(margin, margin, 595f - margin, 842f - margin)
        canvas.drawRoundRect(rect, 16f, 16f, borderPaint)

        // Outer decorative border
        val innerRect = RectF(margin + 6, margin + 6, 595f - margin - 6, 842f - margin - 6)
        val thinBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.parseColor("#FFA726")
        }
        canvas.drawRoundRect(innerRect, 12f, 12f, thinBorderPaint)

        // Header Background
        val headerRect = RectF(margin + 6, margin + 6, 595f - margin - 6, 175f)
        bgPaint.color = Color.parseColor("#FFF3E0")
        bgPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(headerRect, 12f, 12f, bgPaint)

        // Draw Official AKGMM Circular Logo Emblem at Top Center
        drawAkgmmLogo(context, canvas, (595f - 52f) / 2, 30f, 52f)

        var currentY = 94f

        // AKGMM Pill Badge
        val akgmmBadgeRect = RectF(595f / 2 - 50, currentY - 12, 595f / 2 + 50, currentY + 10)
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E65100")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(akgmmBadgeRect, 6f, 6f, badgePaint)

        paint.color = Color.WHITE
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("AKGMM", 595f / 2, currentY + 4, paint)

        currentY += 26f
        // Mandal Name
        paint.color = Color.parseColor("#BF360C")
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        canvas.drawText("अखिल गणेशनगर मित्र मंडळ", 595f / 2, currentY, paint)

        currentY += 16f
        // Mandal Subtitle & Address
        paint.color = Color.parseColor("#5D4037")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("गणेशनगर हडपसर, पुणे - ४११०६०", 595f / 2, currentY, paint)

        currentY += 15f
        // Festival Year & Reg No
        paint.color = Color.parseColor("#E65100")
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("महोत्सव वर्ष: ${settings.festivalYear}  |  नोंदणी क्र.: ${settings.registrationNumber}", 595f / 2, currentY, paint)

        // Receipt Banner Ribbon: वर्गणी पावती
        currentY += 28f
        val ribbonRect = RectF(595f / 2 - 120, currentY - 16, 595f / 2 + 120, currentY + 10)
        val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E65100")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(ribbonRect, 6f, 6f, ribbonPaint)

        paint.color = Color.WHITE
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("वर्गणी पावती", 595f / 2, currentY + 2, paint)

        // Metadata Row: Pavti No & Date
        currentY += 45f
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#37474F")
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("पावती क्रमांक : ${vargani.pavtiNumber}", margin + 20, currentY, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("दिनांक : ${DateUtils.formatDateTime(vargani.timestamp)}", 595f - margin - 20, currentY, paint)

        // Horizontal Line
        currentY += 15f
        canvas.drawLine(margin + 20, currentY, 595f - margin - 20, currentY, thinBorderPaint)

        // Details Box
        currentY += 30f
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#263238")
        paint.textSize = 12f

        fun drawFieldRow(label: String, value: String) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.parseColor("#546E7A")
            canvas.drawText(label, margin + 20, currentY, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.parseColor("#212121")
            canvas.drawText(value, margin + 200, currentY, paint)
            currentY += 26f
        }

        val typeText = when {
            vargani.isOwner -> "घरमालक (Owner)"
            vargani.isTenant -> "भाडेकरू (Tenant)"
            else -> "इतर (${vargani.displayPersonType})"
        }
        drawFieldRow("व्यक्तीचा प्रकार (Type) :", typeText)
        drawFieldRow(if (vargani.isOwner) "घरमालकाचे नाव :" else if (vargani.isTenant) "भाडेकरूचे नाव :" else "नाव :", vargani.contributorName)
        if (vargani.isTenant && vargani.ownerName.isNotBlank()) {
            drawFieldRow("संबंधित घरमालकाचे नाव :", vargani.ownerName)
        }
        if (vargani.isOther && vargani.customCategoryName.isNotBlank()) {
            drawFieldRow("प्रवर्ग / पद :", vargani.customCategoryName)
        }
        drawFieldRow("मोबाईल क्रमांक :", if (vargani.mobileNumber.isNotBlank()) vargani.mobileNumber else "-")
        drawFieldRow("पत्ता / विभाग :", if (vargani.address.isNotBlank()) vargani.address else "गणेशनगर")
        drawFieldRow("पेमेंट पद्धत :", vargani.paymentMode)
        if (vargani.notes.isNotBlank()) {
            drawFieldRow("विशेष टीप :", vargani.notes)
        }

        // Amount Box (High Contrast Highlight Card)
        currentY += 15f
        val amtBox = RectF(margin + 20, currentY, 595f - margin - 20, currentY + 75)
        val amtBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFF3E0")
            style = Paint.Style.FILL
        }
        val amtBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FB8C00")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        canvas.drawRoundRect(amtBox, 10f, 10f, amtBgPaint)
        canvas.drawRoundRect(amtBox, 10f, 10f, amtBorderPaint)

        // Amount Number
        paint.color = Color.parseColor("#E65100")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("वर्गणी रक्कम : ${IndianCurrencyFormatter.formatRupees(vargani.amount)}", margin + 35, currentY + 32, paint)

        // Amount in Words
        val words = if (vargani.amountInWords.isNotBlank()) {
            vargani.amountInWords
        } else {
            NumberToWordsConverter.convertToMarathi(vargani.amount)
        }
        paint.color = Color.parseColor("#455A64")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("अक्षरी: $words", margin + 35, currentY + 58, paint)

        currentY += 120f

        // Blessings Note
        paint.color = Color.parseColor("#E65100")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("🙏 श्री गणरायाची कृपा आपल्या परिवारावर सदैव राहो हीच सदिच्छा! 🙏", 595f / 2, currentY, paint)

        // Signatures Row
        currentY += 90f
        val leftSignX = margin + 80
        val rightSignX = 595f - margin - 80

        canvas.drawLine(leftSignX - 60, currentY, leftSignX + 60, currentY, thinBorderPaint)
        canvas.drawLine(rightSignX - 60, currentY, rightSignX + 60, currentY, thinBorderPaint)

        paint.textSize = 10f
        paint.color = Color.parseColor("#37474F")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(if (vargani.collectedBy.isNotBlank()) vargani.collectedBy else "स्वीकारणारा / प्रतिनिधी", leftSignX, currentY + 16, paint)
        canvas.drawText(settings.authorizedSignatory, rightSignX, currentY + 16, paint)
        canvas.drawText("(अधिकृत स्वाक्षरी)", rightSignX, currentY + 30, paint)

        // Footer Note
        val footerY = 842f - margin - 20
        paint.textSize = 8.5f
        paint.color = Color.parseColor("#9E9E9E")
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("AKGMM अधिकृत डिजिटल पावती. संपर्क: ${settings.contactNumber} | UPI: ${settings.upiId}", 595f / 2, footerY, paint)

        pdfDocument.finishPage(page)

        // Save PDF to cache dir
        val file = File(context.cacheDir, "AKGMM_Pavti_${vargani.pavtiNumber}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.flush()
            fos.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    fun generateFinancialReportPdf(
        context: Context,
        settings: MandalSettings,
        varganiList: List<VarganiTransaction>,
        expenseList: List<ExpenseTransaction>,
        incomeList: List<IncomeTransaction>,
        pendingList: List<PendingVargani> = emptyList()
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val margin = 24f

        // Header Logo Emblem
        drawAkgmmLogo(context, canvas, 24f, 26f, 44f)

        // Header Titles
        paint.color = Color.parseColor("#BF360C")
        paint.textSize = 17f
        paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("AKGMM - अखिल गणेशनगर मित्र मंडळ", 595f / 2 + 10, 42f, paint)

        paint.color = Color.parseColor("#E65100")
        paint.textSize = 10.5f
        canvas.drawText("गणेशनगर हडपसर, पुणे - ४११०६० • वार्षिक हिशोब व ताळेबंद अहवाल (${settings.festivalYear})", 595f / 2 + 10, 60f, paint)

        var currentY = 85f
        val totalVargani = varganiList.filter { !it.isCancelled }.sumOf { it.amount }
        val ownerVargani = varganiList.filter { !it.isCancelled && it.isOwner }.sumOf { it.amount }
        val tenantVargani = varganiList.filter { !it.isCancelled && it.isTenant }.sumOf { it.amount }
        val otherVargani = varganiList.filter { !it.isCancelled && it.isOther }.sumOf { it.amount }
        val totalOtherIncome = incomeList.sumOf { it.amount }
        val totalIncome = totalVargani + totalOtherIncome
        val totalExpenses = expenseList.filter { !it.isFree }.sumOf { it.amount }
        val freeExpensesTotal = expenseList.filter { it.isFree }.sumOf { if (it.totalEstimatedCost > 0) it.totalEstimatedCost else it.amount }
        val balance = totalIncome - totalExpenses

        // Summary Table Box
        val summaryBox = RectF(margin, currentY, 595f - margin, currentY + 105)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFF3E0")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(summaryBox, 8f, 8f, bgPaint)

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 10.5f
        paint.color = Color.parseColor("#2E7D32")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("एकूण वर्गणी जमा (${varganiList.size} पावत्या): ${IndianCurrencyFormatter.formatRupees(totalVargani)}", margin + 12, currentY + 20, paint)
        paint.textSize = 9f
        canvas.drawText("• घरमालक: ${IndianCurrencyFormatter.formatRupees(ownerVargani)} | • भाडेकरू: ${IndianCurrencyFormatter.formatRupees(tenantVargani)} | • इतर: ${IndianCurrencyFormatter.formatRupees(otherVargani)}", margin + 12, currentY + 36, paint)

        paint.color = Color.parseColor("#C62828")
        paint.textSize = 10.5f
        canvas.drawText("एकूण खर्च (${expenseList.size} व्हाउचर): ${IndianCurrencyFormatter.formatRupees(totalExpenses)}", margin + 12, currentY + 58, paint)

        paint.color = if (balance >= 0) Color.parseColor("#1565C0") else Color.parseColor("#D32F2F")
        paint.textSize = 12f
        canvas.drawText("शिल्लक रक्कम (Net Balance): ${IndianCurrencyFormatter.formatRupees(balance)}", margin + 12, currentY + 84, paint)

        currentY += 130f
        paint.color = Color.parseColor("#212121")
        paint.textSize = 11.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("वर्गणी नोंदी तपशील (पावती क्रमांक | नाव | प्रकार | मालक/प्रवर्ग | रक्कम):", margin, currentY, paint)

        currentY += 18f
        paint.textSize = 8.5f
        varganiList.take(20).forEach { item ->
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textAlign = Paint.Align.LEFT
            val extraTag = when {
                item.isTenant && item.ownerName.isNotBlank() -> " (घरमालक: ${item.ownerName})"
                item.isOther && item.customCategoryName.isNotBlank() -> " (${item.customCategoryName})"
                item.isOther -> " (${item.otherPersonType})"
                else -> ""
            }
            val text = "${item.pavtiNumber} | ${item.contributorName} | ${item.displayPersonType}$extraTag | ${DateUtils.formatNumericDate(item.timestamp)}"
            canvas.drawText(text, margin, currentY, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(IndianCurrencyFormatter.formatRupees(item.amount), 595f - margin, currentY, paint)
            currentY += 15f
        }

        currentY += 15f
        paint.color = Color.parseColor("#212121")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("खर्च नोंदी तपशील:", margin, currentY, paint)

        currentY += 18f
        paint.textSize = 8.5f
        expenseList.take(8).forEach { item ->
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textAlign = Paint.Align.LEFT
            val text = "${item.voucherNumber} | ${item.title} (${item.category}) | ${DateUtils.formatNumericDate(item.timestamp)}"
            canvas.drawText(text, margin, currentY, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(IndianCurrencyFormatter.formatRupees(item.amount), 595f - margin, currentY, paint)
            currentY += 15f
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "AKGMM_Taleband_Report_${settings.festivalYear}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.flush()
            fos.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    fun generateEventSchedulePdf(
        context: Context,
        eventList: List<FestivalEvent>,
        settings: MandalSettings
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw Official AKGMM Logo
        drawAkgmmLogo(context, canvas, 35f, 25f, 52f)

        // Header Text
        paint.color = Color.parseColor("#C2410C")
        paint.textSize = 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(settings.mandalName, 95f, 42f, paint)

        paint.color = Color.parseColor("#616161")
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${settings.subTitle} | नोंदणी क्र: ${settings.registrationNumber}", 95f, 57f, paint)
        canvas.drawText("${settings.festivalName} • कार्यक्रम वेळापत्रक (${settings.festivalStartDate} ते ${settings.festivalEndDate})", 95f, 72f, paint)

        // Orange Header Accent Bar
        paint.color = Color.parseColor("#FFD8B2")
        canvas.drawRect(35f, 85f, 560f, 88f, paint)

        var currentY = 105f
        val margin = 35f
        val rightMargin = 560f

        if (eventList.isEmpty()) {
            paint.color = Color.parseColor("#757575")
            paint.textSize = 12f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("कोणताही कार्यक्रम नियोजित नाही.", 595f / 2f, 200f, paint)
        } else {
            // Group events by date
            val grouped = eventList.groupBy { it.dateString }

            grouped.forEach { (dateStr, events) ->
                if (currentY > 750f) return@forEach // Fit comfortably on page

                // Date Group Header
                val dayIdx = DateUtils.getFestivalDayIndex(dateStr, settings.festivalStartDate)
                val dateDisplay = DateUtils.formatToMarathiDisplayDate(dateStr)

                val dateBoxRect = RectF(margin, currentY, rightMargin, currentY + 22f)
                paint.color = Color.parseColor("#FFF3E0")
                paint.style = Paint.Style.FILL
                canvas.drawRoundRect(dateBoxRect, 4f, 4f, paint)

                paint.color = Color.parseColor("#E65100")
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1f
                canvas.drawRoundRect(dateBoxRect, 4f, 4f, paint)

                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#BF360C")
                paint.textSize = 10.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText("दिवस $dayIdx : $dateDisplay", margin + 10f, currentY + 15f, paint)

                currentY += 28f

                events.forEach { event ->
                    if (currentY > 780f) return@forEach

                    // Event Card Box
                    val eventBox = RectF(margin + 10f, currentY, rightMargin, currentY + 36f)
                    paint.color = Color.parseColor("#FAFAFA")
                    paint.style = Paint.Style.FILL
                    canvas.drawRoundRect(eventBox, 3f, 3f, paint)

                    paint.color = Color.parseColor("#E0E0E0")
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 0.5f
                    canvas.drawRoundRect(eventBox, 3f, 3f, paint)

                    // Event Name & Time
                    paint.style = Paint.Style.FILL
                    paint.color = Color.parseColor("#212121")
                    paint.textSize = 10f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText("${event.eventTime} - ${event.eventName}", margin + 18f, currentY + 14f, paint)

                    // Program Type Badge
                    val prgType = if (event.programType == "इतर" && event.customProgramType.isNotBlank()) event.customProgramType else event.programType
                    paint.color = Color.parseColor("#C2410C")
                    paint.textSize = 9f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    canvas.drawText("[$prgType]", margin + 240f, currentY + 14f, paint)

                    // Responsible Person & Aarti Contributor
                    paint.color = Color.parseColor("#616161")
                    paint.textSize = 8.5f
                    val respText = if (event.responsibleMember.isNotBlank()) "जबाबदार: ${event.responsibleMember}" else ""
                    val aartiText = if (event.aartiContributorName.isNotBlank()) "आरती: ${event.aartiContributorName} (${event.aartiContributorType})" else ""
                    val line2 = listOfNotNull(respText.ifBlank { null }, aartiText.ifBlank { null }).joinToString(" | ")
                    if (line2.isNotBlank()) {
                        canvas.drawText(line2, margin + 18f, currentY + 28f, paint)
                    }

                    // Status
                    paint.color = when (event.status) {
                        "पूर्ण" -> Color.parseColor("#2E7D32")
                        "तयारी सुरू" -> Color.parseColor("#E65100")
                        "रद्द" -> Color.parseColor("#C62828")
                        else -> Color.parseColor("#1565C0")
                    }
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(event.status, rightMargin - 10f, currentY + 14f, paint)

                    currentY += 42f
                }
                currentY += 6f
            }
        }

        // Footer
        paint.color = Color.parseColor("#9E9E9E")
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("अखिल गणेशनगर मित्र मंडळ (AKGMM) • सर्व हक्क राखीव • अहवाल निर्मिती: ${DateUtils.formatDateTime(System.currentTimeMillis())}", 595f / 2f, 820f, paint)

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "AKGMM_Event_Schedule_${settings.festivalYear}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.flush()
            fos.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }
}

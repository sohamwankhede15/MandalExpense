package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.MandalSettings
import com.example.data.model.VarganiTransaction
import java.io.File
import java.net.URLEncoder

object ShareHelper {

    fun shareWhatsAppReceipt(
        context: Context,
        vargani: VarganiTransaction,
        settings: MandalSettings
    ) {
        val typeBadge = when {
            vargani.isOwner -> "🏠 *व्यक्तीचा प्रकार:* घरमालक"
            vargani.isTenant -> "🏢 *व्यक्तीचा प्रकार:* भाडेकरू"
            else -> "⭐ *व्यक्तीचा प्रकार:* इतर (${vargani.displayPersonType})"
        }
        val ownerInfo = if (vargani.isTenant && vargani.ownerName.isNotBlank()) "\n👤 *संबंधित घरमालक:* ${vargani.ownerName}" else ""
        val categoryInfo = if (vargani.isOther && vargani.customCategoryName.isNotBlank()) "\n🏷️ *प्रवर्ग:* ${vargani.customCategoryName}" else ""
        val mobileInfo = if (vargani.mobileNumber.isNotBlank()) "\n📱 *मोबाईल:* ${vargani.mobileNumber}" else ""
        val addressInfo = if (vargani.address.isNotBlank()) "\n📍 *पत्ता:* ${vargani.address}" else ""

        val text = """
🚩 *अखिल गणेशनगर मित्र मंडळ (AKGMM)* 🚩
*गणेशनगर हडपसर, पुणे - ४११०२८*
*महोत्सव वर्ष:* ${settings.festivalYear} | *नोंदणी क्र.:* ${settings.registrationNumber}
───────────────────────
📜 *अधिकृत डिजिटल देणगी / वर्गणी पावती*
───────────────────────
🔢 *पावती क्र.:* ${vargani.pavtiNumber}
📅 *दिनांक:* ${DateUtils.formatDateTime(vargani.timestamp)}
$typeBadge
👤 *नाव:* ${vargani.contributorName}$ownerInfo$categoryInfo$mobileInfo$addressInfo
💰 *वर्गणी रक्कम:* *${IndianCurrencyFormatter.formatRupees(vargani.amount)}*
(${if (vargani.amountInWords.isNotBlank()) vargani.amountInWords else NumberToWordsConverter.convertToMarathi(vargani.amount)})
💳 *पेमेंट पद्धत:* ${vargani.paymentMode}
───────────────────────
🙏 *श्री गणरायाची कृपा आपल्या परिवारावर सदैव राहो!*
*आपल्या मोलाच्या सहकार्याबद्दल मनःपूर्वक धन्यवाद!* 🌺

_AKGMM अधिकृत ई-पावती_
        """.trimIndent()

        try {
            val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                val encodedText = URLEncoder.encode(text, "UTF-8")
                val cleanMobile = vargani.mobileNumber.filter { it.isDigit() }
                if (cleanMobile.length == 10) {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=91$cleanMobile&text=$encodedText")
                } else {
                    data = Uri.parse("https://api.whatsapp.com/send?text=$encodedText")
                }
            }
            context.startActivity(sendIntent)
        } catch (_: Exception) {
            // Fallback to standard share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(shareIntent, "पावती शेअर करा"))
        }
    }

    fun shareWhatsAppSummary(
        context: Context,
        settings: MandalSettings,
        totalVargani: Double,
        totalOwnerVargani: Double,
        totalTenantVargani: Double,
        totalOtherVargani: Double = 0.0,
        ownerCount: Int,
        tenantCount: Int,
        otherCount: Int = 0,
        totalExpenses: Double,
        netBalance: Double
    ) {
        val text = """
🚩 *अखिल गणेशनगर मित्र मंडळ (AKGMM)* 🚩
*दैनिक / वार्षिक हिशोब व ताळेबंद अहवाल (${settings.festivalYear})*
───────────────────────
📊 *वर्गणी संकलन तपशील:*
• एकूण वर्गणी जमा: *${IndianCurrencyFormatter.formatRupees(totalVargani)}*
  - 🏠 घरमालकांकडून: ${IndianCurrencyFormatter.formatRupees(totalOwnerVargani)} ($ownerCount घरमालक)
  - 🏢 भाडेकरूंकडून: ${IndianCurrencyFormatter.formatRupees(totalTenantVargani)} ($tenantCount भाडेकरू)
${if (otherCount > 0 || totalOtherVargani > 0) "  - ⭐ इतर देणगीदारांकडून: ${IndianCurrencyFormatter.formatRupees(totalOtherVargani)} ($otherCount देणगीदार)\n" else ""}
📉 *मंडळ खर्च व शिल्लक:*
• एकूण मंडळ खर्च: *${IndianCurrencyFormatter.formatRupees(totalExpenses)}*
• शिल्लक निव्वळ रक्कम: *${IndianCurrencyFormatter.formatRupees(netBalance)}*
───────────────────────
गणपती बाप्पा मोर्या! 🌺
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "अहवाल शेअर करा"))
    }

    fun sharePdfFile(context: Context, file: File, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "PDF शेअर करताना त्रुटी: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareExcelFile(context: Context, file: File, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "Excel फाईल शेअर करताना त्रुटी: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "फाईल शेअर करताना त्रुटी: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(context: Context, text: String, title: String = "शेअर करा") {
        shareGeneralText(context, text, title)
    }

    fun shareGeneralText(context: Context, text: String, title: String = "शेअर करा") {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(sendIntent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "शेअर करताना त्रुटी: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}

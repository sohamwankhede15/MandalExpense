package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExpenseTransaction
import com.example.data.model.IncomeTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.VarganiTransaction
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangeBackground
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedExpense
import com.example.ui.theme.TextOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter

@Composable
fun FinalReportDialog(
    settings: MandalSettings,
    varganiList: List<VarganiTransaction>,
    expenseList: List<ExpenseTransaction>,
    incomeList: List<IncomeTransaction>,
    totalVargani: Double,
    totalOwnerVargani: Double,
    totalTenantVargani: Double,
    ownerCount: Int,
    tenantCount: Int,
    totalExpense: Double,
    netBalance: Double,
    totalCashVargani: Double,
    totalUpiVargani: Double,
    totalCashExpense: Double,
    totalUpiExpense: Double,
    netCashInHand: Double,
    netUpiInBank: Double,
    onExportPdf: () -> Unit,
    onExportExcel: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onDismiss: () -> Unit
) {
    val activeVargani = remember(varganiList) { varganiList.filter { !it.isCancelled } }
    val activeExpenses = remember(expenseList) { expenseList.filter { !it.isCancelled } }

    // Category breakdown for expenses
    val categoryExpenses = remember(activeExpenses) {
        activeExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    // Date-wise summary
    val dateWiseSummary = remember(activeVargani, activeExpenses) {
        val allDates = (activeVargani.map { DateUtils.formatIsoDate(it.timestamp) } +
                activeExpenses.map { DateUtils.formatIsoDate(it.timestamp) }).distinct().sorted()

        allDates.map { date ->
            val vTotal = activeVargani.filter { DateUtils.formatIsoDate(it.timestamp) == date }.sumOf { it.amount }
            val eTotal = activeExpenses.filter { DateUtils.formatIsoDate(it.timestamp) == date }.sumOf { it.amount }
            val vCount = activeVargani.count { DateUtils.formatIsoDate(it.timestamp) == date }
            Triple(date, Pair(vTotal, vCount), eTotal)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(700.dp)
                .testTag("final_report_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = OrangePrimaryDark,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "अंतिम हिशोब अहवाल २०२६",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "AKGMM सर्वंकष ताळेबंद व लेखापरीक्षण",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_final_report_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Mandal Info Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = OrangeBackground,
                        border = BorderStroke(1.dp, OrangeBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "अखिल गणेश नगर मित्र मंडळ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark
                            )
                            Text(
                                text = "गणेशोत्सव वर्ष: ${settings.festivalYear} | पत्ता: गणेश नगर, पुणे - ४११०६०",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("अध्यक्ष: ${settings.presidentName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("खजिनदार: ${settings.treasurerName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Final Net Financial Status Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (netBalance >= 0) GreenContainer else RedContainer,
                        border = BorderStroke(1.dp, if (netBalance >= 0) GreenSuccess else RedExpense),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("अंतिम निव्वळ ताळेबंद (Final Net Balance)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (netBalance >= 0) GreenSuccess else RedExpense)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = IndianCurrencyFormatter.format(netBalance),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = if (netBalance >= 0) GreenSuccess else RedExpense
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("एकूण उत्पन्न: ${IndianCurrencyFormatter.format(totalVargani)}", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("एकूण खर्च: ${IndianCurrencyFormatter.format(totalExpense)}", fontSize = 12.sp, color = RedExpense, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Vargani Overview Section
                    Text("१. वर्गणी सारांश", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WhiteCard,
                        border = BorderStroke(1.dp, OrangeBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("एकूण पावत्या संख्या:", fontSize = 12.sp, color = TextSecondary)
                                Text("${activeVargani.size} पावत्या", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = OrangeBorder.copy(alpha = 0.5f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("मालकांकडून जमा:", fontSize = 12.sp, color = TextSecondary)
                                Text("${IndianCurrencyFormatter.format(totalOwnerVargani)} ($ownerCount मालक)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = OrangeBorder.copy(alpha = 0.5f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("भाडेकरूंकडून जमा:", fontSize = 12.sp, color = TextSecondary)
                                Text("${IndianCurrencyFormatter.format(totalTenantVargani)} ($tenantCount भाडेकरू)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = OrangeBorder.copy(alpha = 0.5f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("एकूण वर्गणी रक्कम:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(IndianCurrencyFormatter.format(totalVargani), fontSize = 14.sp, fontWeight = FontWeight.Black, color = GreenSuccess)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Payment Breakdown (Cash vs UPI)
                    Text("२. रोख व UPI विभाजन", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFFDF8),
                        border = BorderStroke(1.dp, OrangeBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("💵 रोख वर्गणी जमा:", fontSize = 12.sp, color = TextSecondary)
                                Text(IndianCurrencyFormatter.format(totalCashVargani), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("💵 रोख खर्च:", fontSize = 12.sp, color = TextSecondary)
                                Text(IndianCurrencyFormatter.format(totalCashExpense), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("💵 शिल्लक रोख रक्कम (Cash In Hand):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryDark)
                                Text(IndianCurrencyFormatter.format(netCashInHand), fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (netCashInHand >= 0) GreenSuccess else RedExpense)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = OrangeBorder)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("📱 UPI वर्गणी जमा:", fontSize = 12.sp, color = TextSecondary)
                                Text(IndianCurrencyFormatter.format(totalUpiVargani), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("📱 UPI खर्च:", fontSize = 12.sp, color = TextSecondary)
                                Text(IndianCurrencyFormatter.format(totalUpiExpense), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("📱 बँक / UPI शिल्लक (Bank Balance):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlueInfo)
                                Text(IndianCurrencyFormatter.format(netUpiInBank), fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (netUpiInBank >= 0) GreenSuccess else RedExpense)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expense Category Breakdown
                    Text("३. खर्च वर्गवारी (Category Breakdown)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WhiteCard,
                        border = BorderStroke(1.dp, OrangeBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (categoryExpenses.isEmpty()) {
                                Text("कोणताही खर्च नोंदवलेला नाही.", fontSize = 12.sp, color = TextSecondary)
                            } else {
                                categoryExpenses.forEachIndexed { index, (cat, amt) ->
                                    val percent = if (totalExpense > 0) (amt / totalExpense * 100).toInt() else 0
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("• $cat ($percent%)", fontSize = 12.sp, color = TextPrimary)
                                        Text(IndianCurrencyFormatter.format(amt), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                                    }
                                    if (index < categoryExpenses.size - 1) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = OrangeBorder.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date-wise Summary Table
                    Text("४. दैनिक सारांश तक्ता", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFFDF8),
                        border = BorderStroke(1.dp, OrangeBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("तारीख", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1f))
                                Text("जमा", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenSuccess, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                                Text("खर्च", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RedExpense, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = OrangeBorder)
                            dateWiseSummary.forEach { (date, vInfo, eAmt) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(date, fontSize = 11.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Text(IndianCurrencyFormatter.format(vInfo.first), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GreenSuccess, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                                    Text(IndianCurrencyFormatter.format(eAmt), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = RedExpense, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons (PDF, Excel, WhatsApp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_final_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📄 PDF अहवाल", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onExportExcel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_final_excel_button")
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📊 Excel अहवाल", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onShareWhatsApp,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                    border = BorderStroke(1.dp, Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("share_final_whatsapp_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📱 WhatsApp वर अंतिम सारांश पाठवा", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

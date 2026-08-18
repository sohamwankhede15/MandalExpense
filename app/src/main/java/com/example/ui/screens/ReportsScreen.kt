package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard
import com.example.util.IndianCurrencyFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportsScreen(
    settings: MandalSettings,
    varganiList: List<VarganiTransaction>,
    expenseList: List<ExpenseTransaction>,
    incomeList: List<IncomeTransaction>,
    totalVargani: Double,
    totalOwnerVargani: Double,
    totalTenantVargani: Double,
    totalOtherVargani: Double = 0.0,
    ownerCount: Int,
    tenantCount: Int,
    otherCount: Int = 0,
    totalExpense: Double,
    netBalance: Double,
    onOpenFinalReport: () -> Unit = {},
    onOpenDailyClosing: () -> Unit = {},
    onOpenReconciliation: () -> Unit = {},
    onOpenAuditHistory: () -> Unit = {},
    onExportPdfReport: () -> Unit,
    onExportAllVarganiExcel: () -> Unit,
    onExportOwnerVarganiExcel: () -> Unit,
    onExportTenantVarganiExcel: () -> Unit,
    onExportOtherVarganiExcel: () -> Unit = {},
    onExportFullAccountingExcel: () -> Unit,
    onShareSummaryWhatsApp: () -> Unit
) {
    val activeVargani = varganiList.filter { !it.isCancelled }
    val activeExpenses = expenseList.filter { !it.isCancelled }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangeBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // REPORT TITLE HEADER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reports_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, OrangeBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OrangeContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = OrangePrimaryDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "हिशोब व ताळेबंद अहवाल",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "AKGMM वर्ष: ${settings.festivalYear} • Excel व PDF डाऊनलोड",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // ONE-TAP FINAL FESTIVAL REPORT PROMINENT CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenFinalReport)
                    .testTag("reports_open_final_report_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OrangePrimaryDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 अंतिम हिशोब तयार करा (Final Report)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text("1-Tap", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "अध्यक्ष व खजिनदार स्वाक्षरीसह PDF अहवाल, ९-शीट Excel हिशोब वही आणि WhatsApp अहवाल तयार करा.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onOpenFinalReport,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("सर्वंकष अंतिम अहवाल पहा", color = OrangePrimaryDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // TALEBAND SUMMARY CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                border = BorderStroke(1.5.dp, OrangeBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "वार्षिक ताळेबंद सारांश",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: Total Vargani
                    ReportStatLine(
                        label = "एकूण वर्गणी संकलन (${activeVargani.size} पावत्या)",
                        value = IndianCurrencyFormatter.formatRupees(totalVargani),
                        color = OrangePrimaryDark
                    )

                    // Sub-rows: Owner & Tenant & Other
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "  └ 🏠 घरमालकांकडून ($ownerCount घरमालक):",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = IndianCurrencyFormatter.formatRupees(totalOwnerVargani),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "  └ 🏢 भाडेकरूंकडून ($tenantCount भाडेकरू):",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = IndianCurrencyFormatter.formatRupees(totalTenantVargani),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueInfo
                        )
                    }

                    if (otherCount > 0 || totalOtherVargani > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "  └ ⭐ इतर देणगीदारांकडून ($otherCount देणगीदार):",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalOtherVargani),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7E22CE)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 2: Expenses
                    ReportStatLine(
                        label = "एकूण मंडळ खर्च (${activeExpenses.size} व्हाउचर)",
                        value = IndianCurrencyFormatter.formatRupees(totalExpense),
                        color = RedExpense
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 3: Net Balance
                    ReportStatLine(
                        label = "अंतिम शिल्लक रक्कम (Net Balance)",
                        value = IndianCurrencyFormatter.formatRupees(netBalance),
                        color = if (netBalance >= 0) GreenSuccess else RedExpense,
                        isLarge = true
                    )
                }
            }
        }

        // GOVERNANCE & AUDIT CONTROLS
        item {
            Text(
                text = "हिशोब पडताळणी व ऑडिट साधने",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Daily Closing Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WhiteCard,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenDailyClosing)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF854D0E), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("दिवसाचा हिशोब", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("दैनिक क्लोजिंग व लॉक", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                // Reconciliation Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WhiteCard,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenReconciliation)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("रोख व UPI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ताळमेळ पडताळणी", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                // Audit History Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WhiteCard,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenAuditHistory)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ऑडिट ट्रेल", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("बदलांचा इतिहास", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // EXPORT OPTIONS SECTION
        item {
            Text(
                text = "अहवाल डाऊनलोड व शेअर पर्याय",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // 1. PDF Financial Report
        item {
            ExportActionCard(
                icon = Icons.Default.PictureAsPdf,
                iconBg = OrangePrimaryDark,
                title = "संपूर्ण ताळेबंद अहवाल (PDF)",
                subtitle = "सर्व वर्गणी, खर्च व अंतिम शिल्लक अधिकृत PDF स्वरूपात",
                buttonText = "PDF डाऊनलोड",
                onClick = onExportPdfReport
            )
        }

        // 2. Full Accounting Excel (CSV)
        item {
            ExportActionCard(
                icon = Icons.Default.TableChart,
                iconBg = Color(0xFF16A34A),
                title = "संपूर्ण ९-शीट हिशोब वही (Excel / CSV)",
                subtitle = "वर्गणी, खर्च, क्लोजिंग, रोख ताळमेळ व ऑडिट ट्रेल एकाच फाईलमध्ये",
                buttonText = "Excel फाईल",
                onClick = onExportFullAccountingExcel
            )
        }

        // 3. Only Owner Vargani Excel
        item {
            ExportActionCard(
                icon = Icons.Default.Home,
                iconBg = OrangePrimary,
                title = "फक्त घरमालक वर्गणी यादी (Excel)",
                subtitle = "किमान ₹१,००० जमा केलेल्या सर्व घरमालकांची यादी",
                buttonText = "घरमालक Excel",
                onClick = onExportOwnerVarganiExcel
            )
        }

        // 4. Only Tenant Vargani Excel
        item {
            ExportActionCard(
                icon = Icons.Default.Apartment,
                iconBg = BlueInfo,
                title = "फक्त भाडेकरू वर्गणी यादी (Excel)",
                subtitle = "संबंधित घरमालकाच्या नावासह सर्व भाडेकरूंची यादी",
                buttonText = "भाडेकरू Excel",
                onClick = onExportTenantVarganiExcel
            )
        }

        // 5. Only Other Vargani Excel
        item {
            ExportActionCard(
                icon = Icons.Default.Star,
                iconBg = Color(0xFF7E22CE),
                title = "फक्त इतर देणगीदार यादी (Excel)",
                subtitle = "व्यावसायिक, नगरसेवक, आमदार व इतर देणगीदारांची वर्गणी यादी",
                buttonText = "इतर Excel",
                onClick = onExportOtherVarganiExcel
            )
        }

        // 6. WhatsApp Summary Share
        item {
            ExportActionCard(
                icon = Icons.Default.Share,
                iconBg = Color(0xFF25D366),
                title = "WhatsApp हिशोब सारांश मेसेज",
                subtitle = "कार्यकर्ते व सभासदांच्या ग्रुपवर हिशोब सारांश पाठवा",
                buttonText = "WhatsApp शेअर",
                onClick = onShareSummaryWhatsApp
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ReportStatLine(
    label: String,
    value: String,
    color: Color,
    isLarge: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isLarge) 13.sp else 12.sp,
            fontWeight = if (isLarge) FontWeight.Bold else FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = value,
            fontSize = if (isLarge) 16.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ExportActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconBg,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = OrangeContainer,
                border = BorderStroke(1.dp, OrangeBorder)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryDark,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

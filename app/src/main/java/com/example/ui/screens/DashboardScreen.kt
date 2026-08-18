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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.MandalSettings
import com.example.data.model.VarganiTransaction
import com.example.ui.components.AkgmmLogo
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    settings: MandalSettings,
    totalVargani: Double,
    totalOwnerVargani: Double,
    totalTenantVargani: Double,
    totalOtherVargani: Double = 0.0,
    ownerCount: Int,
    tenantCount: Int,
    otherCount: Int = 0,
    totalExpenses: Double,
    netBalance: Double,
    todayVargani: Double = 0.0,
    todayExpenses: Double = 0.0,
    netCashInHand: Double = 0.0,
    netUpiInBank: Double = 0.0,
    recentVargani: List<VarganiTransaction>,
    recentExpenses: List<ExpenseTransaction>,
    events: List<FestivalEvent> = emptyList(),
    onNavigateToVargani: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToPending: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onAddNewPavti: () -> Unit,
    onAddNewExpense: () -> Unit,
    onOpenDailyClosing: () -> Unit = {},
    onOpenReconciliation: () -> Unit = {},
    onOpenFinalReport: () -> Unit = {},
    onOpenAuditHistory: () -> Unit = {},
    onSelectPavti: (VarganiTransaction) -> Unit,
    onShareSummaryWhatsApp: () -> Unit
) {
    val todayIso = DateUtils.getTodayIsoDate()
    val daysUntilStart = DateUtils.getDaysUntil(settings.festivalStartDate)
    val todayEvents = events.filter { it.dateString == todayIso }
    val upcomingEvents = events.filter { it.dateString >= todayIso }.sortedBy { it.dateString + it.eventTime }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangeBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // TOP BANNER: AKGMM IDENTITY
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, OrangeBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AkgmmLogo(
                        size = 56.dp,
                        showSubtext = false
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OrangePrimaryDark
                            ) {
                                Text(
                                    text = "AKGMM",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${settings.festivalYear} महोत्सव",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "अखिल गणेशनगर मित्र मंडळ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "गणेशनगर, पुणे - ४११०६०",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // FESTIVAL COUNTDOWN & GREETING BANNER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToCalendar),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (daysUntilStart <= 0) Color(0xFFFFF3E0) else WhiteCard
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
                border = BorderStroke(1.2.dp, OrangePrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OrangeContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (daysUntilStart > 0) "🚩" else "🌺",
                                fontSize = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        if (daysUntilStart > 0) {
                            Text(
                                text = "गणेशोत्सवाची पूर्वतयारी सुरु",
                                fontSize = 11.5.sp,
                                color = OrangePrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "उत्सव सुरु होण्यास ${DateUtils.toMarathiDigits(daysUntilStart.toString())} दिवस बाकी!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "सुरुवात: ${DateUtils.formatToMarathiDisplayDate(settings.festivalStartDate)}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        } else if (daysUntilStart == 0) {
                            Text(
                                text = "🎉 गणपती बाप्पा मोरया!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark
                            )
                            Text(
                                text = "आज गणेश चतुर्थी! उत्सवाचा पहिला दिवस",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        } else {
                            val dayIdx = DateUtils.getFestivalDayIndex(todayIso, settings.festivalStartDate)
                            Text(
                                text = "🌺 श्री गणेशोत्सव २०२६",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark
                            )
                            Text(
                                text = "उत्सव चालू आहे (दिवस $dayIdx)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangePrimaryDark
                    ) {
                        Text(
                            text = "वेळापत्रक →",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // TODAY'S OR UPCOMING EVENTS PREVIEW
        if (todayEvents.isNotEmpty() || upcomingEvents.isNotEmpty()) {
            item {
                val displayEvents = if (todayEvents.isNotEmpty()) todayEvents else upcomingEvents.take(2)
                val sectionTitle = if (todayEvents.isNotEmpty()) "📅 आजचे कार्यक्रम (Today's Events)" else "📅 आगामी कार्यक्रम (Upcoming)"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard),
                    border = BorderStroke(1.dp, OrangeBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sectionTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "सर्व पहा (${events.size}) →",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark,
                                modifier = Modifier.clickable(onClick = onNavigateToCalendar)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        displayEvents.forEachIndexed { index, ev ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = OrangeBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = OrangeContainer
                                ) {
                                    Text(
                                        text = ev.eventTime,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimaryDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ev.eventName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    val subtitle = listOfNotNull(
                                        ev.programType.ifBlank { null },
                                        if (ev.aartiContributorName.isNotBlank()) "आरती: ${ev.aartiContributorName}" else null
                                    ).joinToString(" • ")
                                    if (subtitle.isNotBlank()) {
                                        Text(
                                            text = subtitle,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ONE-TAP FINAL REPORT BANNER
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = OrangePrimaryDark,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenFinalReport)
                    .testTag("open_final_report_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "📊 अंतिम हिशोब तयार करा",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "PDF, Excel व WhatsApp सर्वंकष ताळेबंद",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }

        // TODAY'S LIVE STATS CARD (दैनिक सद्यस्थिती)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, OrangeBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Today, contentDescription = null, tint = OrangePrimaryDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("आजचा जमा व खर्च (Today's Stats)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Text(
                            text = "क्लोजिंग करा →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark,
                            modifier = Modifier.clickable(onClick = onOpenDailyClosing)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GreenContainer,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("आजची जमा", fontSize = 11.sp, color = GreenSuccess, fontWeight = FontWeight.SemiBold)
                                Text(IndianCurrencyFormatter.formatRupees(todayVargani), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = RedContainer,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("आजचा खर्च", fontSize = 11.sp, color = RedExpense, fontWeight = FontWeight.SemiBold)
                                Text(IndianCurrencyFormatter.formatRupees(todayExpenses), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                            }
                        }
                    }
                }
            }
        }

        // MAIN FINANCIAL BALANCE SUMMARY CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ताळेबंद सद्यस्थिती (Balance Overview)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Surface(
                            shape = CircleShape,
                            color = if (netBalance >= 0) GreenContainer else RedContainer
                        ) {
                            Text(
                                text = if (netBalance >= 0) "नफा/शिल्लक" else "तूट",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netBalance >= 0) GreenSuccess else RedExpense,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Net Balance Amount Display
                    Text(
                        text = IndianCurrencyFormatter.formatRupees(netBalance),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (netBalance >= 0) OrangePrimaryDark else RedExpense
                    )
                    Text(
                        text = "अंतिम शिल्लक निव्वळ रक्कम",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Income vs Expense Breakdown Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Total Vargani Collection
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = GreenSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "एकूण वर्गणी जमा",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalVargani),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccess
                            )
                        }

                        // Total Expenses
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = RedExpense,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "एकूण मंडळ खर्च",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalExpenses),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedExpense
                            )
                        }
                    }
                }
            }
        }

        // CASH VS UPI SPLIT CARD (रोख शिल्लक vs बँक शिल्लक)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("रोख व बँक शिल्लक स्थिती", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "ताळमेळ जुळवा →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark,
                            modifier = Modifier.clickable(onClick = onOpenReconciliation)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = OrangeContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, OrangeBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payments, contentDescription = null, tint = OrangePrimaryDark, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("शिल्लक रोख", fontSize = 11.sp, color = OrangePrimaryDark, fontWeight = FontWeight.Bold)
                                }
                                Text(IndianCurrencyFormatter.formatRupees(netCashInHand), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BlueContainer,
                            border = BorderStroke(1.dp, BlueInfo.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = BlueInfo, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("बँक / UPI", fontSize = 11.sp, color = BlueInfo, fontWeight = FontWeight.Bold)
                                }
                                Text(IndianCurrencyFormatter.formatRupees(netUpiInBank), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // PERSON CATEGORY METRIC CARDS (घरमालक, भाडेकरू, इतर)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Owner Vargani Card (घरमालक वर्गणी)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToVargani),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, OrangeBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = OrangeContainer,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "घरमालक",
                                            tint = OrangePrimaryDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = OrangeContainer
                                ) {
                                    Text(
                                        text = "$ownerCount घरमालक",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimaryDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "घरमालकांकडून जमा",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalOwnerVargani),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OrangePrimaryDark
                            )
                            Text(
                                text = "किमान ₹१,००० नियम",
                                fontSize = 9.5.sp,
                                color = TextOrange
                            )
                        }
                    }

                    // Tenant Vargani Card (भाडेकरू वर्गणी)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToVargani),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = BlueContainer,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Apartment,
                                            contentDescription = "भाडेकरू",
                                            tint = BlueInfo,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BlueContainer
                                ) {
                                    Text(
                                        text = "$tenantCount भाडेकरू",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BlueInfo,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "भाडेकरूंकडून जमा",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalTenantVargani),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BlueInfo
                            )
                            Text(
                                text = "घरमालक संदर्भ",
                                fontSize = 9.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Other / Special Category Card (इतर - व्यापारी, लोकप्रतिनिधी, इ.)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onNavigateToVargani),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF3E8FF),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "इतर",
                                        tint = Color(0xFF7E22CE),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "इतर / विशेष देणगीदार",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "व्यापारी • नगरसेवक • आमदार • देणगीदार",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalOtherVargani),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF7E22CE)
                            )
                            Text(
                                text = "$otherCount देणगीदार",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7E22CE)
                            )
                        }
                    }
                }
            }
        }

        // QUICK ACTION BUTTONS ROW
        item {
            Text(
                text = "त्वरित कृती (Quick Actions)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. New Pavti
                QuickActionChip(
                    icon = Icons.Default.Add,
                    title = "नवीन पावती",
                    subtitle = "मालक / भाडेकरू",
                    bgColor = OrangePrimaryDark,
                    contentColor = Color.White,
                    onClick = onAddNewPavti
                )

                // 2. Add Expense
                QuickActionChip(
                    icon = Icons.Default.MoneyOff,
                    title = "खर्च नोंदवा",
                    subtitle = "बिल / व्हाउचर्स",
                    bgColor = Color(0xFFEF4444),
                    contentColor = Color.White,
                    onClick = onAddNewExpense
                )

                // 3. Daily Closing
                QuickActionChip(
                    icon = Icons.Default.Lock,
                    title = "दिवसाचा हिशोब",
                    subtitle = "क्लोजिंग व लॉक",
                    bgColor = Color(0xFF854D0E),
                    contentColor = Color.White,
                    onClick = onOpenDailyClosing
                )

                // 4. Reconciliation
                QuickActionChip(
                    icon = Icons.Default.Payments,
                    title = "रोख व UPI ताळमेळ",
                    subtitle = "पडताळणी",
                    bgColor = Color(0xFF0284C7),
                    contentColor = Color.White,
                    onClick = onOpenReconciliation
                )

                // 5. WhatsApp Summary
                QuickActionChip(
                    icon = Icons.Default.Share,
                    title = "WhatsApp सारांश",
                    subtitle = "हिशोब शेअर करा",
                    bgColor = Color(0xFF25D366),
                    contentColor = Color.White,
                    onClick = onShareSummaryWhatsApp
                )

                // 6. Audit Trail
                QuickActionChip(
                    icon = Icons.Default.History,
                    title = "ऑडिट इतिहास",
                    subtitle = "सर्व नोंदींचा ट्रेल",
                    bgColor = Color(0xFF6B7280),
                    contentColor = Color.White,
                    onClick = onOpenAuditHistory
                )
            }
        }

        // RECENT VARGANI RECEIPTS FEED
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "अलीकडील वर्गणी पावत्या",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "सर्व पहा (${recentVargani.size}) →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryDark,
                    modifier = Modifier.clickable(onClick = onNavigateToVargani)
                )
            }
        }

        if (recentVargani.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "अद्याप कोणतीही पावती नोंदवलेली नाही.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onAddNewPavti,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark)
                        ) {
                            Text("पहिली पावती नोंदवा")
                        }
                    }
                }
            }
        } else {
            items(recentVargani.take(5), key = { it.id }) { item ->
                val isOwner = item.isOwner
                val isTenant = item.isTenant
                val badgeBg = when {
                    isOwner -> OrangeContainer
                    isTenant -> BlueContainer
                    else -> Color(0xFFF3E8FF)
                }
                val badgeTint = when {
                    isOwner -> OrangePrimaryDark
                    isTenant -> BlueInfo
                    else -> Color(0xFF7E22CE)
                }
                val badgeIcon = when {
                    isOwner -> Icons.Default.Home
                    isTenant -> Icons.Default.Apartment
                    else -> Icons.Default.Star
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPavti(item) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = badgeBg,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = badgeIcon,
                                        contentDescription = null,
                                        tint = badgeTint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.contributorName,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = when {
                                        isTenant && item.ownerName.isNotBlank() -> "भाडेकरू (घरमालक: ${item.ownerName})"
                                        item.isOther && item.customCategoryName.isNotBlank() -> "${item.customCategoryName} • ${item.pavtiNumber}"
                                        item.isOther -> "${item.otherPersonType} • ${item.pavtiNumber}"
                                        else -> "घरमालक • ${item.pavtiNumber}"
                                    },
                                    fontSize = 10.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(item.amount),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark
                            )
                            Text(
                                text = DateUtils.formatNumericDate(item.timestamp),
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuickActionChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    bgColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = WhiteCard,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp,
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = bgColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

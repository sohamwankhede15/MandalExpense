package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyClosing
import com.example.data.model.MandalSettings
import com.example.data.model.OtherPersonType
import com.example.ui.theme.OrangeBackground
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.ui.theme.TextOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard
import com.example.ui.viewmodel.MandalViewModel
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter
import com.example.util.ShareHelper

@Composable
fun DailyClosingScreen(
    viewModel: MandalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val varganiList by viewModel.varganiList.collectAsState()
    val expenseList by viewModel.expenseList.collectAsState()
    val incomeList by viewModel.incomeList.collectAsState()
    val dailyClosings by viewModel.dailyClosings.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedDate by remember { mutableStateOf(DateUtils.getTodayIsoDate()) }
    var closingNotes by remember { mutableStateOf("") }
    var showCloseConfirmDialog by remember { mutableStateOf(false) }
    var showReopenConfirmDialog by remember { mutableStateOf(false) }
    var reopenReason by remember { mutableStateOf("") }

    // Check if the selected date is closed
    val isSelectedDateClosed = dailyClosings.any { it.dateString == selectedDate && it.isClosed }
    val existingClosing = dailyClosings.find { it.dateString == selectedDate }

    // Calculate Day Transactions
    val dayVargani = remember(varganiList, selectedDate) {
        varganiList.filter { !it.isCancelled && DateUtils.formatIsoDate(it.timestamp) == selectedDate }
    }

    val dayExpenses = remember(expenseList, selectedDate) {
        expenseList.filter { !it.isCancelled && !it.isFree && DateUtils.formatIsoDate(it.timestamp) == selectedDate }
    }

    val dayFreeExpenses = remember(expenseList, selectedDate) {
        expenseList.filter { !it.isCancelled && it.isFree && DateUtils.formatIsoDate(it.timestamp) == selectedDate }
    }

    val dayOtherIncome = remember(incomeList, selectedDate) {
        incomeList.filter { DateUtils.formatIsoDate(it.timestamp) == selectedDate }
    }

    // Income Breakdowns
    val ownerIncome = dayVargani.filter { it.isOwner }.sumOf { it.amount }
    val tenantIncome = dayVargani.filter { it.isTenant }.sumOf { it.amount }
    val corporatorIncome = dayVargani.filter { it.isOther && OtherPersonType.fromCodeOrLabel(it.otherPersonType) == OtherPersonType.CORPORATOR }.sumOf { it.amount }
    val mlaIncome = dayVargani.filter { it.isOther && OtherPersonType.fromCodeOrLabel(it.otherPersonType) == OtherPersonType.MLA }.sumOf { it.amount }
    val businessIncome = dayVargani.filter { it.isOther && OtherPersonType.fromCodeOrLabel(it.otherPersonType) == OtherPersonType.BUSINESS }.sumOf { it.amount }
    val donorIncome = dayVargani.filter { it.isOther && OtherPersonType.fromCodeOrLabel(it.otherPersonType) == OtherPersonType.DONOR }.sumOf { it.amount }
    val sponsorIncome = dayVargani.filter { it.isOther && OtherPersonType.fromCodeOrLabel(it.otherPersonType) == OtherPersonType.SPONSOR }.sumOf { it.amount }
    val otherCustomIncome = dayVargani.filter { it.isOther && OtherPersonType.fromCodeOrLabel(it.otherPersonType) == OtherPersonType.OTHER }.sumOf { it.amount }
    val directOtherIncome = dayOtherIncome.sumOf { it.amount }

    val totalDayIncome = dayVargani.sumOf { it.amount } + directOtherIncome

    // Expense Breakdowns
    val generalExpense = dayExpenses.filter { it.expenseType != "MAHAPRASAD" && it.expenseType != "ADVANCE" }.sumOf { it.amount }
    val mahaprasadExpense = dayExpenses.filter { it.expenseType == "MAHAPRASAD" || it.category == "महाप्रसाद" }.sumOf { it.amount }
    val advanceExpense = dayExpenses.filter { it.expenseType == "ADVANCE" }.sumOf { it.amount }
    val totalDayExpense = dayExpenses.sumOf { it.amount }

    // Payment Modes Breakdowns
    val cashIncome = dayVargani.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount } +
        dayOtherIncome.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    val upiIncome = dayVargani.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount } +
        dayOtherIncome.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }

    val cashExpense = dayExpenses.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    val upiExpense = dayExpenses.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }

    val netDayBalance = totalDayIncome - totalDayExpense

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OrangeBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            // Screen Title & Date Selector Card
            item {
                DateSelectorHeaderCard(
                    selectedDate = selectedDate,
                    accountingStartDate = settings.accountingStartDate,
                    isClosed = isSelectedDateClosed,
                    onSelectDate = { selectedDate = it },
                    onShareReport = {
                        val shareText = buildDayClosingSummaryText(
                            dateString = selectedDate,
                            settings = settings,
                            totalIncome = totalDayIncome,
                            totalExpense = totalDayExpense,
                            netBalance = netDayBalance,
                            varganiCount = dayVargani.size,
                            expenseCount = dayExpenses.size,
                            cashIn = cashIncome,
                            upiIn = upiIncome,
                            isClosed = isSelectedDateClosed
                        )
                        ShareHelper.shareText(context, shareText, "दिवसाचा हिशोब - ${DateUtils.formatToMarathiDisplayDate(selectedDate)}")
                    }
                )
            }

            // Status Indicator Banner
            item {
                ClosingStatusBanner(
                    isClosed = isSelectedDateClosed,
                    existingClosing = existingClosing,
                    onReopen = { showReopenConfirmDialog = true }
                )
            }

            // Day Balance Highlight Card
            item {
                DayBalanceHighlightCard(
                    totalIncome = totalDayIncome,
                    totalExpense = totalDayExpense,
                    netBalance = netDayBalance,
                    totalPavtis = dayVargani.size,
                    totalExpensesCount = dayExpenses.size
                )
            }

            // Income Breakdown Card
            item {
                IncomeBreakdownCard(
                    ownerIncome = ownerIncome,
                    tenantIncome = tenantIncome,
                    corporatorIncome = corporatorIncome,
                    mlaIncome = mlaIncome,
                    businessIncome = businessIncome,
                    donorIncome = donorIncome,
                    sponsorIncome = sponsorIncome,
                    otherCustomIncome = otherCustomIncome,
                    directOtherIncome = directOtherIncome,
                    totalIncome = totalDayIncome,
                    cashIncome = cashIncome,
                    upiIncome = upiIncome
                )
            }

            // Expense Breakdown Card
            item {
                ExpenseBreakdownCard(
                    generalExpense = generalExpense,
                    mahaprasadExpense = mahaprasadExpense,
                    advanceExpense = advanceExpense,
                    freeExpenseCount = dayFreeExpenses.size,
                    totalExpense = totalDayExpense,
                    cashExpense = cashExpense,
                    upiExpense = upiExpense
                )
            }

            // Close / Action Box
            item {
                ClosingActionCard(
                    isClosed = isSelectedDateClosed,
                    notes = closingNotes,
                    onNotesChange = { closingNotes = it },
                    onCloseDay = { showCloseConfirmDialog = true },
                    onReopenDay = { showReopenConfirmDialog = true }
                )
            }

            // History of Closed Days Section
            item {
                ClosedDaysHistorySection(
                    closings = dailyClosings,
                    onSelectHistoricalDate = { selectedDate = it }
                )
            }
        }
    }

    // Confirmation Dialog for Closing Day
    if (showCloseConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCloseConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = OrangePrimaryDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("दिवसाचा हिशोब बंद करा?", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "तारीख: ${DateUtils.formatToMarathiDisplayDate(selectedDate)}",
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• एकूण जमा: ${IndianCurrencyFormatter.formatRupees(totalDayIncome)}")
                    Text("• एकूण खर्च: ${IndianCurrencyFormatter.formatRupees(totalDayExpense)}")
                    Text("• शिल्लक: ${IndianCurrencyFormatter.formatRupees(netDayBalance)}")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "या दिवसाचा हिशोब बंद केल्यानंतर सामान्य वापरकर्त्यांना या तारखेच्या आर्थिक नोंदी बदलता येणार नाहीत. तुम्हाला खात्री आहे का?",
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.closeDay(
                            dateString = selectedDate,
                            notes = closingNotes,
                            closedBy = currentUser,
                            onSuccess = {
                                showCloseConfirmDialog = false
                                closingNotes = ""
                                Toast.makeText(context, "दिवसाचा हिशोब यशस्वीरीत्या बंद करण्यात आला!", Toast.LENGTH_SHORT).show()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark)
                ) {
                    Text("हिशोब बंद करा (Lock)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseConfirmDialog = false }) {
                    Text("रद्द करा")
                }
            }
        )
    }

    // Confirmation Dialog for Reopening Day
    if (showReopenConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showReopenConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("बंद केलेला हिशोब पुन्हा उघडा?", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "तारीख: ${DateUtils.formatToMarathiDisplayDate(selectedDate)}",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "हा दिवस पुन्हा उघडल्यास नोंदींमध्ये बदल करता येतील. ही कृती ऑडिट लॉगमध्ये नोंदवली जाईल.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reopenReason,
                        onValueChange = { reopenReason = it },
                        label = { Text("उघडण्याचे कारण (Reason) *", fontSize = 12.sp) },
                        placeholder = { Text("उदा. पावती दुरुस्ती / सुटलेला खर्च") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reopenDay(
                            dateString = selectedDate,
                            reason = reopenReason,
                            reopenedBy = currentUser,
                            onSuccess = {
                                showReopenConfirmDialog = false
                                reopenReason = ""
                                Toast.makeText(context, "दिवसाचा हिशोब पुन्हा उघडण्यात आला!", Toast.LENGTH_SHORT).show()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("पुन्हा उघडा (Unlock)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReopenConfirmDialog = false }) {
                    Text("रद्द करा")
                }
            }
        )
    }
}

@Composable
private fun DateSelectorHeaderCard(
    selectedDate: String,
    accountingStartDate: String,
    isClosed: Boolean,
    onSelectDate: (String) -> Unit,
    onShareReport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "दिवसाचा हिशोब व क्लोजिंग",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "हिशोब सुरुवातीची तारीख: ${DateUtils.formatToMarathiDisplayDate(accountingStartDate)}",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onShareReport) {
                    Icon(Icons.Default.Share, contentDescription = "शेअर करा", tint = OrangePrimaryDark)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Date Jump Buttons (Today, Yesterday, Custom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val today = DateUtils.getTodayIsoDate()
                val yesterday = run {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    sdf.format(cal.time)
                }

                Surface(
                    onClick = { onSelectDate(today) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedDate == today) OrangePrimaryDark else OrangeContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "आज (Today)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedDate == today) Color.White else OrangePrimaryDark,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    onClick = { onSelectDate(yesterday) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedDate == yesterday) OrangePrimaryDark else OrangeContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "काल (Yesterday)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedDate == yesterday) Color.White else OrangePrimaryDark,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date text input
            OutlinedTextField(
                value = selectedDate,
                onValueChange = onSelectDate,
                label = { Text("हिशोबाची तारीख (YYYY-MM-DD)", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = OrangePrimaryDark) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun ClosingStatusBanner(
    isClosed: Boolean,
    existingClosing: DailyClosing?,
    onReopen: () -> Unit
) {
    val bgColor = if (isClosed) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val borderColor = if (isClosed) Color(0xFFEF9A9A) else Color(0xFFA5D6A7)
    val textColor = if (isClosed) Color(0xFFC62828) else Color(0xFF2E7D32)
    val icon = if (isClosed) Icons.Default.Lock else Icons.Default.CheckCircle

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isClosed) "🔒 या दिवसाचा हिशोब बंद (Locked) आहे" else "🟢 या दिवसाचा हिशोब उघडा (Open) आहे",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (isClosed && existingClosing != null) {
                    Text(
                        text = "बंद करणारा: ${existingClosing.closedBy} • वेळ: ${DateUtils.formatDateTime(existingClosing.closingTimestamp)}",
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.85f)
                    )
                }
            }
            if (isClosed) {
                OutlinedButton(
                    onClick = onReopen,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, textColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("उघडा", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DayBalanceHighlightCard(
    totalIncome: Double,
    totalExpense: Double,
    netBalance: Double,
    totalPavtis: Int,
    totalExpensesCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OrangeBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "दिवसाचा सारांश (Day Balance)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = OrangePrimaryDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryMetricItem(
                    label = "एकूण जमा",
                    amount = totalIncome,
                    count = "$totalPavtis पावत्या",
                    color = Color(0xFF2E7D32)
                )
                SummaryMetricItem(
                    label = "एकूण खर्च",
                    amount = totalExpense,
                    count = "$totalExpensesCount नोंदी",
                    color = Color(0xFFDC2626)
                )
                SummaryMetricItem(
                    label = "दिवसाची शिल्लक",
                    amount = netBalance,
                    count = "निव्वळ",
                    color = if (netBalance >= 0) OrangePrimaryDark else Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricItem(
    label: String,
    amount: Double,
    count: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.5.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Text(
            text = IndianCurrencyFormatter.formatRupees(amount),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(text = count, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun IncomeBreakdownCard(
    ownerIncome: Double,
    tenantIncome: Double,
    corporatorIncome: Double,
    mlaIncome: Double,
    businessIncome: Double,
    donorIncome: Double,
    sponsorIncome: Double,
    otherCustomIncome: Double,
    directOtherIncome: Double,
    totalIncome: Double,
    cashIncome: Double,
    upiIncome: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📥 जमा तपशील (Income Breakdown)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = IndianCurrencyFormatter.formatRupees(totalIncome),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OrangeBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            BreakdownRow("घरमालक वर्गणी", ownerIncome)
            BreakdownRow("भाडेकरू वर्गणी", tenantIncome)
            if (corporatorIncome > 0) BreakdownRow("नगरसेवक योगदान", corporatorIncome)
            if (mlaIncome > 0) BreakdownRow("आमदार योगदान", mlaIncome)
            if (businessIncome > 0) BreakdownRow("व्यावसायिक / व्यापारी", businessIncome)
            if (donorIncome > 0) BreakdownRow("देणगीदार", donorIncome)
            if (sponsorIncome > 0) BreakdownRow("प्रायोजक", sponsorIncome)
            if (otherCustomIncome > 0) BreakdownRow("इतर विशेष व्यक्ती", otherCustomIncome)
            if (directOtherIncome > 0) BreakdownRow("इतर थेट जमा", directOtherIncome)

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = OrangeBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("• रोख जमा: ${IndianCurrencyFormatter.formatRupees(cashIncome)}", fontSize = 11.5.sp, color = TextSecondary)
                Text("• UPI जमा: ${IndianCurrencyFormatter.formatRupees(upiIncome)}", fontSize = 11.5.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun ExpenseBreakdownCard(
    generalExpense: Double,
    mahaprasadExpense: Double,
    advanceExpense: Double,
    freeExpenseCount: Int,
    totalExpense: Double,
    cashExpense: Double,
    upiExpense: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📤 खर्च तपशील (Expense Breakdown)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = IndianCurrencyFormatter.formatRupees(totalExpense),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OrangeBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            BreakdownRow("सामान्य मंडळ खर्च", generalExpense)
            BreakdownRow("महाप्रसाद खर्च", mahaprasadExpense)
            if (advanceExpense > 0) BreakdownRow("आगाऊ रक्कम (Advance)", advanceExpense)
            if (freeExpenseCount > 0) BreakdownRow("मोफत / प्रायोजित खर्च", 0.0, extraNote = "$freeExpenseCount नोंदी (₹०)")

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = OrangeBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("• रोख खर्च: ${IndianCurrencyFormatter.formatRupees(cashExpense)}", fontSize = 11.5.sp, color = TextSecondary)
                Text("• UPI खर्च: ${IndianCurrencyFormatter.formatRupees(upiExpense)}", fontSize = 11.5.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    title: String,
    amount: Double,
    extraNote: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        if (extraNote.isNotBlank()) {
            Text(text = extraNote, fontSize = 12.sp, color = TextSecondary)
        } else {
            Text(
                text = IndianCurrencyFormatter.formatRupees(amount),
                fontSize = 12.5.sp,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ClosingActionCard(
    isClosed: Boolean,
    notes: String,
    onNotesChange: (String) -> Unit,
    onCloseDay: () -> Unit,
    onReopenDay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!isClosed) {
                Text(
                    text = "हिशोब बंद करण्याची कृती",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("क्लोजिंग टीप (पर्यायी)", fontSize = 12.sp) },
                    placeholder = { Text("उदा. सर्व पावत्या व खर्च पडताळले") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onCloseDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("close_day_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("या दिवसाचा हिशोब बंद करा (Close Day)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "हा दिवस बंद करण्यात आला आहे",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = "दुरुस्तीसाठी अधिकृत व्यक्तीने दिवस पुन्हा उघडावा लागेल.",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                    }
                    Button(
                        onClick = onReopenDay,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("हिशोब पुन्हा उघडा", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClosedDaysHistorySection(
    closings: List<DailyClosing>,
    onSelectHistoricalDate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = OrangePrimaryDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "बंद केलेल्या दिवसांचा इतिहास (${closings.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (closings.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = WhiteCard,
                border = BorderStroke(1.dp, OrangeBorder)
            ) {
                Text(
                    text = "कोणताही दिवस अद्याप बंद केलेला नाही.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            closings.forEach { closing ->
                Surface(
                    onClick = { onSelectHistoricalDate(closing.dateString) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = WhiteCard,
                    border = BorderStroke(1.dp, OrangeBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = DateUtils.formatToMarathiDisplayDate(closing.dateString),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "जमा: ${IndianCurrencyFormatter.formatRupees(closing.totalIncome)} • खर्च: ${IndianCurrencyFormatter.formatRupees(closing.totalExpenses)}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(closing.closingBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark
                            )
                            Text(
                                text = "🔒 बंद",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildDayClosingSummaryText(
    dateString: String,
    settings: MandalSettings,
    totalIncome: Double,
    totalExpense: Double,
    netBalance: Double,
    varganiCount: Int,
    expenseCount: Int,
    cashIn: Double,
    upiIn: Double,
    isClosed: Boolean
): String {
    return """
        *${settings.mandalName}*
        *दिवसाचा हिशोब अहवाल (Daily Closing)*
        📅 तारीख: ${DateUtils.formatToMarathiDisplayDate(dateString)}
        ----------------------------------
        📥 एकूण जमा: ${IndianCurrencyFormatter.formatRupees(totalIncome)} ($varganiCount पावत्या)
        • रोख: ${IndianCurrencyFormatter.formatRupees(cashIn)}
        • UPI: ${IndianCurrencyFormatter.formatRupees(upiIn)}
        
        📤 एकूण खर्च: ${IndianCurrencyFormatter.formatRupees(totalExpense)} ($expenseCount नोंदी)
        
        💰 निव्वळ शिल्लक: ${IndianCurrencyFormatter.formatRupees(netBalance)}
        ----------------------------------
        स्थिती: ${if (isClosed) "🔒 हिशोब बंद (Closed)" else "🟢 हिशोब चालू (Open)"}
        नोंदणी क्र: ${settings.registrationNumber}
    """.trimIndent()
}

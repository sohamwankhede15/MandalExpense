package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyClosing
import com.example.data.model.ExpenseTransaction
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
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter

@Composable
fun DailyClosingDialog(
    varganiList: List<VarganiTransaction>,
    expenseList: List<ExpenseTransaction>,
    dailyClosingList: List<DailyClosing>,
    currentUser: String,
    onCloseDay: (dateString: String, notes: String, closedBy: String) -> Unit,
    onReopenDay: (dateString: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = आजचा हिशोब, 1 = बंद दिवसांचा इतिहास
    var selectedDateString by remember { mutableStateOf(DateUtils.getTodayIsoDate()) }
    var closingNotes by remember { mutableStateOf("") }
    var showConfirmCloseDialog by remember { mutableStateOf(false) }
    var showConfirmUnlockDialog by remember { mutableStateOf(false) }

    // Day calculations
    val dayVargani = remember(varganiList, selectedDateString) {
        varganiList.filter { !it.isCancelled && DateUtils.formatIsoDate(it.timestamp) == selectedDateString }
    }
    val dayExpenses = remember(expenseList, selectedDateString) {
        expenseList.filter { !it.isCancelled && DateUtils.formatIsoDate(it.timestamp) == selectedDateString }
    }

    val dayTotalVargani = remember(dayVargani) { dayVargani.sumOf { it.amount } }
    val dayCashVargani = remember(dayVargani) {
        dayVargani.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    }
    val dayUpiVargani = remember(dayVargani) {
        dayVargani.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    }
    val dayTotalExpenses = remember(dayExpenses) { dayExpenses.sumOf { it.amount } }
    val dayCashExpenses = remember(dayExpenses) {
        dayExpenses.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    }
    val dayUpiExpenses = remember(dayExpenses) {
        dayExpenses.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    }
    val dayNetBalance = dayTotalVargani - dayTotalExpenses

    val existingClosing = remember(dailyClosingList, selectedDateString) {
        dailyClosingList.find { it.dateString == selectedDateString && it.isClosed }
    }
    val isDayClosed = existingClosing != null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(650.dp)
                .testTag("daily_closing_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = OrangeContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = OrangePrimaryDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "दिवसाचा हिशोब व क्लोजिंग",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "दैनिक जमा-खर्च पडताळणी व दिवस बंद करणे",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_daily_closing_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = OrangeContainer.copy(alpha = 0.5f),
                    contentColor = OrangePrimaryDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "📅 दिवसाचा हिशोब",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "📋 बंद दिवसांचा इतिहास (${dailyClosingList.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // Day Closing Main View
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Date selector & Status Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = OrangeBackground,
                            border = BorderStroke(1.dp, OrangeBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "तारीख: $selectedDateString",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = if (selectedDateString == DateUtils.getTodayIsoDate()) "आजचा दिवस" else "मागील दिवस",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isDayClosed) RedContainer else GreenContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isDayClosed) Icons.Default.Lock else Icons.Default.LockOpen,
                                            contentDescription = null,
                                            tint = if (isDayClosed) RedExpense else GreenSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isDayClosed) "हिशोब बंद (Locked)" else "चालू दिवस (Open)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDayClosed) RedExpense else GreenSuccess
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Summary Grid
                        Text(
                            text = "दैनिक जमा व खर्च तपशील",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Pavti Count & Total Vargani Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GreenContainer,
                                border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("एकूण जमा वर्गणी", fontSize = 11.sp, color = GreenSuccess, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        IndianCurrencyFormatter.format(dayTotalVargani),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess
                                    )
                                    Text("${dayVargani.size} पावत्या", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            // Total Expenses Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RedContainer,
                                border = BorderStroke(1.dp, RedExpense.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("एकूण खर्च", fontSize = 11.sp, color = RedExpense, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        IndianCurrencyFormatter.format(dayTotalExpenses),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RedExpense
                                    )
                                    Text("${dayExpenses.size} खर्च नोंदी", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cash & UPI Breakdown Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = OrangeContainer.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, OrangeBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("💵 रोख जमा: ${IndianCurrencyFormatter.format(dayCashVargani)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text("📱 UPI जमा: ${IndianCurrencyFormatter.format(dayUpiVargani)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("💵 रोख खर्च: ${IndianCurrencyFormatter.format(dayCashExpenses)}", fontSize = 11.sp, color = TextSecondary)
                                    Text("📱 UPI खर्च: ${IndianCurrencyFormatter.format(dayUpiExpenses)}", fontSize = 11.sp, color = TextSecondary)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = OrangeBorder)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("दिवसाची निव्वळ शिल्लक:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryDark)
                                    Text(
                                        IndianCurrencyFormatter.format(dayNetBalance),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (dayNetBalance >= 0) GreenSuccess else RedExpense
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isDayClosed) {
                            // Day is already closed
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFF3E0),
                                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OrangePrimaryDark, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "हा दिवस ${existingClosing?.closedBy} यांनी बंद केला आहे.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OrangePrimaryDark
                                        )
                                    }
                                    if (!existingClosing?.notes.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("नोंद: ${existingClosing?.notes}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { showConfirmUnlockDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedExpense),
                                border = BorderStroke(1.dp, RedExpense),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reopen_day_button")
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("दिवस अनलॉक करा (Re-open Day)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Day is open - provide close button
                            Text(
                                text = "क्लोजिंग शेरा / टीप (पर्यायी)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = closingNotes,
                                onValueChange = { closingNotes = it },
                                placeholder = { Text("उदा. रोख मोजून जमा केली, हिशोब जुळला", fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangePrimary,
                                    unfocusedBorderColor = OrangeBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showConfirmCloseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("close_day_submit_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🔒 दिवसाचा हिशोब बंद करा", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    // Closed Days History
                    if (dailyClosingList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.History, contentDescription = null, tint = OrangeBorder, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("अजून कोणताही दिवस बंद केलेला नाही.", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(dailyClosingList) { item ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFFFDF8),
                                    border = BorderStroke(1.dp, OrangeBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = OrangePrimaryDark, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = item.dateString,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GreenContainer
                                            ) {
                                                Text(
                                                    text = "शिल्लक: ${IndianCurrencyFormatter.format(item.closingBalance)}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GreenSuccess,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "जमा: ${IndianCurrencyFormatter.format(item.totalIncome)} (${item.totalPavtisCount} पावत्या)",
                                                fontSize = 12.sp,
                                                color = GreenSuccess,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                "खर्च: ${IndianCurrencyFormatter.format(item.totalExpenses)}",
                                                fontSize = 12.sp,
                                                color = RedExpense,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "बंद करणारा: ${item.closedBy} | वेळ: ${DateUtils.formatDateTime(item.closingTimestamp)}",
                                            fontSize = 10.sp,
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
    }

    // Confirm Close Dialog
    if (showConfirmCloseDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmCloseDialog = false },
            title = {
                Text("दिवसाचा हिशोब बंद करायचा आहे का?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(
                    "तारीख $selectedDateString चा हिशोब बंद केल्यानंतर या तारखेच्या पावत्यांमध्ये बदल करता येणार नाही.\n\n" +
                            "• एकूण जमा: ${IndianCurrencyFormatter.format(dayTotalVargani)}\n" +
                            "• एकूण खर्च: ${IndianCurrencyFormatter.format(dayTotalExpenses)}\n" +
                            "• शिल्लक: ${IndianCurrencyFormatter.format(dayNetBalance)}",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmCloseDialog = false
                        onCloseDay(selectedDateString, closingNotes, currentUser)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark)
                ) {
                    Text("होय, दिवस बंद करा", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCloseDialog = false }) {
                    Text("रद्द करा", color = TextSecondary)
                }
            }
        )
    }

    // Confirm Unlock Dialog
    if (showConfirmUnlockDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmUnlockDialog = false },
            title = {
                Text("दिवस अनलॉक करायचा आहे का?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text("तारीख $selectedDateString अनलॉक केल्यास या दिवसाच्या नोंदी पुन्हा संपादन करता येतील.", fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmUnlockDialog = false
                        onReopenDay(selectedDateString)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedExpense)
                ) {
                    Text("होय, अनलॉक करा", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmUnlockDialog = false }) {
                    Text("रद्द करा", color = TextSecondary)
                }
            }
        )
    }
}

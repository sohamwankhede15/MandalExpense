package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.ExpenseTransaction
import com.example.data.model.MandalSettings
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangeBackground
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedExpense
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    settings: MandalSettings,
    expenseList: List<ExpenseTransaction>,
    onOpenAddExpense: () -> Unit,
    onEditExpense: (ExpenseTransaction) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    onSettleAdvance: ((ExpenseTransaction) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    var selectedCategory by remember { mutableStateOf("सर्व (All)") }
    var selectedImageForPreview by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "सर्व (All)",
        "मंडप व स्टेज सजावट",
        "लाईटिंग व ध्वनी (DJ)",
        "श्रींची मूर्ती व पूजन",
        "महाप्रसाद व भोजन",
        "दैनिक पूजा साहित्य",
        "मिरवणूक व विसर्जन",
        "सुरक्षा व पोलीस",
        "बक्षीस व मानचिन्ह",
        "प्रसिद्धी व बॅनर",
        "इतर किरकोळ खर्च"
    )

    val activeExpenses = remember(expenseList) { expenseList.filter { !it.isCancelled } }

    // Summary calculations
    val totalCashAndUpiExpense = remember(activeExpenses) {
        activeExpenses.filter { !it.isFree }.sumOf { it.amount }
    }
    val unsettledAdvances = remember(activeExpenses) {
        activeExpenses.filter { it.expenseType == "ADVANCE" && !it.isSettled }
    }
    val totalUnsettledAdvanceAmount = remember(unsettledAdvances) {
        unsettledAdvances.sumOf { it.amount }
    }
    val freeExpenses = remember(activeExpenses) {
        activeExpenses.filter { it.isFree || it.expenseType == "FREE_SPONSORED" }
    }
    val totalFreeValue = remember(freeExpenses) {
        freeExpenses.sumOf { if (it.totalEstimatedCost > 0) it.totalEstimatedCost else it.amount }
    }
    val mahaprasadExpenses = remember(activeExpenses) {
        activeExpenses.filter { it.isMahaprasad || it.category.contains("महाप्रसाद") }
    }
    val totalMahaprasadAmount = remember(mahaprasadExpenses) {
        mahaprasadExpenses.filter { !it.isFree }.sumOf { it.amount }
    }

    val filteredList = activeExpenses.filter { item ->
        val matchesType = when (selectedTypeFilter) {
            "ADVANCE" -> item.expenseType == "ADVANCE"
            "UNSETTLED" -> item.expenseType == "ADVANCE" && !item.isSettled
            "SETTLED" -> item.expenseType == "FINAL_SETTLEMENT" || (item.expenseType == "ADVANCE" && item.isSettled)
            "FREE" -> item.isFree || item.expenseType == "FREE_SPONSORED"
            "MAHAPRASAD" -> item.isMahaprasad || item.category.contains("महाप्रसाद")
            else -> true
        }
        val matchesCategory = (selectedCategory == "सर्व (All)") || (item.category == selectedCategory)
        val query = searchQuery.trim().lowercase()
        val matchesSearch = query.isEmpty() ||
                item.title.lowercase().contains(query) ||
                item.paidTo.lowercase().contains(query) ||
                item.sponsorName.lowercase().contains(query) ||
                item.memberAttribution.lowercase().contains(query) ||
                item.voucherNumber.lowercase().contains(query) ||
                item.billReceiptNumber.lowercase().contains(query)
        matchesType && matchesCategory && matchesSearch
    }

    val totalFilteredAmount = filteredList.filter { !it.isFree }.sumOf { it.amount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddExpense,
                containerColor = CrimsonAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_expense")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "खर्च / ॲडव्हान्स नोंदवा", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color(0xFFFDFBF7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Summary Header Card with tabs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "एकूण प्रत्यक्ष खर्च (Actual Cash/Bank Outflow)",
                                fontSize = 12.sp,
                                color = CrimsonAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(totalCashAndUpiExpense),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CrimsonAccent
                            )
                        }

                        Surface(
                            color = CrimsonAccent,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${activeExpenses.size} व्हाउचर्स",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (unsettledAdvances.isNotEmpty() || freeExpenses.isNotEmpty() || mahaprasadExpenses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (unsettledAdvances.isNotEmpty()) {
                                Surface(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedTypeFilter = "UNSETTLED" }
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("⏳ प्रलंबित ॲडव्हान्स", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SaffronDark)
                                        Text(
                                            IndianCurrencyFormatter.formatRupees(totalUnsettledAdvanceAmount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SaffronDark
                                        )
                                        Text("${unsettledAdvances.size} नोंदी बाकी", fontSize = 9.5.sp, color = TextMuted)
                                    }
                                }
                            }

                            if (freeExpenses.isNotEmpty()) {
                                Surface(
                                    color = Color(0xFFF3E5F5),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFCE93D8)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedTypeFilter = "FREE" }
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("🎁 मोफत / प्रायोजित", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                        Text(
                                            IndianCurrencyFormatter.formatRupees(totalFreeValue),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF6A1B9A)
                                        )
                                        Text("${freeExpenses.size} प्रायोजक", fontSize = 9.5.sp, color = TextMuted)
                                    }
                                }
                            }

                            if (mahaprasadExpenses.isNotEmpty()) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedTypeFilter = "MAHAPRASAD" }
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("🍲 महाप्रसाद खर्च", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        Text(
                                            IndianCurrencyFormatter.formatRupees(totalMahaprasadAmount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text("${mahaprasadExpenses.size} नोंदी", fontSize = 9.5.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_search_bar"),
                placeholder = { Text("खर्च नाव, दुकानदार, जबाबदार किंवा व्हाउचर क्र. शोधा...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CrimsonAccent)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = CrimsonAccent,
                    unfocusedBorderColor = Color(0xFFFFCDD2)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Type Filter Chips (Horizontal scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filterTypes = listOf(
                    "ALL" to "सर्व (${activeExpenses.size})",
                    "UNSETTLED" to "⏳ बाकी ॲडव्हान्स (${unsettledAdvances.size})",
                    "ADVANCE" to "💰 सर्व ॲडव्हान्स (${activeExpenses.count { it.expenseType == "ADVANCE" }})",
                    "SETTLED" to "🧾 अंतिम बिल (${activeExpenses.count { it.expenseType == "FINAL_SETTLEMENT" }})",
                    "FREE" to "🎁 मोफत/प्रायोजित (${freeExpenses.size})",
                    "MAHAPRASAD" to "🍲 महाप्रसाद (${mahaprasadExpenses.size})"
                )

                filterTypes.forEach { (typeKey, label) ->
                    FilterChip(
                        selected = selectedTypeFilter == typeKey,
                        onClick = { selectedTypeFilter = typeKey },
                        label = { Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (typeKey) {
                                "UNSETTLED" -> Color(0xFFE65100)
                                "FREE" -> Color(0xFF6A1B9A)
                                "MAHAPRASAD" -> Color(0xFF2E7D32)
                                else -> CrimsonAccent
                            },
                            selectedLabelColor = Color.White,
                            containerColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(text = cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronDark,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💸", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "शोध परिणामात खर्च आढळला नाही" else "कोणतीही खर्चाची नोंद नाही",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "नवीन खर्च किंवा ॲडव्हान्स नोंदवण्यासाठी खालील बटनावर क्लिक करा.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        ExpenseItemCard(
                            expense = item,
                            onEdit = { onEditExpense(item) },
                            onDelete = { onDeleteExpense(item.id) },
                            onViewImage = { path -> selectedImageForPreview = path },
                            onSettleAdvance = if (item.expenseType == "ADVANCE" && !item.isSettled) {
                                { onSettleAdvance?.invoke(item) }
                            } else null
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Bill Image Preview Dialog
    if (selectedImageForPreview != null) {
        Dialog(
            onDismissRequest = { selectedImageForPreview = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📷 बिलाचा फोटो (Bill Photo)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { selectedImageForPreview = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val imgFile = File(selectedImageForPreview ?: "")
                    if (imgFile.exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(imgFile),
                            contentDescription = "Full Bill Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("फोटो उपलब्ध नाही किंवा हटवला गेला आहे.", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: ExpenseTransaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewImage: (String) -> Unit,
    onSettleAdvance: (() -> Unit)? = null
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_item_${expense.voucherNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                expense.isFree -> Color(0xFFFAF5FC)
                expense.expenseType == "ADVANCE" && !expense.isSettled -> Color(0xFFFFFDF7)
                else -> Color.White
            }
        ),
        border = when {
            expense.isFree -> BorderStroke(1.dp, Color(0xFFE1BEE7))
            expense.expenseType == "ADVANCE" && !expense.isSettled -> BorderStroke(1.dp, Color(0xFFFFCC80))
            expense.expenseType == "FINAL_SETTLEMENT" -> BorderStroke(1.dp, Color(0xFFA5D6A7))
            else -> BorderStroke(0.5.dp, Color(0xFFEEEEEE))
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Voucher badge, Type Badge, Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = when (expense.expenseType) {
                            "ADVANCE" -> Color(0xFFFFE0B2)
                            "FINAL_SETTLEMENT" -> Color(0xFFC8E6C9)
                            "FREE_SPONSORED" -> Color(0xFFE1BEE7)
                            else -> Color(0xFFFFCDD2)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = expense.voucherNumber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (expense.expenseType) {
                                "ADVANCE" -> SaffronDark
                                "FINAL_SETTLEMENT" -> Color(0xFF2E7D32)
                                "FREE_SPONSORED" -> Color(0xFF6A1B9A)
                                else -> CrimsonAccent
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Expense Type Badge
                    when (expense.expenseType) {
                        "ADVANCE" -> {
                            Surface(
                                color = if (expense.isSettled) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (expense.isSettled) Icons.Default.CheckCircle else Icons.Default.HourglassBottom,
                                        contentDescription = null,
                                        tint = if (expense.isSettled) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (expense.isSettled) "ॲडव्हान्स (समायोजित ✓)" else "ॲडव्हान्स (हिशोब बाकी ⏳)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (expense.isSettled) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }
                            }
                        }
                        "FINAL_SETTLEMENT" -> {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "अंतिम हिशोब (Settled)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        "FREE_SPONSORED" -> {
                            Surface(
                                color = Color(0xFFF3E5F5),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = Color(0xFF6A1B9A),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "मोफत / प्रायोजित",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6A1B9A)
                                    )
                                }
                            }
                        }
                    }

                    if (expense.isMahaprasad) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🍲 महाप्रसाद",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = DateUtils.formatDate(expense.timestamp),
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title & Amount Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.title,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "वर्गवारी: ${expense.category}",
                        fontSize = 11.5.sp,
                        color = SaffronDark,
                        fontWeight = FontWeight.Medium
                    )

                    // Paid to / Sponsor name
                    if (expense.isFree && expense.sponsorName.isNotBlank()) {
                        Text(
                            text = "🎁 देणगीदार / प्रायोजक: ${expense.sponsorName}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6A1B9A)
                        )
                    } else if (expense.paidTo.isNotBlank() || expense.billReceiptNumber.isNotBlank()) {
                        Text(
                            text = listOfNotNull(
                                if (expense.paidTo.isNotBlank()) "दुकानदार/व्यक्ती: ${expense.paidTo}" else null,
                                if (expense.billReceiptNumber.isNotBlank()) "बिल क्र: ${expense.billReceiptNumber}" else null
                            ).joinToString(" • "),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    // Member Attribution
                    if (expense.memberAttribution.isNotBlank()) {
                        Surface(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "जबाबदार: ${expense.memberAttribution}",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Settlement Breakdown Info
                    if (expense.expenseType == "FINAL_SETTLEMENT" && expense.advancePaidAmount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "एकूण बिल: ${IndianCurrencyFormatter.formatRupees(expense.totalEstimatedCost)} - आगाऊ: ${IndianCurrencyFormatter.formatRupees(expense.advancePaidAmount)} = प्रत्यक्ष अदा: ${IndianCurrencyFormatter.formatRupees(expense.amount)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Estimated cost for advance
                    if (expense.expenseType == "ADVANCE" && expense.totalEstimatedCost > 0) {
                        Text(
                            text = "अंदाजे एकूण खर्च: ${IndianCurrencyFormatter.formatRupees(expense.totalEstimatedCost)}",
                            fontSize = 10.5.sp,
                            color = TextMuted
                        )
                    }

                    // Bill Photo chip
                    if (expense.billImagePath.isNotBlank() && File(expense.billImagePath).exists()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF3E0),
                            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable { onViewImage(expense.billImagePath) }
                                .testTag("view_bill_photo_${expense.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("📷 बिल फोटो पहा", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                            }
                        }
                    }

                    if (expense.notes.isNotBlank()) {
                        Text(
                            text = "टीप: ${expense.notes}",
                            fontSize = 10.5.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Side: Amount & Actions
                Column(horizontalAlignment = Alignment.End) {
                    if (expense.isFree) {
                        Text(
                            text = "₹० (मोफत)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6A1B9A)
                        )
                        if (expense.totalEstimatedCost > 0 || expense.amount > 0) {
                            Text(
                                text = "मूल्य: ${IndianCurrencyFormatter.formatRupees(if (expense.totalEstimatedCost > 0) expense.totalEstimatedCost else expense.amount)}",
                                fontSize = 10.5.sp,
                                color = TextMuted
                            )
                        }
                    } else {
                        Text(
                            text = IndianCurrencyFormatter.formatRupees(expense.amount),
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (expense.expenseType) {
                                "ADVANCE" -> SaffronDark
                                "FINAL_SETTLEMENT" -> Color(0xFF2E7D32)
                                else -> CrimsonAccent
                            }
                        )
                        Text(
                            text = expense.paymentMode,
                            fontSize = 10.5.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SaffronDark, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = true }, modifier = Modifier.size(30.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonAccent, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Quick Settle Action Button for Unsettled Advance
            if (expense.expenseType == "ADVANCE" && !expense.isSettled && onSettleAdvance != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onSettleAdvance() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("अंतिम बिल जोडून हिशोब पूर्ण करा (Settle Advance)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("खर्च नोंद रद्द करायची आहे का?", fontWeight = FontWeight.Bold) },
            text = { Text("व्हाउचर क्र. ${expense.voucherNumber} (${expense.title}) चा खर्च रद्द केला जाईल.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent)
                ) {
                    Text("होय, रद्द करा", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("रद्द करा", color = TextSecondary)
                }
            }
        )
    }
}

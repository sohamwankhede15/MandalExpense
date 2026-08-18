package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.ExpenseTransaction
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.IndianCurrencyFormatter
import com.example.util.NumberToWordsConverter
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    initialExpense: ExpenseTransaction? = null,
    unsettledAdvances: List<ExpenseTransaction> = emptyList(),
    executiveMembers: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (ExpenseTransaction) -> Unit
) {
    val context = LocalContext.current

    // Expense Type: "REGULAR", "ADVANCE", "FINAL_SETTLEMENT", "FREE_SPONSORED"
    var expenseType by remember {
        mutableStateOf(
            initialExpense?.expenseType ?: if (initialExpense?.isFree == true) "FREE_SPONSORED" else "REGULAR"
        )
    }

    var title by remember { mutableStateOf(initialExpense?.title ?: "") }
    var amountText by remember {
        mutableStateOf(
            if (initialExpense != null && initialExpense.amount > 0) {
                if (initialExpense.amount % 1 == 0.0) initialExpense.amount.toInt().toString()
                else initialExpense.amount.toString()
            } else ""
        )
    }

    // For Advance: Total estimated cost
    var totalEstimatedCostText by remember {
        mutableStateOf(
            if (initialExpense != null && initialExpense.totalEstimatedCost > 0) {
                if (initialExpense.totalEstimatedCost % 1 == 0.0) initialExpense.totalEstimatedCost.toInt().toString()
                else initialExpense.totalEstimatedCost.toString()
            } else ""
        )
    }

    // For Final Settlement: Selected Linked Advance
    var selectedAdvanceId by remember { mutableStateOf<Long?>(initialExpense?.linkedAdvanceId) }
    var selectedAdvance by remember {
        mutableStateOf(unsettledAdvances.find { it.id == initialExpense?.linkedAdvanceId })
    }
    var totalBillAmountText by remember {
        mutableStateOf(
            if (initialExpense?.expenseType == "FINAL_SETTLEMENT" && initialExpense.totalEstimatedCost > 0) {
                if (initialExpense.totalEstimatedCost % 1 == 0.0) initialExpense.totalEstimatedCost.toInt().toString()
                else initialExpense.totalEstimatedCost.toString()
            } else ""
        )
    }

    // Free / Sponsored
    var sponsorName by remember { mutableStateOf(initialExpense?.sponsorName ?: "") }

    // Mahaprasad Flag & Subcategories
    var isMahaprasad by remember {
        mutableStateOf(initialExpense?.isMahaprasad ?: (initialExpense?.category == "महाप्रसाद व भोजन"))
    }
    var mahaprasadSubCategory by remember { mutableStateOf("") }

    // Member Attribution
    var memberAttribution by remember { mutableStateOf(initialExpense?.memberAttribution ?: "") }
    var isMemberDropdownExpanded by remember { mutableStateOf(false) }

    var category by remember { mutableStateOf(initialExpense?.category ?: "मंडप व स्टेज सजावट") }
    var paidTo by remember { mutableStateOf(initialExpense?.paidTo ?: "") }
    var paymentMode by remember { mutableStateOf(initialExpense?.paymentMode ?: "CASH") }
    var billReceiptNumber by remember { mutableStateOf(initialExpense?.billReceiptNumber ?: "") }
    var billImagePath by remember { mutableStateOf(initialExpense?.billImagePath ?: "") }
    var notes by remember { mutableStateOf(initialExpense?.notes ?: "") }
    var voucherNumber by remember { mutableStateOf(initialExpense?.voucherNumber ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var billAmountError by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val imagesDir = File(context.filesDir, "expense_bills")
                if (!imagesDir.exists()) imagesDir.mkdirs()
                val fileName = "bill_${System.currentTimeMillis()}.jpg"
                val destFile = File(imagesDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                billImagePath = destFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val categories = listOf(
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

    val mahaprasadSubCategories = listOf(
        "किराणा व धान्य",
        "भाजीपाला",
        "आचारी मानधन",
        "भांडी व पत्रावळी",
        "गॅस सिलेंडर",
        "दूध, तूप व मिठाई",
        "इतर महाप्रसाद साहित्य"
    )

    val paymentModes = listOf("CASH", "UPI / Online", "CHEQUE", "BANK_TRANSFER")

    // Dynamic calculated values for Final Settlement
    val advancePaid = selectedAdvance?.amount ?: (initialExpense?.advancePaidAmount ?: 0.0)
    val totalBillVal = totalBillAmountText.toDoubleOrNull() ?: 0.0
    val netPayableNow = if (totalBillVal > advancePaid) totalBillVal - advancePaid else 0.0

    val currentAmountVal = if (expenseType == "FINAL_SETTLEMENT") netPayableNow else (amountText.toDoubleOrNull() ?: 0.0)
    val amountInWords = if (currentAmountVal > 0) NumberToWordsConverter.convertToMarathi(currentAmountVal) else ""

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (expenseType) {
                                "ADVANCE" -> "⏳"
                                "FINAL_SETTLEMENT" -> "🧾"
                                "FREE_SPONSORED" -> "🎁"
                                else -> "💸"
                            },
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (initialExpense != null) "खर्च संपादन (Edit)" else "खर्च / आगाऊ रक्कम नोंदणी",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonAccent
                            )
                            Text(
                                text = "अखिल गणेशनगर मित्र मंडळ (AKGMM)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. EXPENSE TYPE SELECTOR
                Text(
                    text = "खर्चाचा प्रकार निवडा (Expense Type):",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val types = listOf(
                        Triple("REGULAR", "सामान्य खर्च", Icons.Default.AccountBalanceWallet),
                        Triple("ADVANCE", "आगाऊ रक्कम (Advance)", Icons.Default.HourglassTop),
                        Triple("FINAL_SETTLEMENT", "अंतिम खर्च (Final)", Icons.Default.ReceiptLong),
                        Triple("FREE_SPONSORED", "मोफत / प्रायोजित (Free)", Icons.Default.CardGiftcard)
                    )

                    types.forEach { (typeKey, label, icon) ->
                        FilterChip(
                            selected = expenseType == typeKey,
                            onClick = {
                                expenseType = typeKey
                                if (typeKey == "FREE_SPONSORED") {
                                    paymentMode = "FREE"
                                } else if (paymentMode == "FREE") {
                                    paymentMode = "CASH"
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (expenseType == typeKey) Color.White else CrimsonAccent
                                )
                            },
                            label = { Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonAccent,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFFFF3E0),
                                labelColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TYPE-SPECIFIC EXPLANATION CARD
                when (expenseType) {
                    "ADVANCE" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                            border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⏳", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "आगाऊ रक्कम नोंद (Advance Paid)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17)
                                    )
                                    Text(
                                        text = "ही दिलेली आगाऊ रक्कम तात्काळ रोख/UPI खर्चात मोजली जाईल आणि भविष्यात अंतिम बिलाशी जुळवली जाईल.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    "FINAL_SETTLEMENT" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🧾", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "अंतिम खर्च व आगाऊ रक्कम समायोजन (No Double Counting)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "एकूण बिलातून आधी दिलेली आगाऊ रक्कम वजा करून केवळ उर्वरित रक्कम खात्यातून वजा होईल.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    "FREE_SPONSORED" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                            border = BorderStroke(1.dp, Color(0xFFD1C4E9)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🎁", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "मोफत / प्रायोजित साहित्य व सेवा (Sponsored / In-kind)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF512DA8)
                                    )
                                    Text(
                                        text = "मंडळाला मिळालेल्या मोफत सेवेचे अंदाजित मूल्य नोंदवले जाईल. यामुळे रोख शिल्लक कमी होणार नाही.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (titleError && it.isNotBlank()) titleError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_title_input"),
                    label = { Text("खर्चाचे नाव / तपशील (Title) *") },
                    placeholder = {
                        Text(
                            when (expenseType) {
                                "ADVANCE" -> "उदा. साऊंड सिस्टीम बुकिंग आगाऊ रक्कम"
                                "FINAL_SETTLEMENT" -> "उदा. साऊंड सिस्टीम अंतिम बिल"
                                "FREE_SPONSORED" -> "उदा. मोफत 100 किलो साखर / मोफत फुलांची सजावट"
                                else -> "उदा. मंडप डेकोरेशन / दैनिक पूजा साहित्य"
                            }
                        )
                    },
                    isError = titleError,
                    supportingText = if (titleError) { { Text("खर्चाचे नाव आवश्यक आहे", color = CrimsonAccent) } } else null,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // DYNAMIC AMOUNTS SECTION
                if (expenseType == "FINAL_SETTLEMENT") {
                    // Final Settlement Form: Link to Advance + Total Bill
                    if (unsettledAdvances.isNotEmpty()) {
                        Text(
                            text = "संबंधित प्रलंबित आगाऊ रक्कम निवडा (Select Linked Advance):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            unsettledAdvances.forEach { adv ->
                                val isSelected = selectedAdvanceId == adv.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            selectedAdvanceId = null
                                            selectedAdvance = null
                                        } else {
                                            selectedAdvanceId = adv.id
                                            selectedAdvance = adv
                                            if (paidTo.isBlank() && adv.paidTo.isNotBlank()) paidTo = adv.paidTo
                                            if (category == "मंडप व स्टेज सजावट" && adv.category.isNotBlank()) category = adv.category
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "${adv.voucherNumber}: ${adv.title} (आगाऊ: ₹${adv.amount.toLong()})",
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF2E7D32),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Total Final Bill Amount
                    OutlinedTextField(
                        value = totalBillAmountText,
                        onValueChange = {
                            totalBillAmountText = it.filter { char -> char.isDigit() || char == '.' }
                            if (billAmountError && it.isNotBlank()) billAmountError = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("total_bill_amount_input"),
                        label = { Text("अंतिम एकूण बिल रक्कम (Total Final Bill ₹) *") },
                        placeholder = { Text("उदा. 50000") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = CrimsonAccent)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = billAmountError,
                        supportingText = if (billAmountError) {
                            { Text("एकूण बिल रक्कम प्रविष्ट करा", color = CrimsonAccent) }
                        } else null,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calculation Summary Card for Final Settlement
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("एकूण अंतिम बिल:", fontSize = 12.sp, color = TextSecondary)
                                Text(IndianCurrencyFormatter.formatRupees(totalBillVal), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("वजा: आधी दिलेली आगाऊ रक्कम:", fontSize = 12.sp, color = Color(0xFFE65100))
                                Text("- ${IndianCurrencyFormatter.formatRupees(advancePaid)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("आता प्रत्यक्ष द्यायची रक्कम (Net Outflow):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CrimsonAccent)
                                Text(IndianCurrencyFormatter.formatRupees(netPayableNow), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = CrimsonAccent)
                            }
                            if (amountInWords.isNotBlank()) {
                                Text("अक्षरी: $amountInWords", fontSize = 11.sp, color = SaffronDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                } else if (expenseType == "ADVANCE") {
                    // Advance Amount + Estimated Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = {
                                amountText = it.filter { char -> char.isDigit() || char == '.' }
                                if (amountError && it.isNotBlank()) amountError = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("expense_amount_input"),
                            label = { Text("आता दिलेली आगाऊ रक्कम (₹) *") },
                            placeholder = { Text("उदा. 20000") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = CrimsonAccent)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = amountError,
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = totalEstimatedCostText,
                            onValueChange = {
                                totalEstimatedCostText = it.filter { char -> char.isDigit() || char == '.' }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("अंदाजित एकूण खर्च (₹)") },
                            placeholder = { Text("उदा. 50000") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = SaffronPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    if (amountInWords.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("अक्षरी आगाऊ: $amountInWords", fontSize = 11.sp, color = SaffronDark, fontWeight = FontWeight.SemiBold)
                    }

                } else if (expenseType == "FREE_SPONSORED") {
                    // Free / Sponsored estimated value & donor name
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it.filter { char -> char.isDigit() || char == '.' }
                            if (amountError && it.isNotBlank()) amountError = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_amount_input"),
                        label = { Text("साहित्य / सेवेचे अंदाजित बाजार मूल्य (₹) *") },
                        placeholder = { Text("उदा. 5000") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF512DA8))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = amountError,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = sponsorName,
                        onValueChange = { sponsorName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("देणगीदार / प्रायोजक व्यक्तीचे नाव (Donor / Sponsor)") },
                        placeholder = { Text("उदा. श्री. राहुल शिंदे (हडपसर)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF512DA8))
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                } else {
                    // Regular Expense Amount
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it.filter { char -> char.isDigit() || char == '.' }
                            if (amountError && it.isNotBlank()) amountError = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_amount_input"),
                        label = { Text("खर्च रक्कम (₹) *") },
                        placeholder = { Text("उदा. 2500") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = CrimsonAccent)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = amountError,
                        supportingText = if (amountError) {
                            { Text("वैध रक्कम प्रविष्ट करा", color = CrimsonAccent) }
                        } else if (amountInWords.isNotBlank()) {
                            { Text("अक्षरी: $amountInWords", color = SaffronDark, fontWeight = FontWeight.SemiBold) }
                        } else null,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. MAHAPRASAD SPECIAL SECTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMahaprasad) Color(0xFFFFF3E0) else Color(0xFFFAFAFA)
                    ),
                    border = BorderStroke(1.dp, if (isMahaprasad) OrangeBorder else Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Fastfood, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("🍲 महाप्रसाद विशेष विभाग", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("महाप्रसादाचा स्वतंत्र हिशोब ठेवण्यासाठी", fontSize = 10.5.sp, color = TextSecondary)
                                }
                            }
                            Switch(
                                checked = isMahaprasad,
                                onCheckedChange = {
                                    isMahaprasad = it
                                    if (it) category = "महाप्रसाद व भोजन"
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary, checkedTrackColor = Color(0xFFFFE0B2))
                            )
                        }

                        if (isMahaprasad) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("महाप्रसाद प्रकार निवडा:", fontSize = 11.5.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                mahaprasadSubCategories.forEach { subCat ->
                                    FilterChip(
                                        selected = mahaprasadSubCategory == subCat,
                                        onClick = {
                                            mahaprasadSubCategory = subCat
                                            if (title.isBlank() || mahaprasadSubCategories.any { title.contains(it) }) {
                                                title = "महाप्रसाद - $subCat"
                                            }
                                        },
                                        label = { Text(text = subCat, fontSize = 10.5.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = OrangePrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. EXECUTIVE MEMBER ATTRIBUTION (15 Member List)
                Text(
                    text = "खर्च जबाबदार कार्यकारणी सदस्य (Member Attribution):",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = isMemberDropdownExpanded,
                    onExpandedChange = { isMemberDropdownExpanded = !isMemberDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (memberAttribution.isBlank()) "कोणताही नाही (सामाईक खर्च)" else memberAttribution,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMemberDropdownExpanded) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SaffronDark)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = isMemberDropdownExpanded,
                        onDismissRequest = { isMemberDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("कोणताही नाही (सामाईक खर्च)", fontWeight = FontWeight.Medium) },
                            onClick = {
                                memberAttribution = ""
                                isMemberDropdownExpanded = false
                            }
                        )

                        executiveMembers.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member) },
                                onClick = {
                                    memberAttribution = member
                                    isMemberDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Category Selection (if not Mahaprasad)
                if (!isMahaprasad) {
                    Text(text = "खर्च वर्गवारी (Expense Category):", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(text = cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFCDD2),
                                    selectedLabelColor = CrimsonAccent
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 5. Paid to / Vendor and Bill Receipt No
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = paidTo,
                        onValueChange = { paidTo = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expense_paid_to_input"),
                        label = { Text("दुकानदार / व्यक्तीचे नाव") },
                        placeholder = { Text("उदा. श्री समर्थ डेकोरेटर्स") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = SaffronPrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = billReceiptNumber,
                        onValueChange = { billReceiptNumber = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("बिल / पावती क्र.") },
                        placeholder = { Text("उदा. Bill #451") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = SaffronPrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 6. Bill Photo Attachment
                Text(text = "बिल / पावतीचा फोटो (Bill Photo Attachment):", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                if (billImagePath.isNotBlank() && File(billImagePath).exists()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, OrangeBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(File(billImagePath)),
                                contentDescription = "Bill Photo",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("📷 बिल जोडले आहे", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                                Text("फोटो बदलण्यासाठी क्लिक करा", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Row {
                            IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Change", tint = OrangePrimary)
                            }
                            IconButton(onClick = { billImagePath = "" }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = CrimsonAccent)
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("attach_bill_photo_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, OrangeBorder)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = OrangePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📷 बिलाचा फोटो जोडा (गॅलरी / कॅमेरा)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 7. Payment Mode (if not Free)
                if (expenseType != "FREE_SPONSORED") {
                    Text(text = "पेमेंट पद्धत:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentModes.forEach { mode ->
                            FilterChip(
                                selected = paymentMode == mode,
                                onClick = { paymentMode = mode },
                                label = { Text(text = mode, fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFE082),
                                    selectedLabelColor = TextPrimary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 8. Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("अतिरिक्त टीप (Notes)") },
                    placeholder = { Text("उदा. उर्वरित रक्कम नंतर दिली जाईल") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "रद्द करा")
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                                return@Button
                            }

                            val calculatedAmount: Double
                            val totalEst: Double
                            val advPaidAmt: Double
                            var isFreeExpense = false

                            when (expenseType) {
                                "FINAL_SETTLEMENT" -> {
                                    if (totalBillVal <= 0.0) {
                                        billAmountError = true
                                        return@Button
                                    }
                                    calculatedAmount = netPayableNow
                                    totalEst = totalBillVal
                                    advPaidAmt = advancePaid
                                }
                                "ADVANCE" -> {
                                    val amt = amountText.toDoubleOrNull()
                                    if (amt == null || amt <= 0) {
                                        amountError = true
                                        return@Button
                                    }
                                    calculatedAmount = amt
                                    totalEst = totalEstimatedCostText.toDoubleOrNull() ?: amt
                                    advPaidAmt = 0.0
                                }
                                "FREE_SPONSORED" -> {
                                    val amt = amountText.toDoubleOrNull() ?: 0.0
                                    calculatedAmount = amt
                                    totalEst = amt
                                    advPaidAmt = 0.0
                                    isFreeExpense = true
                                }
                                else -> {
                                    val amt = amountText.toDoubleOrNull()
                                    if (amt == null || amt <= 0) {
                                        amountError = true
                                        return@Button
                                    }
                                    calculatedAmount = amt
                                    totalEst = amt
                                    advPaidAmt = 0.0
                                }
                            }

                            val exp = ExpenseTransaction(
                                id = initialExpense?.id ?: 0,
                                voucherNumber = voucherNumber,
                                title = title.trim(),
                                category = if (isMahaprasad) "महाप्रसाद व भोजन" else category,
                                amount = calculatedAmount,
                                paymentMode = if (isFreeExpense) "FREE" else paymentMode,
                                paidTo = paidTo.trim(),
                                timestamp = initialExpense?.timestamp ?: System.currentTimeMillis(),
                                notes = notes.trim(),
                                billReceiptNumber = billReceiptNumber.trim(),
                                billImagePath = billImagePath.trim(),
                                expenseType = expenseType,
                                linkedAdvanceId = if (expenseType == "FINAL_SETTLEMENT") selectedAdvanceId else null,
                                advancePaidAmount = advPaidAmt,
                                totalEstimatedCost = totalEst,
                                isSettled = false,
                                isFree = isFreeExpense,
                                sponsorName = sponsorName.trim(),
                                isMahaprasad = isMahaprasad,
                                memberAttribution = memberAttribution.trim()
                            )
                            onSave(exp)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_expense_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent)
                    ) {
                        Text(
                            text = if (initialExpense != null) "अपडेट करा" else "खर्च सेव्ह करा 💰",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

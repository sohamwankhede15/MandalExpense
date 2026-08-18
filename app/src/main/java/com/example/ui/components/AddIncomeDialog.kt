package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.IncomeTransaction
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.NumberToWordsConverter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddIncomeDialog(
    initialIncome: IncomeTransaction? = null,
    onDismiss: () -> Unit,
    onSave: (IncomeTransaction) -> Unit
) {
    var sourceName by remember { mutableStateOf(initialIncome?.sourceName ?: "") }
    var amountText by remember { mutableStateOf(if (initialIncome != null && initialIncome.amount > 0) initialIncome.amount.toInt().toString() else "") }
    var category by remember { mutableStateOf(initialIncome?.category ?: "लिलाव (Auction)") }
    var paymentMode by remember { mutableStateOf(initialIncome?.paymentMode ?: "CASH") }
    var receivedBy by remember { mutableStateOf(initialIncome?.receivedBy ?: "") }
    var notes by remember { mutableStateOf(initialIncome?.notes ?: "") }

    var sourceError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = listOf("लिलाव (Auction)", "प्रायोजक (Sponsorship)", "स्टॉल भाडे (Stall Rent)", "बँक व्याज (Bank Interest)", "इतर देणगी")
    val paymentModes = listOf("CASH", "UPI", "CHEQUE", "BANK_TRANSFER")

    val amountVal = amountText.toDoubleOrNull() ?: 0.0
    val amountInWords = if (amountVal > 0) NumberToWordsConverter.convertToMarathi(amountVal) else ""

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp),
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
                        Text(text = "💰", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (initialIncome != null) "इतर उत्पन्न संपादन" else "इतर उत्पन्न नोंदवा",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = sourceName,
                    onValueChange = {
                        sourceName = it
                        if (sourceError && it.isNotBlank()) sourceError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("income_source_input"),
                    label = { Text("उत्पन्नाचा स्त्रोत / देणाऱ्याचे नाव *") },
                    placeholder = { Text("उदा. मोदक लिलाव / मुख्य कमान प्रायोजक") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = GreenSuccess)
                    },
                    isError = sourceError,
                    supportingText = if (sourceError) { { Text("नाव आवश्यक आहे", color = CrimsonAccent) } } else null,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char -> char.isDigit() || char == '.' }
                        if (amountError && it.isNotBlank()) amountError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("income_amount_input"),
                    label = { Text("जमा रक्कम (₹) *") },
                    placeholder = { Text("उदा. 5000") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = GreenSuccess)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    supportingText = if (amountError) {
                        { Text("वैध रक्कम प्रविष्ट करा", color = CrimsonAccent) }
                    } else if (amountInWords.isNotBlank()) {
                        { Text("अक्षरी: $amountInWords", color = GreenSuccess, fontWeight = FontWeight.SemiBold) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "उत्पन्न वर्गवारी:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(text = cat, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFC8E6C9),
                                selectedLabelColor = GreenSuccess
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = receivedBy,
                        onValueChange = { receivedBy = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("स्वीकारणारा / खजिनदार") },
                        placeholder = { Text("खजिनदार") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("टीप") },
                        placeholder = { Text("तपशील") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                            if (sourceName.isBlank()) {
                                sourceError = true
                                return@Button
                            }
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt <= 0) {
                                amountError = true
                                return@Button
                            }

                            val inc = IncomeTransaction(
                                id = initialIncome?.id ?: 0,
                                sourceName = sourceName.trim(),
                                category = category,
                                amount = amt,
                                paymentMode = paymentMode,
                                timestamp = initialIncome?.timestamp ?: System.currentTimeMillis(),
                                notes = notes.trim(),
                                receivedBy = receivedBy.trim()
                            )
                            onSave(inc)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_income_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                    ) {
                        Text(
                            text = if (initialIncome != null) "अपडेट करा" else "उत्पन्न सेव्ह करा 💰",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

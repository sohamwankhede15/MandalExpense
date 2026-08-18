package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OtherPersonType
import com.example.data.model.PendingVargani
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.ui.theme.RedExpense
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPendingDialog(
    onDismiss: () -> Unit,
    onSave: (PendingVargani) -> Unit
) {
    var personType by remember { mutableStateOf("घरमालक") }
    var otherPersonType by remember { mutableStateOf("व्यावसायिक / व्यापारी") }
    var customCategoryName by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val otherSubCategories = listOf(
        "व्यावसायिक / व्यापारी",
        "नगरसेवक",
        "आमदार",
        "राजकीय व्यक्ती",
        "देणगीदार",
        "प्रायोजक",
        "इतर"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteCard),
            border = BorderStroke(1.dp, OrangeBorder)
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
                    Text(
                        text = "शिल्लक वर्गणी नोंदवा",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Person Type Selector (घरमालक, भाडेकरू, इतर)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isOwner = personType == "घरमालक"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { personType = "घरमालक"; errorMessage = null },
                        color = if (isOwner) OrangePrimaryDark else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = if (isOwner) Color.White else TextSecondary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "घरमालक", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isOwner) Color.White else TextSecondary)
                        }
                    }

                    val isTenant = personType == "भाडेकरू"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { personType = "भाडेकरू"; errorMessage = null },
                        color = if (isTenant) OrangePrimaryDark else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Apartment, contentDescription = null, tint = if (isTenant) Color.White else TextSecondary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "भाडेकरू", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isTenant) Color.White else TextSecondary)
                        }
                    }

                    val isOther = personType == "इतर"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { personType = "इतर"; errorMessage = null },
                        color = if (isOther) OrangePrimaryDark else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = if (isOther) Color.White else TextSecondary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "इतर", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isOther) Color.White else TextSecondary)
                        }
                    }
                }

                // Sub-category selector for "इतर"
                if (personType == "इतर") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "प्रकार निवडा:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                otherSubCategories.forEach { cat ->
                                    val isSelected = otherPersonType == cat
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) OrangePrimaryDark else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) OrangePrimaryDark else Color(0xFFFFD54F)),
                                        modifier = Modifier.clickable { otherPersonType = cat }
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (otherPersonType == "इतर") {
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = customCategoryName,
                                    onValueChange = { customCategoryName = it },
                                    label = { Text("कस्टम प्रकार नाव *") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = {
                        Text(
                            when (personType) {
                                "घरमालक" -> "घरमालकाचे नाव *"
                                "भाडेकरू" -> "भाडेकरूचे नाव *"
                                else -> "नाव / संस्था / व्यापारी नाव *"
                            }
                        )
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = OrangePrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // If Tenant -> Owner Name
                if (personType == "भाडेकरू") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it; errorMessage = null },
                        label = { Text("संबंधित घरमालकाचे नाव *") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = OrangePrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Expected Amount
                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it.filter { c -> c.isDigit() }; errorMessage = null },
                    label = { Text("अपेक्षित वर्गणी रक्कम (₹) *") },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp), color = OrangePrimaryDark) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { if (it.length <= 10) mobileNumber = it },
                    label = { Text("मोबाईल क्रमांक") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = OrangePrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("पत्ता / रूम क्र.") },
                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("टीप (उदा. १ तारखेला देणार)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage ?: "", color = RedExpense, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("रद्द करा")
                    }

                    Button(
                        onClick = {
                            val amt = amountString.toDoubleOrNull() ?: 0.0
                            if (name.trim().isBlank()) {
                                errorMessage = "कृपया नाव प्रविष्ट करा."
                                return@Button
                            }
                            if (personType == "भाडेकरू" && ownerName.trim().isBlank()) {
                                errorMessage = "भाडेकरूसाठी घरमालकाचे नाव आवश्यक आहे."
                                return@Button
                            }
                            if (personType == "इतर" && otherPersonType == "इतर" && customCategoryName.trim().isBlank()) {
                                errorMessage = "कृपया कस्टम प्रकार नाव प्रविष्ट करा."
                                return@Button
                            }
                            if (amt <= 0.0) {
                                errorMessage = "कृपया अपेक्षित रक्कम प्रविष्ट करा."
                                return@Button
                            }

                            onSave(
                                PendingVargani(
                                    name = name.trim(),
                                    personType = personType,
                                    ownerName = if (personType == "भाडेकरू") ownerName.trim() else "",
                                    otherPersonType = if (personType == "इतर") otherPersonType else "",
                                    customCategoryName = if (personType == "इतर" && otherPersonType == "इतर") customCategoryName.trim() else "",
                                    mobileNumber = mobileNumber.trim(),
                                    address = address.trim(),
                                    expectedAmount = amt,
                                    note = note.trim()
                                )
                            )
                        },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark)
                    ) {
                        Text("नोंदवा", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

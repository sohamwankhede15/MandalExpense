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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OtherPersonType
import com.example.data.model.PersonType
import com.example.data.model.VarganiTransaction
import com.example.ui.theme.GreenSuccess
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
import com.example.util.NumberToWordsConverter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddVarganiDialog(
    initialVargani: VarganiTransaction? = null,
    nextPavtiNumber: String,
    collectorName: String = "",
    knownOwnerNames: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (VarganiTransaction) -> Unit
) {
    // Primary Person Type: "घरमालक", "भाडेकरू", "इतर"
    var personType by remember {
        mutableStateOf(
            if (initialVargani != null) PersonType.getStandardMarathiLabel(initialVargani.personType) else "घरमालक"
        )
    }

    // Sub-category for "इतर"
    var otherPersonType by remember {
        mutableStateOf(
            if (initialVargani != null && initialVargani.otherPersonType.isNotBlank())
                OtherPersonType.fromCodeOrLabel(initialVargani.otherPersonType).marathiLabel
            else
                "व्यावसायिक / व्यापारी"
        )
    }

    var customCategoryName by remember { mutableStateOf(initialVargani?.customCategoryName ?: "") }
    var contributorName by remember { mutableStateOf(initialVargani?.contributorName ?: "") }
    var ownerName by remember { mutableStateOf(initialVargani?.ownerName ?: "") }
    var mobileNumber by remember { mutableStateOf(initialVargani?.mobileNumber ?: "") }
    var address by remember { mutableStateOf(initialVargani?.address ?: "") }
    var amountString by remember {
        mutableStateOf(
            if (initialVargani != null && initialVargani.amount > 0) initialVargani.amount.toLong().toString() else ""
        )
    }
    var paymentMode by remember { mutableStateOf(initialVargani?.paymentMode ?: "रोख") }
    var notes by remember { mutableStateOf(initialVargani?.notes ?: "") }
    var pavtiNumber by remember { mutableStateOf(initialVargani?.pavtiNumber ?: nextPavtiNumber) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amountDouble = amountString.toDoubleOrNull() ?: 0.0

    // Auto-update amount in words in Marathi
    val amountInWords = remember(amountDouble) {
        if (amountDouble > 0) NumberToWordsConverter.convertToMarathi(amountDouble) else ""
    }

    // Dynamic Quick Amount Presets
    val ownerPresets = listOf("1000", "1501", "2001", "3001", "5001", "11000")
    val tenantPresets = listOf("101", "251", "501", "751", "1001", "2001")
    val otherPresets = listOf("501", "1001", "2001", "5001", "11000", "21000")
    val quickPresets = when (personType) {
        "घरमालक" -> ownerPresets
        "भाडेकरू" -> tenantPresets
        else -> otherPresets
    }

    val otherSubCategories = listOf(
        "व्यावसायिक / व्यापारी",
        "नगरसेवक",
        "आमदार",
        "राजकीय व्यक्ती",
        "देणगीदार",
        "प्रायोजक",
        "इतर"
    )

    val paymentModes = listOf("रोख", "UPI", "गुगल पे", "फोन पे", "चेक", "बँक")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("add_vargani_dialog_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.5.dp, OrangeBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
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
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = "Pavti",
                                    tint = OrangePrimaryDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (initialVargani == null) "नवीन पावती नोंदवा" else "पावती बदल करा",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "पावती क्र.: $pavtiNumber",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOrange
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "बंद करा",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION 1: MANDATORY PERSON TYPE SELECTOR (घरमालक / भाडेकरू / इतर)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "व्यक्तीचा प्रकार निवडा *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // 1. घरमालक (Owner)
                        val isOwner = personType == "घरमालक"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    personType = "घरमालक"
                                    errorMessage = null
                                }
                                .testTag("person_type_owner_btn"),
                            color = if (isOwner) OrangePrimaryDark else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = if (isOwner) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "घरमालक",
                                    tint = if (isOwner) Color.White else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "घरमालक",
                                    fontSize = 13.sp,
                                    fontWeight = if (isOwner) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isOwner) Color.White else TextSecondary
                                )
                            }
                        }

                        // 2. भाडेकरू (Tenant)
                        val isTenant = personType == "भाडेकरू"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    personType = "भाडेकरू"
                                    errorMessage = null
                                }
                                .testTag("person_type_tenant_btn"),
                            color = if (isTenant) OrangePrimaryDark else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = if (isTenant) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = "भाडेकरू",
                                    tint = if (isTenant) Color.White else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "भाडेकरू",
                                    fontSize = 13.sp,
                                    fontWeight = if (isTenant) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTenant) Color.White else TextSecondary
                                )
                            }
                        }

                        // 3. इतर (Other)
                        val isOther = personType == "इतर"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    personType = "इतर"
                                    errorMessage = null
                                }
                                .testTag("person_type_other_btn"),
                            color = if (isOther) OrangePrimaryDark else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = if (isOther) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "इतर",
                                    tint = if (isOther) Color.White else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "इतर",
                                    fontSize = 13.sp,
                                    fontWeight = if (isOther) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isOther) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    // Rule Notification Banner for Owner
                    if (personType == "घरमालक") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = OrangeContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = OrangePrimaryDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "घरमालकांसाठी किमान वर्गणी: ₹१,००० किंवा अधिक",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OrangePrimaryDark
                                )
                            }
                        }
                    }

                    // SUB-CATEGORY SELECTION FOR "इतर"
                    if (personType == "इतर") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFE082)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "इतर वर्गणी प्रकार निवडा *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    otherSubCategories.forEach { subCat ->
                                        val isSelected = otherPersonType == subCat
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) OrangePrimaryDark else Color.White,
                                            border = BorderStroke(1.dp, if (isSelected) OrangePrimaryDark else Color(0xFFFFD54F)),
                                            modifier = Modifier.clickable {
                                                otherPersonType = subCat
                                                errorMessage = null
                                            }
                                        ) {
                                            Text(
                                                text = subCat,
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else TextPrimary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                // Custom text box if "इतर -> इतर" is selected
                                if (otherPersonType == "इतर") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = customCategoryName,
                                        onValueChange = {
                                            customCategoryName = it
                                            errorMessage = null
                                        },
                                        label = { Text("वर्गणी प्रकार नाव प्रविष्ट करा *") },
                                        placeholder = { Text("उदा. स्थानिक संस्था / मित्र परिवार / ज्येष्ठ नागरिक") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("custom_other_category_input"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = OrangePrimaryDark,
                                            unfocusedBorderColor = OrangeBorder,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Field: नाव (Contributor Name / Business Name / Donor Name)
                OutlinedTextField(
                    value = contributorName,
                    onValueChange = {
                        contributorName = it
                        errorMessage = null
                    },
                    label = {
                        Text(
                            when (personType) {
                                "घरमालक" -> "घरमालकाचे पूर्ण नाव *"
                                "भाडेकरू" -> "भाडेकरूचे पूर्ण नाव *"
                                else -> when (otherPersonType) {
                                    "व्यावसायिक / व्यापारी" -> "व्यापारी / दुकानाचे नाव *"
                                    "नगरसेवक" -> "नगरसेवकाचे नाव *"
                                    "आमदार" -> "आमदारांचे नाव *"
                                    "राजकीय व्यक्ती" -> "राजकीय व्यक्तीचे नाव *"
                                    "देणगीदार" -> "देणगीदाराचे नाव *"
                                    "प्रायोजक" -> "प्रायोजकाचे नाव *"
                                    else -> "व्यक्तीचे / संस्थेचे नाव *"
                                }
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when (personType) {
                                "घरमालक" -> "उदा. सुरेश विठ्ठल देशमुख"
                                "भाडेकरू" -> "उदा. राहुल संतोष पाटील"
                                else -> "उदा. श्री गणेश ट्रेडर्स / मा. नगरसेवक"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (personType) {
                                "घरमालक" -> Icons.Default.Person
                                "भाडेकरू" -> Icons.Default.Person
                                else -> Icons.Default.Business
                            },
                            contentDescription = null,
                            tint = OrangePrimary
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contributor_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryDark,
                        unfocusedBorderColor = OrangeBorder
                    )
                )

                // FIELD FOR TENANT: घरमालकाचे नाव (Owner Name) - MANDATORY FOR TENANTS
                if (personType == "भाडेकरू") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = {
                                ownerName = it
                                errorMessage = null
                            },
                            label = { Text("संबंधित घरमालकाचे नाव (Owner Name) *") },
                            placeholder = { Text("ज्यांच्या जागेत राहतात त्या घरमालकाचे नाव") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = OrangePrimary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owner_name_for_tenant_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimaryDark,
                                unfocusedBorderColor = OrangeBorder
                            )
                        )

                        // Quick suggestion chips of registered owners if available
                        if (knownOwnerNames.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "नोंदणीकृत घरमालकांमधून निवडा:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                knownOwnerNames.take(5).forEach { name ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (ownerName == name) OrangeContainer else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (ownerName == name) OrangePrimary else Color.Transparent),
                                        modifier = Modifier.clickable { ownerName = name }
                                    ) {
                                        Text(
                                            text = "🏠 $name",
                                            fontSize = 11.sp,
                                            fontWeight = if (ownerName == name) FontWeight.Bold else FontWeight.Normal,
                                            color = if (ownerName == name) OrangePrimaryDark else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mobile Number
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { if (it.length <= 10) mobileNumber = it },
                    label = { Text("मोबाईल क्रमांक") },
                    placeholder = { Text("९८XXXXXXXX") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = OrangePrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mobile_number_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryDark,
                        unfocusedBorderColor = OrangeBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Address / Room No / Shop No
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = {
                        Text(
                            if (personType == "इतर") "पत्ता / दुकान क्र. / कार्यालय" else "पत्ता / रूम क्र. / गल्ली / विभाग"
                        )
                    },
                    placeholder = { Text("उदा. गणेशनगर गल्ली क्र. ३ / शॉप क्र. ५") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryDark,
                        unfocusedBorderColor = OrangeBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 2: AMOUNT & PRESETS
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = amountString,
                        onValueChange = {
                            amountString = it.filter { c -> c.isDigit() }
                            errorMessage = null
                        },
                        label = { Text("वर्गणीची रक्कम (₹) *") },
                        placeholder = { Text(if (personType == "घरमालक") "किमान १०००" else "रक्कम टाका") },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimaryDark,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amount_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimaryDark,
                            unfocusedBorderColor = OrangeBorder
                        )
                    )

                    // Display Amount in Words
                    if (amountInWords.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "अक्षरी: $amountInWords",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = OrangePrimaryDark,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fast Amount Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPresets.forEach { preset ->
                            val isSelected = amountString == preset
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) OrangePrimaryDark else OrangeContainer,
                                border = BorderStroke(1.dp, if (isSelected) OrangePrimaryDark else OrangeBorder),
                                modifier = Modifier.clickable {
                                    amountString = preset
                                    errorMessage = null
                                }
                            ) {
                                Text(
                                    text = "₹$preset",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else OrangePrimaryDark,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 3: PAYMENT MODE
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "पेमेंट पद्धत *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentModes.forEach { mode ->
                            val isSelected = paymentMode == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) OrangePrimaryDark else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { paymentMode = mode }
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes / Remark
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("नोंद / टीप / संदर्भ (ऐच्छिक)") },
                    placeholder = { Text("उदा. विशेष सहकार्य / मंडप जवळ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryDark,
                        unfocusedBorderColor = OrangeBorder
                    )
                )

                // Error Banner Display
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = RedContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = RedExpense,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedExpense
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (रद्द करा / पावती जतन करा)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(text = "रद्द करा", color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            // Validation Check
                            if (contributorName.trim().isBlank()) {
                                errorMessage = "कृपया नाव प्रविष्ट करा."
                                return@Button
                            }

                            // घरमालक rule: किमान ₹१,०००
                            if (personType == "घरमालक" && amountDouble < 1000.0) {
                                errorMessage = "घरमालकांसाठी किमान वर्गणी ₹१,००० असणे आवश्यक आहे."
                                return@Button
                            }

                            // भाडेकरू rule: घरमालकाचे नाव आवश्यक
                            if (personType == "भाडेकरू" && ownerName.trim().isBlank()) {
                                errorMessage = "भाडेकरूसाठी संबंधित घरमालकाचे नाव असणे आवश्यक आहे."
                                return@Button
                            }

                            // इतर rule: If 'इतर' subcategory selected, custom name is required
                            if (personType == "इतर" && otherPersonType == "इतर" && customCategoryName.trim().isBlank()) {
                                errorMessage = "कृपया इतर वर्गणी प्रकाराचे नाव प्रविष्ट करा."
                                return@Button
                            }

                            if (amountDouble <= 0.0) {
                                errorMessage = "कृपया वैध वर्गणी रक्कम प्रविष्ट करा."
                                return@Button
                            }

                            val selectedCategory = when (personType) {
                                "घरमालक" -> "घरगुती वर्गणी (मालक)"
                                "भाडेकरू" -> "घरगुती वर्गणी (भाडेकरू)"
                                else -> OtherPersonType.getDisplayLabel(otherPersonType, customCategoryName)
                            }

                            val transaction = (initialVargani ?: VarganiTransaction(
                                pavtiNumber = pavtiNumber,
                                contributorName = contributorName.trim(),
                                personType = personType,
                                ownerName = if (personType == "भाडेकरू") ownerName.trim() else "",
                                otherPersonType = if (personType == "इतर") otherPersonType else "",
                                customCategoryName = if (personType == "इतर" && otherPersonType == "इतर") customCategoryName.trim() else "",
                                mobileNumber = mobileNumber.trim(),
                                address = address.trim(),
                                amount = amountDouble,
                                amountInWords = amountInWords,
                                category = selectedCategory,
                                paymentMode = paymentMode,
                                notes = notes.trim(),
                                collectedBy = collectorName
                            )).copy(
                                contributorName = contributorName.trim(),
                                personType = personType,
                                ownerName = if (personType == "भाडेकरू") ownerName.trim() else "",
                                otherPersonType = if (personType == "इतर") otherPersonType else "",
                                customCategoryName = if (personType == "इतर" && otherPersonType == "इतर") customCategoryName.trim() else "",
                                mobileNumber = mobileNumber.trim(),
                                address = address.trim(),
                                amount = amountDouble,
                                amountInWords = amountInWords,
                                category = selectedCategory,
                                paymentMode = paymentMode,
                                notes = notes.trim(),
                                collectedBy = collectorName
                            )

                            onSave(transaction)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_vargani_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark)
                    ) {
                        Text(
                            text = if (initialVargani == null) "पावती जतन करा" else "अपडेट करा",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

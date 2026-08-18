package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MandalSettings
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.MarigoldSecondary
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: MandalSettings,
    onSaveSettings: (MandalSettings) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackupDialog: () -> Unit
) {
    var mandalName by remember(settings) { mutableStateOf(settings.mandalName) }
    var subTitle by remember(settings) { mutableStateOf(settings.subTitle) }
    var address by remember(settings) { mutableStateOf(settings.address) }
    var festivalYear by remember(settings) { mutableStateOf(settings.festivalYear) }
    var registrationNumber by remember(settings) { mutableStateOf(settings.registrationNumber) }
    var authorizedSignatory by remember(settings) { mutableStateOf(settings.authorizedSignatory) }
    var receiptPrefix by remember(settings) { mutableStateOf(settings.receiptPrefix) }
    var upiId by remember(settings) { mutableStateOf(settings.upiId) }
    var executiveMembers by remember(settings) { mutableStateOf(settings.executiveMembers) }
    var isPinEnabled by remember(settings) { mutableStateOf(settings.isPinEnabled) }
    var securityPin by remember(settings) { mutableStateOf(settings.securityPin) }
    var isSavedToast by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Save Banner
            Button(
                onClick = {
                    val updated = settings.copy(
                        mandalName = mandalName.trim(),
                        subTitle = subTitle.trim(),
                        address = address.trim(),
                        festivalYear = festivalYear.trim(),
                        registrationNumber = registrationNumber.trim(),
                        authorizedSignatory = authorizedSignatory.trim(),
                        receiptPrefix = receiptPrefix.trim(),
                        upiId = upiId.trim(),
                        executiveMembers = executiveMembers.trim(),
                        isPinEnabled = isPinEnabled,
                        securityPin = securityPin.trim()
                    )
                    onSaveSettings(updated)
                    isSavedToast = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_settings_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSavedToast) "बदल सेव्ह झाले! (Settings Saved ✓)" else "सेटिंग्ज सेव्ह करा (Save Settings)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Section 1: Mandal Profile
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚩 मंडळाची माहिती (Mandal Profile)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = mandalName,
                        onValueChange = { mandalName = it; isSavedToast = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_mandal_name_input"),
                        label = { Text("मंडळाचे पूर्ण नाव *") },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subTitle,
                        onValueChange = { subTitle = it; isSavedToast = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("उपनिशाणी / टॅगलाईन") },
                        placeholder = { Text("उदा. सार्वजनिक गणेशोत्सव मंडळ") },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it; isSavedToast = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("मंडळाचा पत्ता व परिसर") },
                        placeholder = { Text("उदा. शिवाजी चौक, पुणे - ४११०३०") },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = festivalYear,
                            onValueChange = { festivalYear = it; isSavedToast = false },
                            modifier = Modifier.weight(1f),
                            label = { Text("उत्सव वर्ष") },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = registrationNumber,
                            onValueChange = { registrationNumber = it; isSavedToast = false },
                            modifier = Modifier.weight(1f),
                            label = { Text("नोंदणी क्र. (Reg No)") },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section 2: Pavti & Committee Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📜 पावती व स्वाक्षरी (Receipt & Signatory)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = authorizedSignatory,
                        onValueChange = { authorizedSignatory = it; isSavedToast = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("अधिकृत स्वाक्षरीकर्ता / खजिनदार नाव") },
                        placeholder = { Text("उदा. सागर मोरे (खजिनदार)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SaffronPrimary)
                        },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = receiptPrefix,
                            onValueChange = { receiptPrefix = it; isSavedToast = false },
                            modifier = Modifier.weight(1f),
                            label = { Text("पावती प्रीफिक्स") },
                            placeholder = { Text("उदा. PAV-") },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { upiId = it; isSavedToast = false },
                            modifier = Modifier.weight(1f),
                            label = { Text("UPI ID (देणगीसाठी)") },
                            placeholder = { Text("mandal@upi") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = SaffronPrimary)
                            },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section: Executive Members (पदाधिकारी यादी)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "👥 नियामक / पदाधिकारी यादी (Executive Committee)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                            Text(
                                text = "खर्च व आगाऊ रकमेची जबाबदारी सोपवण्यासाठी १५ पदाधिकारी",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = executiveMembers,
                        onValueChange = { executiveMembers = it; isSavedToast = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("settings_executive_members_input"),
                        label = { Text("पदाधिकारी नावे व पदे (स्वल्पविराम किंवा नवीन ओळ)") },
                        placeholder = { Text("सचिन सपकाळ (अध्यक्ष)\nअनिकेत सपकाळ (उपाध्यक्ष)\nसागर मोरे (खजिनदार)") },
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 सूचना: प्रत्येक ओळीवर एका पदाधिकाऱ्याचे नाव व पद टाका.",
                        fontSize = 10.5.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Section 3: App Lock & Security
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🔒 ॲप सुरक्षा पिन (App PIN Lock)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                            Text(
                                text = "हिशोब सुरक्षित ठेवण्यासाठी ४ अंकी पिन",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { isPinEnabled = it; isSavedToast = false },
                            colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary, checkedTrackColor = Color(0xFFFFE0B2))
                        )
                    }

                    if (isPinEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = securityPin,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    securityPin = it
                                    isSavedToast = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_pin_input"),
                            label = { Text("४-अंकी सुरक्षा पिन (Enter 4-digit PIN)") },
                            placeholder = { Text("उदा. 1234") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section 4: Data Backup & Restore
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💾 डेटा बॅकअप व रिस्टोअर (Backup & Restore)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronDark
                    )
                    Text(
                        text = "सर्व पावत्या, खर्च व हिशोब सुरक्षित सेव्ह करण्यासाठी संपूर्ण डेटा बॅकअप फाइल तयार करा.",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExportBackup,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_backup_json_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "बॅकअप घ्या (Export)", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onImportBackupDialog,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("import_backup_json_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "रिस्टोअर (Import)", fontSize = 12.sp)
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

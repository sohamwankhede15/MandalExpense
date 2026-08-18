package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
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

@Composable
fun AuditHistoryScreen(
    auditLogs: List<AuditLog>,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterType by remember { mutableStateOf("सर्व") }

    val filterOptions = listOf("सर्व", "VARGANI", "EXPENSE", "DAY_CLOSING", "RECONCILIATION", "BACKUP", "AUTH")

    val filteredLogs = remember(auditLogs, searchQuery, selectedFilterType) {
        auditLogs.filter { log ->
            val matchesType = when (selectedFilterType) {
                "सर्व" -> true
                else -> log.recordType.equals(selectedFilterType, ignoreCase = true)
            }

            val q = searchQuery.trim()
            val matchesQuery = if (q.isEmpty()) true else {
                log.action.contains(q, ignoreCase = true) ||
                        log.recordIdentifier.contains(q, ignoreCase = true) ||
                        log.details.contains(q, ignoreCase = true) ||
                        log.performedBy.contains(q, ignoreCase = true) ||
                        log.newValue.contains(q, ignoreCase = true)
            }

            matchesType && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangeBackground)
            .padding(16.dp)
            .testTag("audit_history_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = OrangeContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = OrangePrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ऑडिट इतिहास (Audit Trail)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "सर्व आर्थिक व्यवहारांची व बदलांची नोंद",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("close_audit_history")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("पावती क्र, खर्च, व्यक्तीचे नाव शोधा...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = OrangePrimary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = OrangeBorder,
                focusedContainerColor = WhiteCard,
                unfocusedContainerColor = WhiteCard
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("audit_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { filter ->
                val label = when (filter) {
                    "सर्व" -> "सर्व नोंदी"
                    "VARGANI" -> "📜 वर्गणी"
                    "EXPENSE" -> "💸 खर्च"
                    "DAY_CLOSING" -> "🔒 दिवस क्लोजिंग"
                    "RECONCILIATION" -> "💰 ताळमेळ"
                    "BACKUP" -> "💾 बॅकअप"
                    "AUTH" -> "🔐 लॉगिन"
                    else -> filter
                }
                val isSelected = selectedFilterType == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilterType = filter },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = WhiteCard,
                        labelColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, if (isSelected) OrangePrimary else OrangeBorder)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Count Summary
        Text(
            text = "${auditLogs.size} पैकी ${filteredLogs.size} ऑडिट नोंदी",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Log Items List
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, contentDescription = null, tint = OrangeBorder, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("कोणत्याही ऑडिट नोंदी सापडल्या नाहीत.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredLogs) { log ->
                    val isDeleteOrCancel = log.action.contains("रद्द") || log.action.contains("हटव")
                    val isClosing = log.recordType == "DAY_CLOSING"
                    val isEdit = log.action.contains("बदल") || log.action.contains("Edit")

                    val badgeColor = when {
                        isDeleteOrCancel -> RedContainer
                        isClosing -> Color(0xFFFFF3E0)
                        isEdit -> BlueContainer
                        else -> GreenContainer
                    }
                    val badgeTextColor = when {
                        isDeleteOrCancel -> RedExpense
                        isClosing -> OrangePrimaryDark
                        isEdit -> BlueInfo
                        else -> GreenSuccess
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("audit_log_item_${log.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, OrangeBorder.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = badgeColor
                                ) {
                                    Text(
                                        text = log.action,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = DateUtils.formatMarathiDateTime(log.timestamp),
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            if (log.recordIdentifier.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "नोंद क्र / ID: ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = log.recordIdentifier,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            if (log.oldValue.isNotBlank() || log.newValue.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = OrangeBackground,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        if (log.oldValue.isNotBlank()) {
                                            Text(
                                                text = "पूर्वी: ${log.oldValue}",
                                                fontSize = 11.sp,
                                                color = RedExpense,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        if (log.newValue.isNotBlank()) {
                                            Text(
                                                text = "नवीन: ${log.newValue}",
                                                fontSize = 11.sp,
                                                color = GreenSuccess,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            if (log.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "तपशील: ${log.details}",
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = OrangeBorder.copy(alpha = 0.4f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "कर्ता: ${log.performedBy}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OrangePrimaryDark
                                    )
                                }

                                Text(
                                    text = "प्रकार: ${log.recordType}",
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

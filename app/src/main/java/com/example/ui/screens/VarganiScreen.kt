package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MandalSettings
import com.example.data.model.OtherPersonType
import com.example.data.model.PersonType
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
import com.example.ui.theme.TextOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard
import com.example.ui.viewmodel.OwnerWiseRecord
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VarganiScreen(
    varganiList: List<VarganiTransaction>,
    ownerWiseRecords: List<OwnerWiseRecord>,
    allOwnerNames: List<String>,
    settings: MandalSettings,
    totalVarganiAmount: Double,
    totalOwnerVargani: Double,
    totalTenantVargani: Double,
    totalOtherVargani: Double = 0.0,
    ownerCount: Int,
    tenantCount: Int,
    otherCount: Int = 0,
    onAddNewPavti: () -> Unit,
    onSelectPavti: (VarganiTransaction) -> Unit,
    onQuickWhatsApp: (VarganiTransaction) -> Unit,
    onQuickPdf: (VarganiTransaction) -> Unit,
    onExportExcel: () -> Unit
) {
    // View Mode: 0 = "सर्व पावत्या" (List View), 1 = "मालकनिहाय हिशोब" (Owner-wise Group View)
    var selectedViewMode by remember { mutableIntStateOf(0) }

    // Search query
    var searchQuery by remember { mutableStateOf("") }

    // Filter by Person Type: "सर्व", "घरमालक", "भाडेकरू", "इतर"
    var selectedPersonTypeFilter by remember { mutableStateOf("सर्व") }

    // Filter by Specific Owner Name
    var selectedOwnerFilter by remember { mutableStateOf<String?>(null) }

    // Show Filter Panel
    var showFilterPanel by remember { mutableStateOf(false) }

    // Filtered Vargani List
    val filteredList = remember(
        varganiList,
        searchQuery,
        selectedPersonTypeFilter,
        selectedOwnerFilter
    ) {
        varganiList.filter { item ->
            val matchesType = when (selectedPersonTypeFilter) {
                "घरमालक", "मालक" -> item.isOwner
                "भाडेकरू" -> item.isTenant
                "इतर" -> item.isOther
                else -> true
            }

            val matchesOwner = if (selectedOwnerFilter != null) {
                if (item.isOwner) {
                    item.contributorName.equals(selectedOwnerFilter, ignoreCase = true)
                } else if (item.isTenant) {
                    item.ownerName.equals(selectedOwnerFilter, ignoreCase = true)
                } else {
                    false
                }
            } else {
                true
            }

            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isBlank() ||
                    item.contributorName.lowercase().contains(query) ||
                    item.ownerName.lowercase().contains(query) ||
                    item.pavtiNumber.lowercase().contains(query) ||
                    item.mobileNumber.contains(query) ||
                    item.address.lowercase().contains(query) ||
                    item.paymentMode.lowercase().contains(query) ||
                    item.otherPersonType.lowercase().contains(query) ||
                    item.customCategoryName.lowercase().contains(query)

            matchesType && matchesOwner && matchesSearch
        }
    }

    // Filtered Owner-wise Records
    val filteredOwnerRecords = remember(ownerWiseRecords, searchQuery, selectedOwnerFilter) {
        ownerWiseRecords.filter { rec ->
            val matchesOwner = selectedOwnerFilter == null || rec.ownerName.equals(selectedOwnerFilter, ignoreCase = true)
            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isBlank() ||
                    rec.ownerName.lowercase().contains(query) ||
                    rec.tenantTransactions.any { it.contributorName.lowercase().contains(query) }
            matchesOwner && matchesSearch
        }
    }

    val isFilterActive = selectedPersonTypeFilter != "सर्व" || selectedOwnerFilter != null || searchQuery.isNotBlank()

    Scaffold(
        containerColor = OrangeBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewPavti,
                containerColor = OrangePrimaryDark,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp),
                modifier = Modifier.testTag("add_new_pavti_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "पावती जोडा")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "नवीन पावती", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // View Mode Tab Bar (पावत्या यादी vs मालकनिहाय हिशोब)
            Surface(
                color = WhiteCard,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tab 0: सर्व पावत्या
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedViewMode = 0 },
                        color = if (selectedViewMode == 0) OrangeContainer else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (selectedViewMode == 0) OrangePrimaryDark else Color.Transparent)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = if (selectedViewMode == 0) OrangePrimaryDark else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "पावत्या यादी (${filteredList.size})",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedViewMode == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedViewMode == 0) OrangePrimaryDark else TextSecondary
                            )
                        }
                    }

                    // Tab 1: मालकनिहाय हिशोब
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedViewMode = 1 },
                        color = if (selectedViewMode == 1) OrangeContainer else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (selectedViewMode == 1) OrangePrimaryDark else Color.Transparent)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = if (selectedViewMode == 1) OrangePrimaryDark else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "मालकनिहाय गट (${filteredOwnerRecords.size})",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedViewMode == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedViewMode == 1) OrangePrimaryDark else TextSecondary
                            )
                        }
                    }
                }
            }

            // Summary Stats Pill Bar
            Surface(
                color = OrangeContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "एकूण: ${IndianCurrencyFormatter.formatRupees(totalVarganiAmount)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryDark
                    )
                    Text(
                        text = "🏠 घरमालक: ${IndianCurrencyFormatter.formatRupees(totalOwnerVargani)} ($ownerCount)",
                        fontSize = 10.5.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "🏢 भाडेकरू: ${IndianCurrencyFormatter.formatRupees(totalTenantVargani)} ($tenantCount)",
                        fontSize = 10.5.sp,
                        color = TextPrimary
                    )
                    if (otherCount > 0 || totalOtherVargani > 0) {
                        Text(
                            text = "⭐ इतर: ${IndianCurrencyFormatter.formatRupees(totalOtherVargani)} ($otherCount)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimaryDark
                        )
                    }
                }
            }

            // Search Bar & Filter Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("नाव, मालक, इतर प्रवर्ग, मोबाईल शोधा...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "शोधा", tint = OrangePrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimaryDark,
                        unfocusedBorderColor = OrangeBorder,
                        focusedContainerColor = WhiteCard,
                        unfocusedContainerColor = WhiteCard
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("vargani_search_input")
                )

                // Filter Button with Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isFilterActive) OrangePrimaryDark else WhiteCard,
                    border = BorderStroke(1.dp, if (isFilterActive) OrangePrimaryDark else OrangeBorder),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showFilterPanel = !showFilterPanel }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "फिल्टर",
                            tint = if (isFilterActive) Color.White else OrangePrimaryDark
                        )
                    }
                }
            }

            // Quick Type Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("सर्व", "घरमालक", "भाडेकरू", "इतर").forEach { type ->
                    val isSelected = selectedPersonTypeFilter == type
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) OrangePrimaryDark else WhiteCard,
                        border = BorderStroke(1.dp, if (isSelected) OrangePrimaryDark else OrangeBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedPersonTypeFilter = type }
                    ) {
                        Text(
                            text = when (type) {
                                "घरमालक" -> "🏠 घरमालक ($ownerCount)"
                                "भाडेकरू" -> "🏢 भाडेकरू ($tenantCount)"
                                "इतर" -> "⭐ इतर ($otherCount)"
                                else -> "सर्व (${varganiList.size})"
                            },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }

                // Clear Filter Button if active
                if (isFilterActive) {
                    Text(
                        text = "✕",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedPersonTypeFilter = "सर्व"
                                selectedOwnerFilter = null
                                searchQuery = ""
                            }
                            .padding(4.dp)
                    )
                }
            }

            // Expanded Filter Panel (for specific owner selection)
            AnimatedVisibility(
                visible = showFilterPanel,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = WhiteCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, OrangeBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "विशिष्ट मालकानुसार फिल्टर करा:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            IconButton(
                                onClick = { showFilterPanel = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (allOwnerNames.isEmpty()) {
                            Text("अद्याप कोणतीही नोंदणी नाही.", fontSize = 11.sp, color = TextSecondary)
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                allOwnerNames.forEach { owner ->
                                    val isSelected = selectedOwnerFilter == owner
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) OrangePrimaryDark else Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, if (isSelected) OrangePrimaryDark else Color(0xFFE2E8F0)),
                                        modifier = Modifier.clickable {
                                            selectedOwnerFilter = if (isSelected) null else owner
                                        }
                                    ) {
                                        Text(
                                            text = "🏠 $owner",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // MAIN CONTENT BASED ON TAB
            if (selectedViewMode == 0) {
                // TAB 0: INDIVIDUAL RECEIPT LIST
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = OrangePrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isFilterActive) "निवडलेल्या फिल्टरनुसार पावती आढळली नाही." else "अद्याप कोणतीही वर्गणी पावती नोंदवलेली नाही.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            if (isFilterActive) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        selectedPersonTypeFilter = "सर्व"
                                        selectedOwnerFilter = null
                                        searchQuery = ""
                                    }
                                ) {
                                    Text("सर्व दाखवा")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList, key = { it.id }) { item ->
                            VarganiItemCard(
                                vargani = item,
                                onClick = { onSelectPavti(item) },
                                onWhatsApp = { onQuickWhatsApp(item) },
                                onPdf = { onQuickPdf(item) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            } else {
                // TAB 1: OWNER-WISE GROUPED VIEW
                if (filteredOwnerRecords.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "कोणतेही मालकनिहाय रेकॉर्ड उपलब्ध नाहीत.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredOwnerRecords, key = { it.ownerName }) { record ->
                            OwnerWiseCard(
                                record = record,
                                onSelectPavti = onSelectPavti
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VarganiItemCard(
    vargani: VarganiTransaction,
    onClick: () -> Unit,
    onWhatsApp: () -> Unit,
    onPdf: () -> Unit
) {
    val isOwner = vargani.isOwner
    val isTenant = vargani.isTenant
    val isOther = vargani.isOther

    val badgeBgColor = when {
        isOwner -> OrangeContainer
        isTenant -> BlueContainer
        else -> Color(0xFFF3E8FF) // Lavender/Purple
    }
    val badgeTextColor = when {
        isOwner -> OrangePrimaryDark
        isTenant -> BlueInfo
        else -> Color(0xFF7E22CE) // PurpleDark
    }
    val badgeIcon = when {
        isOwner -> Icons.Default.Home
        isTenant -> Icons.Default.Apartment
        else -> Icons.Default.Star
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("vargani_item_${vargani.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (isOwner) OrangeBorder else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type Badge + Pavti No + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Person Type Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeBgColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = null,
                                tint = badgeTextColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = vargani.displayPersonType,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }

                    Text(
                        text = vargani.pavtiNumber,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                // Amount
                Text(
                    text = IndianCurrencyFormatter.formatRupees(vargani.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OrangePrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contributor Name
            Text(
                text = vargani.contributorName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // If Tenant, show Owner Name
            if (isTenant && vargani.ownerName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏠 घरमालक: ",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = vargani.ownerName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimaryDark
                    )
                }
            }

            // If Other with custom category, show category note
            if (isOther && vargani.customCategoryName.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "प्रवर्ग: ${vargani.customCategoryName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7E22CE)
                )
            }

            // Mobile / Address / Date Row
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (vargani.mobileNumber.isNotBlank()) "📱 ${vargani.mobileNumber}" else if (vargani.address.isNotBlank()) "📍 ${vargani.address}" else "पेमेंट: ${vargani.paymentMode}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Text(
                    text = DateUtils.formatNumericDate(vargani.timestamp),
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.7.dp, color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            // Quick Actions: WhatsApp & PDF Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick WhatsApp Share Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onWhatsApp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "WhatsApp",
                            tint = GreenSuccess,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "WhatsApp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Quick PDF Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangeContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onPdf)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = OrangePrimaryDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerWiseCard(
    record: OwnerWiseRecord,
    onSelectPavti: (VarganiTransaction) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Owner Name & Total Combined Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OrangeContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = OrangePrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = record.ownerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (record.ownerTransaction != null) "नोंदणीकृत मालक" else "भाडेकरू संदर्भ",
                            fontSize = 10.5.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Total Combined Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = IndianCurrencyFormatter.formatRupees(record.totalCombinedAmount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangePrimaryDark
                    )
                    Text(
                        text = "एकत्रित जमा",
                        fontSize = 9.5.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Breakdown: मालकाची वर्गणी vs भाडेकरूंची वर्गणी
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Owner Part
                Column {
                    Text(
                        text = "🏠 मालक वर्गणी:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = if (record.ownerAmount > 0) IndianCurrencyFormatter.formatRupees(record.ownerAmount) else "अद्याप नाही",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.ownerAmount > 0) OrangePrimaryDark else TextSecondary
                    )
                }

                // Tenant Part
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "🏢 भाडेकरू वर्गणी (${record.tenantTransactions.size}):",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = IndianCurrencyFormatter.formatRupees(record.tenantAmount),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueInfo
                    )
                }
            }

            // Expand / Collapse Tenants
            if (record.tenantTransactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "भाडेकरू यादी लपवा" else "${record.tenantTransactions.size} भाडेकरूंची यादी पहा",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimaryDark
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = OrangePrimaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        record.tenantTransactions.forEach { tenant ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WhiteCard,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectPavti(tenant) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = tenant.contributorName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${tenant.pavtiNumber} • ${tenant.paymentMode}",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = IndianCurrencyFormatter.formatRupees(tenant.amount),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BlueInfo
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

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FestivalEvent
import com.example.data.model.MandalSettings
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
import com.example.util.ShareHelper
import com.example.util.excel.ExcelReportGenerator
import com.example.util.pdf.PdfReceiptGenerator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScheduleScreen(
    viewModel: MandalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val eventList by viewModel.festivalEvents.collectAsState()

    var selectedDate by remember { mutableStateOf(settings.festivalStartDate.ifBlank { DateUtils.getTodayIsoDate() }) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<FestivalEvent?>(null) }
    var eventToDelete by remember { mutableStateOf<FestivalEvent?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("सर्व") }

    // Generate list of festival dates
    val festivalDays = remember(settings.festivalStartDate, settings.festivalEndDate) {
        generateFestivalDateList(settings.festivalStartDate, settings.festivalEndDate)
    }

    // Filtered events
    val filteredEvents = remember(eventList, selectedDate, searchQuery, selectedTypeFilter) {
        eventList.filter { event ->
            val matchDate = if (selectedDate.isNotBlank()) event.dateString == selectedDate else true
            val matchSearch = searchQuery.isBlank() ||
                event.eventName.contains(searchQuery, ignoreCase = true) ||
                event.responsibleMember.contains(searchQuery, ignoreCase = true) ||
                event.aartiContributorName.contains(searchQuery, ignoreCase = true) ||
                event.mahaprasadContributorName.contains(searchQuery, ignoreCase = true)
            val matchType = selectedTypeFilter == "सर्व" || event.programType == selectedTypeFilter
            matchDate && matchSearch && matchType
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OrangeBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Card
            item {
                FestivalHeaderCard(
                    settings = settings,
                    totalEvents = eventList.size,
                    onExportPdf = {
                        val file = PdfReceiptGenerator.generateEventSchedulePdf(context, eventList, settings)
                        if (file != null) {
                            ShareHelper.shareFile(context, file, "application/pdf", "गणेशोत्सव कार्यक्रम वेळापत्रक PDF")
                        } else {
                            Toast.makeText(context, "PDF तयार करताना त्रुटी आली", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onExportExcel = {
                        val file = ExcelReportGenerator.generateEventScheduleExcel(context, eventList, settings)
                        if (file != null) {
                            ShareHelper.shareFile(context, file, "text/csv", "गणेशोत्सव कार्यक्रम वेळापत्रक Excel")
                        } else {
                            Toast.makeText(context, "Excel तयार करताना त्रुटी आली", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Festival Days Selector (Day 1, Day 2, etc. horizontal chip strip)
            item {
                FestivalDaysStrip(
                    days = festivalDays,
                    selectedDate = selectedDate,
                    startDate = settings.festivalStartDate,
                    eventsByDate = eventList.groupBy { it.dateString },
                    onSelectDate = { selectedDate = it }
                )
            }

            // Search and Type Filter Bar
            item {
                SearchAndFilterBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedTypeFilter,
                    onFilterChange = { selectedTypeFilter = it }
                )
            }

            // Selected Date Summary Bar
            item {
                SelectedDateSummaryHeader(
                    selectedDate = selectedDate,
                    startDate = settings.festivalStartDate,
                    dayEventsCount = filteredEvents.size,
                    onAddEvent = {
                        eventToEdit = null
                        showAddEditDialog = true
                    }
                )
            }

            // Events List or Empty State
            if (filteredEvents.isEmpty()) {
                item {
                    EmptyEventState(
                        selectedDate = selectedDate,
                        onAddEvent = {
                            eventToEdit = null
                            showAddEditDialog = true
                        },
                        onLoadDefaultSchedule = {
                            loadSampleGanpatiSchedule(selectedDate, settings, viewModel) {
                                Toast.makeText(context, "कार्यक्रमाचे नमुने जोडले गेले!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            } else {
                items(filteredEvents, key = { it.id }) { event ->
                    FestivalEventCard(
                        event = event,
                        onEdit = {
                            eventToEdit = event
                            showAddEditDialog = true
                        },
                        onDelete = {
                            eventToDelete = event
                        }
                    )
                }
            }
        }

        // Floating Action Button to Add Event
        FloatingActionButton(
            onClick = {
                eventToEdit = null
                showAddEditDialog = true
            },
            containerColor = OrangePrimaryDark,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_event_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "कार्यक्रम जोडा")
                Spacer(modifier = Modifier.width(6.dp))
                Text("कार्यक्रम जोडा", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditEventDialog(
            initialEvent = eventToEdit,
            defaultDate = selectedDate,
            settings = settings,
            onDismiss = { showAddEditDialog = false },
            onSave = { event ->
                if (eventToEdit == null) {
                    viewModel.addFestivalEvent(
                        event = event,
                        onSuccess = {
                            showAddEditDialog = false
                            Toast.makeText(context, "कार्यक्रम यशस्वीरीत्या जोडला!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    viewModel.updateFestivalEvent(
                        event = event,
                        onSuccess = {
                            showAddEditDialog = false
                            Toast.makeText(context, "कार्यक्रम यशस्वीरीत्या अपडेट केला!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = {
                Text("कार्यक्रम हटवायचा आहे का?", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            },
            text = {
                Text("\"${eventToDelete?.eventName}\" (${eventToDelete?.eventTime}) हा कार्यक्रम कायमचा हटवला जाईल.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toDel = eventToDelete ?: return@Button
                        viewModel.deleteFestivalEvent(
                            event = toDel,
                            onSuccess = {
                                eventToDelete = null
                                Toast.makeText(context, "कार्यक्रम हटवला गेला.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("हटवा (Delete)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("रद्द करा")
                }
            }
        )
    }
}

@Composable
private fun FestivalHeaderCard(
    settings: MandalSettings,
    totalEvents: Int,
    onExportPdf: () -> Unit,
    onExportExcel: () -> Unit
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = OrangePrimaryDark
                        ) {
                            Text(
                                text = "उत्सव नियोजन",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "एकूण $totalEvents कार्यक्रम",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = settings.festivalName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "कालावधी: ${DateUtils.formatToShortMarathiDate(settings.festivalStartDate)} ते ${DateUtils.formatToMarathiDisplayDate(settings.festivalEndDate)}",
                        fontSize = 12.sp,
                        color = OrangePrimaryDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = OrangeBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (PDF / Excel Export)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExportPdf,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, OrangePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimaryDark),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("वेळापत्रक PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onExportExcel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("वेळापत्रक Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FestivalDaysStrip(
    days: List<String>,
    selectedDate: String,
    startDate: String,
    eventsByDate: Map<String, List<FestivalEvent>>,
    onSelectDate: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "उत्सवाचे दिवस निवडा:",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { dateStr ->
                val isSelected = dateStr == selectedDate
                val dayIdx = DateUtils.getFestivalDayIndex(dateStr, startDate)
                val count = eventsByDate[dateStr]?.size ?: 0

                Surface(
                    onClick = { onSelectDate(dateStr) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) OrangePrimaryDark else WhiteCard,
                    border = BorderStroke(1.dp, if (isSelected) OrangePrimaryDark else OrangeBorder),
                    tonalElevation = if (isSelected) 4.dp else 1.dp,
                    modifier = Modifier.testTag("festival_day_chip_$dayIdx")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "दिवस $dayIdx",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else OrangePrimaryDark
                        )
                        Text(
                            text = DateUtils.formatToShortMarathiDate(dateStr),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                        if (count > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color.White.copy(alpha = 0.3f) else OrangeContainer
                            ) {
                                Text(
                                    text = "$count कार्यक्रम",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else OrangePrimaryDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filterTypes = listOf("सर्व", "आरती", "महाप्रसाद", "गणपती स्थापना", "सांस्कृतिक कार्यक्रम", "नृत्य स्पर्धा", "भजन", "विसर्जन")

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("कार्यक्रमाचे नाव, जबाबदार सदस्य, आरती यजमान शोधा...", fontSize = 12.5.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimaryDark) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = OrangeBorder,
                focusedBorderColor = OrangePrimaryDark,
                unfocusedContainerColor = WhiteCard,
                focusedContainerColor = WhiteCard
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filterTypes) { type ->
                val isSelected = selectedFilter == type
                Surface(
                    onClick = { onFilterChange(type) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) OrangePrimaryDark else OrangeContainer,
                    border = BorderStroke(0.5.dp, if (isSelected) OrangePrimaryDark else OrangeBorder)
                ) {
                    Text(
                        text = type,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else OrangePrimaryDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedDateSummaryHeader(
    selectedDate: String,
    startDate: String,
    dayEventsCount: Int,
    onAddEvent: () -> Unit
) {
    val dayIdx = DateUtils.getFestivalDayIndex(selectedDate, startDate)
    val displayDate = DateUtils.formatToMarathiDisplayDate(selectedDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "दिवस $dayIdx : $displayDate",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "$dayEventsCount नियोजित कार्यक्रम",
                fontSize = 11.5.sp,
                color = OrangePrimaryDark,
                fontWeight = FontWeight.Medium
            )
        }

        TextButton(onClick = onAddEvent) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = OrangePrimaryDark)
            Spacer(modifier = Modifier.width(4.dp))
            Text("+ कार्यक्रम", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangePrimaryDark)
        }
    }
}

@Composable
private fun FestivalEventCard(
    event: FestivalEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (event.status) {
        "पूर्ण" -> Color(0xFF2E7D32)
        "तयारी सुरू" -> Color(0xFFE65100)
        "रद्द" -> Color(0xFFDC2626)
        else -> Color(0xFF1565C0) // नियोजित
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Time, Event Name, Status & Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OrangeContainer
                    ) {
                        Text(
                            text = event.eventTime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = event.eventName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        val prgType = if (event.programType == "इतर" && event.customProgramType.isNotBlank()) event.customProgramType else event.programType
                        Text(
                            text = prgType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrangePrimaryDark
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = event.status,
                            color = statusColor,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "संपादित करा", tint = OrangePrimaryDark, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "हटवा", tint = Color(0xFFDC2626), modifier = Modifier.size(17.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OrangeBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            // Details Grid
            if (event.responsibleMember.isNotBlank()) {
                EventDetailRow(
                    icon = Icons.Default.Person,
                    label = "जबाबदार सदस्य:",
                    value = event.responsibleMember + if (event.responsibleMobile.isNotBlank()) " (${event.responsibleMobile})" else ""
                )
            }

            if (event.aartiContributorName.isNotBlank()) {
                EventDetailRow(
                    icon = Icons.Default.WbSunny,
                    label = "आरतीचे यजमान:",
                    value = "${event.aartiContributorName} (${event.aartiContributorType})"
                )
            }

            if (event.mahaprasadContributorName.isNotBlank()) {
                EventDetailRow(
                    icon = Icons.Default.Restaurant,
                    label = "महाप्रसाद:",
                    value = "${event.mahaprasadContributorName} (${event.mahaprasadContributorType})"
                )
            }

            if (event.flowerArrangementType.isNotBlank() && event.flowerArrangementType != "लागू नाही") {
                EventDetailRow(
                    icon = Icons.Default.LocalFlorist,
                    label = "हार व फुले:",
                    value = event.flowerArrangementType + if (event.flowerContributorName.isNotBlank()) " (${event.flowerContributorName})" else ""
                )
            }

            if (event.location.isNotBlank()) {
                EventDetailRow(
                    icon = Icons.Default.LocationOn,
                    label = "ठिकाण:",
                    value = event.location
                )
            }

            if (event.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OrangeContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "टीप: ${event.notes}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = OrangePrimaryDark, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
private fun EmptyEventState(
    selectedDate: String,
    onAddEvent: () -> Unit,
    onLoadDefaultSchedule: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OrangeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "या दिवसासाठी कार्यक्रम जोडलेला नाही",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "आरती, महाप्रसाद, सांस्कृतिक कार्यक्रम इत्यादींची नोंद करा.",
                fontSize = 11.5.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddEvent,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ कार्यक्रम जोडा")
                }
                OutlinedButton(
                    onClick = onLoadDefaultSchedule,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, OrangePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimaryDark)
                ) {
                    Text("नमुना वेळापत्रक जोडा")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditEventDialog(
    initialEvent: FestivalEvent?,
    defaultDate: String,
    settings: MandalSettings,
    onDismiss: () -> Unit,
    onSave: (FestivalEvent) -> Unit
) {
    var dateString by remember { mutableStateOf(initialEvent?.dateString ?: defaultDate) }
    var eventName by remember { mutableStateOf(initialEvent?.eventName ?: "") }
    var eventTime by remember { mutableStateOf(initialEvent?.eventTime ?: "07:30 PM") }
    var programType by remember { mutableStateOf(initialEvent?.programType ?: "आरती") }
    var customProgramType by remember { mutableStateOf(initialEvent?.customProgramType ?: "") }
    var location by remember { mutableStateOf(initialEvent?.location ?: "मुख्य मंडप") }
    var responsibleMember by remember { mutableStateOf(initialEvent?.responsibleMember ?: "") }
    var responsibleMobile by remember { mutableStateOf(initialEvent?.responsibleMobile ?: "") }
    var aartiContributorName by remember { mutableStateOf(initialEvent?.aartiContributorName ?: "") }
    var aartiContributorType by remember { mutableStateOf(initialEvent?.aartiContributorType ?: "सदस्य") }
    var mahaprasadContributorName by remember { mutableStateOf(initialEvent?.mahaprasadContributorName ?: "") }
    var mahaprasadContributorType by remember { mutableStateOf(initialEvent?.mahaprasadContributorType ?: "सदस्य") }
    var flowerArrangementType by remember { mutableStateOf(initialEvent?.flowerArrangementType ?: "मंडळाचा खर्च") }
    var flowerContributorName by remember { mutableStateOf(initialEvent?.flowerContributorName ?: "") }
    var status by remember { mutableStateOf(initialEvent?.status ?: "नियोजित") }
    var notes by remember { mutableStateOf(initialEvent?.notes ?: "") }

    val programTypes = listOf("गणपती स्थापना", "आरती", "महाप्रसाद", "नृत्य स्पर्धा", "सांस्कृतिक कार्यक्रम", "भजन", "कीर्तन", "सत्यनारायण पूजा", "विसर्जन", "इतर")
    val contributorTypes = listOf("सदस्य", "घरमालक", "भाडेकरू", "नगरसेवक", "आमदार", "व्यावसायिक / व्यापारी", "राजकीय व्यक्ती", "देणगीदार", "प्रायोजक", "इतर")
    val flowerTypes = listOf("मंडळाचा खर्च", "सदस्याकडून", "घरमालकाकडून", "भाडेकरूकडून", "नगरसेवकाकडून", "आमदाराकडून", "व्यावसायिकाकडून", "देणगीदाराकडून", "प्रायोजकाकडून", "लागू नाही")
    val statusOptions = listOf("नियोजित", "तयारी सुरू", "पूर्ण", "रद्द")

    // Parse member names from settings
    val memberList = remember(settings.executiveMembers) {
        settings.executiveMembers.lines().mapNotNull { line ->
            val cleaned = line.trim()
            if (cleaned.isNotBlank()) cleaned else null
        }
    }

    var expandedPrgType by remember { mutableStateOf(false) }
    var expandedMember by remember { mutableStateOf(false) }
    var expandedAartiType by remember { mutableStateOf(false) }
    var expandedMahaprasadType by remember { mutableStateOf(false) }
    var expandedFlowerType by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialEvent == null) "नवीन कार्यक्रम जोडा" else "कार्यक्रम संपादित करा",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = OrangePrimaryDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date & Time Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("तारीख (YYYY-MM-DD)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = { eventTime = it },
                        label = { Text("वेळ (उदा. 07:30 PM)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Event Name
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("कार्यक्रमाचे नाव *", fontSize = 12.sp) },
                    placeholder = { Text("उदा. संध्याकाळची महाआरती / नृत्य स्पर्धा") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Program Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedPrgType,
                    onExpandedChange = { expandedPrgType = !expandedPrgType }
                ) {
                    OutlinedTextField(
                        value = programType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("कार्यक्रमाचा प्रकार", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrgType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPrgType,
                        onDismissRequest = { expandedPrgType = false }
                    ) {
                        programTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    programType = type
                                    expandedPrgType = false
                                }
                            )
                        }
                    }
                }

                if (programType == "इतर") {
                    OutlinedTextField(
                        value = customProgramType,
                        onValueChange = { customProgramType = it },
                        label = { Text("इतर कार्यक्रमाचा प्रकार लिहा", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("ठिकाण", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Responsible Member Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMember,
                    onExpandedChange = { expandedMember = !expandedMember }
                ) {
                    OutlinedTextField(
                        value = responsibleMember,
                        onValueChange = { responsibleMember = it },
                        label = { Text("जबाबदार सदस्य (१५ कार्यकारिणीतून)", fontSize = 12.sp) },
                        placeholder = { Text("सदस्य निवडा किंवा नाव लिहा") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMember) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMember,
                        onDismissRequest = { expandedMember = false }
                    ) {
                        memberList.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member, fontSize = 13.sp) },
                                onClick = {
                                    responsibleMember = member
                                    expandedMember = false
                                }
                            )
                        }
                    }
                }

                // Responsible Mobile
                OutlinedTextField(
                    value = responsibleMobile,
                    onValueChange = { responsibleMobile = it },
                    label = { Text("जबाबदार सदस्याचा मोबाईल", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                HorizontalDivider(color = OrangeBorder)

                // Aarti Section
                Text("🙏 आरती यजमान / योगदानकर्ता:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangePrimaryDark)

                OutlinedTextField(
                    value = aartiContributorName,
                    onValueChange = { aartiContributorName = it },
                    label = { Text("आरतीचे यजमान (नाव)", fontSize = 12.sp) },
                    placeholder = { Text("उदा. श्री. राहुल कदम / नगरसेवक...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedAartiType,
                    onExpandedChange = { expandedAartiType = !expandedAartiType }
                ) {
                    OutlinedTextField(
                        value = aartiContributorType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("यजमान प्रवर्ग", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAartiType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAartiType,
                        onDismissRequest = { expandedAartiType = false }
                    ) {
                        contributorTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    aartiContributorType = type
                                    expandedAartiType = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = OrangeBorder)

                // Mahaprasad Section
                Text("🍚 महाप्रसाद तपशील:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangePrimaryDark)

                OutlinedTextField(
                    value = mahaprasadContributorName,
                    onValueChange = { mahaprasadContributorName = it },
                    label = { Text("महाप्रसाद कोणाकडून (नाव / प्रायोजक)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedMahaprasadType,
                    onExpandedChange = { expandedMahaprasadType = !expandedMahaprasadType }
                ) {
                    OutlinedTextField(
                        value = mahaprasadContributorType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("महाप्रसाद प्रवर्ग", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMahaprasadType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMahaprasadType,
                        onDismissRequest = { expandedMahaprasadType = false }
                    ) {
                        contributorTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    mahaprasadContributorType = type
                                    expandedMahaprasadType = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = OrangeBorder)

                // Flower Arrangements
                Text("🌸 हार व फुलांची व्यवस्था:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangePrimaryDark)

                ExposedDropdownMenuBox(
                    expanded = expandedFlowerType,
                    onExpandedChange = { expandedFlowerType = !expandedFlowerType }
                ) {
                    OutlinedTextField(
                        value = flowerArrangementType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("फुलांची व्यवस्था कोणाकडून", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFlowerType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFlowerType,
                        onDismissRequest = { expandedFlowerType = false }
                    ) {
                        flowerTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    flowerArrangementType = type
                                    expandedFlowerType = false
                                }
                            )
                        }
                    }
                }

                if (flowerArrangementType != "मंडळाचा खर्च" && flowerArrangementType != "लागू नाही") {
                    OutlinedTextField(
                        value = flowerContributorName,
                        onValueChange = { flowerContributorName = it },
                        label = { Text("हार/फुले देणाऱ्याचे नाव", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                HorizontalDivider(color = OrangeBorder)

                // Status
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("कार्यक्रमाची स्थिती", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statusOptions.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    status = st
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("विशेष सूचना / टीप", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val event = FestivalEvent(
                        id = initialEvent?.id ?: 0,
                        dateString = dateString.trim(),
                        eventName = eventName.trim(),
                        eventTime = eventTime.trim(),
                        programType = programType,
                        customProgramType = customProgramType.trim(),
                        location = location.trim(),
                        responsibleMember = responsibleMember.trim(),
                        responsibleMobile = responsibleMobile.trim(),
                        aartiContributorName = aartiContributorName.trim(),
                        aartiContributorType = aartiContributorType,
                        mahaprasadContributorName = mahaprasadContributorName.trim(),
                        mahaprasadContributorType = mahaprasadContributorType,
                        flowerArrangementType = flowerArrangementType,
                        flowerContributorName = flowerContributorName.trim(),
                        status = status,
                        notes = notes.trim()
                    )
                    onSave(event)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark)
            ) {
                Text(if (initialEvent == null) "जोडा (Save)" else "अपडेट करा (Update)", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करा")
            }
        }
    )
}

private fun generateFestivalDateList(startDateIso: String, endDateIso: String): List<String> {
    val result = mutableListOf<String>()
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val start = sdf.parse(startDateIso) ?: return listOf(startDateIso)
        val end = sdf.parse(endDateIso) ?: return listOf(startDateIso)

        val cal = Calendar.getInstance()
        cal.time = start

        while (!cal.time.after(end)) {
            result.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
    } catch (_: Exception) {
        result.add(startDateIso)
    }
    return if (result.isEmpty()) listOf(startDateIso) else result
}

private fun loadSampleGanpatiSchedule(
    currentDate: String,
    settings: MandalSettings,
    viewModel: MandalViewModel,
    onDone: () -> Unit
) {
    val sampleEvents = listOf(
        FestivalEvent(
            dateString = currentDate,
            eventName = "श्री गणपती बाप्पा आगमन व प्राणप्रतिष्ठापना",
            eventTime = "10:30 AM",
            programType = "गणपती स्थापना",
            location = "मुख्य मंडप",
            responsibleMember = "सचिन सपकाळ (अध्यक्ष)",
            responsibleMobile = "9876543210",
            aartiContributorName = "सचिन सपकाळ व सर्व कार्यकारिणी",
            aartiContributorType = "सदस्य",
            flowerArrangementType = "मंडळाचा खर्च",
            status = "नियोजित",
            notes = "भव्य आगमन मिरवणूक व विधिवत पूजा"
        ),
        FestivalEvent(
            dateString = currentDate,
            eventName = "संध्याकाळची महाआरती",
            eventTime = "07:30 PM",
            programType = "आरती",
            location = "मुख्य मंडप",
            responsibleMember = "सागर शितोळे (खजिनदार)",
            aartiContributorName = "गणेशनगर स्थानिक नागरिक व घरमालक",
            aartiContributorType = "घरमालक",
            flowerArrangementType = "सदस्याकडून",
            flowerContributorName = "रोहन शिंदे",
            status = "नियोजित"
        ),
        FestivalEvent(
            dateString = currentDate,
            eventName = "भव्य महाप्रसाद वाटप",
            eventTime = "08:30 PM",
            programType = "महाप्रसाद",
            location = "मंडप परिसर",
            responsibleMember = "दीपक पाटील (महाप्रसाद प्रमुख)",
            mahaprasadContributorName = "स्थानिक व्यावसायिक व मित्र मंडळ",
            mahaprasadContributorType = "व्यावसायिक / व्यापारी",
            status = "नियोजित"
        )
    )

    sampleEvents.forEach { event ->
        viewModel.addFestivalEvent(event)
    }
    onDone()
}

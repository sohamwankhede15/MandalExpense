package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AuditLog
import com.example.data.model.BackupData
import com.example.data.model.CashReconciliation
import com.example.data.model.ChatMessage
import com.example.data.model.DailyClosing
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.IncomeTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.PendingVargani
import com.example.data.model.VarganiTransaction
import com.example.data.repository.MandalRepository
import com.example.util.DateUtils
import com.example.util.backup.BackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Data class for Owner-Wise View
data class OwnerWiseRecord(
    val ownerName: String,
    val ownerTransaction: VarganiTransaction?,
    val ownerAmount: Double,
    val tenantTransactions: List<VarganiTransaction>,
    val tenantAmount: Double,
    val totalCombinedAmount: Double
)

class MandalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MandalRepository
    val database: AppDatabase

    init {
        database = AppDatabase.getDatabase(application)
        repository = MandalRepository(database.mandalDao())
    }

    // --- Authentication (Offline) ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow("Akhil")
    val currentUser: StateFlow<String> = _currentUser.asStateFlow()

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }

    fun login(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val trimmedUser = username.trim()
        val trimmedPass = password.trim()
        if (trimmedUser.isEmpty()) {
            onError("कृपया वापरकर्ता नाव टाका.")
            return
        }
        if (trimmedPass.isEmpty()) {
            onError("कृपया पासवर्ड टाका.")
            return
        }

        if (trimmedUser == "akhilganeshnagar@gmail.com" && trimmedPass == "411060") {
            _isLoggedIn.value = true
            _currentUser.value = "Akhil"
            logAudit(
                action = "लॉगिन केले",
                recordType = "AUTH",
                recordIdentifier = "akhilganeshnagar@gmail.com",
                details = "वापरकर्त्याने यशस्वी लॉगिन केले"
            )
            onSuccess()
        } else {
            onError("वापरकर्ता नाव किंवा पासवर्ड चुकीचा आहे.")
        }
    }

    fun logout() {
        logAudit(
            action = "लॉगआउट केले",
            recordType = "AUTH",
            recordIdentifier = _currentUser.value,
            details = "वापरकर्त्याने लॉगआउट केले"
        )
        _isLoggedIn.value = false
    }

    // --- Streams from Database ---
    val varganiList: StateFlow<List<VarganiTransaction>> = repository.allVargani
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseList: StateFlow<List<ExpenseTransaction>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeList: StateFlow<List<IncomeTransaction>> = repository.allIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingList: StateFlow<List<PendingVargani>> = repository.allPendingVargani
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyClosings: StateFlow<List<DailyClosing>> = repository.allDailyClosings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reconciliations: StateFlow<List<CashReconciliation>> = repository.allCashReconciliations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cashReconciliations: StateFlow<List<CashReconciliation>> = reconciliations

    val festivalEvents: StateFlow<List<FestivalEvent>> = repository.allFestivalEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<MandalSettings> = repository.settings
        .combine(MutableStateFlow(Unit)) { s, _ -> s ?: MandalSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MandalSettings())

    // Financial Totals (excluding cancelled)
    val totalVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOwnerVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOwner }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalTenantVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isTenant }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOtherVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Subcategory breakdowns for "इतर"
    val totalBusinessVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.BUSINESS }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCorporatorVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.CORPORATOR }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMlaVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.MLA }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPoliticalVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.POLITICAL_PERSON }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDonorVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.DONOR }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSponsorVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.SPONSOR }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOtherCustomVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isOther && com.example.data.model.OtherPersonType.fromCodeOrLabel(it.otherPersonType) == com.example.data.model.OtherPersonType.OTHER }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && !it.isFree }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOtherIncome: StateFlow<Double> = repository.totalIncomeAmount
        .combine(MutableStateFlow(0.0)) { amt, _ -> amt ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Total Income = Vargani + Other Income
    val totalIncomeCombined: StateFlow<Double> = combine(totalVargani, totalOtherIncome) { vargani, income ->
        vargani + income
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Balance = (Total Vargani + Other Income) - Total Expense
    val netBalance: StateFlow<Double> = combine(totalIncomeCombined, totalExpense) { income, expense ->
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Cash vs UPI Totals (Active records, excluding Free)
    val totalCashVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && (it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true)) }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalUpiVargani: StateFlow<Double> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && (it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true)) }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCashExpense: StateFlow<Double> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && !it.isFree && (it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true)) }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalUpiExpense: StateFlow<Double> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && !it.isFree && (it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true)) }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netCashInHand: StateFlow<Double> = combine(totalCashVargani, totalCashExpense) { vCash, eCash ->
        vCash - eCash
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netUpiInBank: StateFlow<Double> = combine(totalUpiVargani, totalUpiExpense) { vUpi, eUpi ->
        vUpi - eUpi
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Advanced Expense Categories StateFlows
    val advanceExpenseList: StateFlow<List<ExpenseTransaction>> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.expenseType == "ADVANCE" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unsettledAdvancesList: StateFlow<List<ExpenseTransaction>> = advanceExpenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isSettled }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAdvancePaid: StateFlow<Double> = advanceExpenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalUnsettledAdvance: StateFlow<Double> = unsettledAdvancesList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val freeExpenseList: StateFlow<List<ExpenseTransaction>> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && it.isFree }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFreeContribution: StateFlow<Double> = freeExpenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val mahaprasadExpenseList: StateFlow<List<ExpenseTransaction>> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && (it.isMahaprasad || it.category == "महाप्रसाद व भोजन") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMahaprasadExpense: StateFlow<Double> = mahaprasadExpenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isFree }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMahaprasadFreeValue: StateFlow<Double> = mahaprasadExpenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.isFree }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val executiveMembersList: StateFlow<List<String>> = settings.combine(MutableStateFlow(Unit)) { s, _ ->
        s.executiveMembers.lines().map { it.trim() }.filter { it.isNotBlank() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Metrics
    val todayVarganiList: StateFlow<List<VarganiTransaction>> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && DateUtils.isToday(it.timestamp) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayExpenseList: StateFlow<List<ExpenseTransaction>> = expenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { !it.isCancelled && !it.isFree && DateUtils.isToday(it.timestamp) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotalVargani: StateFlow<Double> = todayVarganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val todayVargani: StateFlow<Double> = todayTotalVargani

    val todayCashVargani: StateFlow<Double> = todayVarganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayUpiVargani: StateFlow<Double> = todayVarganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayTotalExpense: StateFlow<Double> = todayExpenseList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val todayExpenses: StateFlow<Double> = todayTotalExpense

    val todayNetBalance: StateFlow<Double> = combine(todayTotalVargani, todayTotalExpense) { v, e ->
        v - e
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayPavtiCount: StateFlow<Int> = todayVarganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Owner Count, Tenant Count, Other Count (Active only)
    val totalOwnersCount: StateFlow<Int> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { !it.isCancelled && it.isOwner }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTenantsCount: StateFlow<Int> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { !it.isCancelled && it.isTenant }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalOthersCount: StateFlow<Int> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { !it.isCancelled && it.isOther }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPavtiCount: StateFlow<Int> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { !it.isCancelled }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // List of all known owner names (from owner transactions + tenant ownerName references)
    val allOwnerNames: StateFlow<List<String>> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        val fromOwners = list.filter { !it.isCancelled && it.isOwner }.map { it.contributorName.trim() }
        val fromTenants = list.filter { !it.isCancelled && it.isTenant && it.ownerName.isNotBlank() }.map { it.ownerName.trim() }
        (fromOwners + fromTenants).filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Owner-wise grouping records
    val ownerWiseRecords: StateFlow<List<OwnerWiseRecord>> = varganiList.combine(MutableStateFlow(Unit)) { list, _ ->
        val ownersMap = mutableMapOf<String, OwnerWiseRecord>()
        val activeList = list.filter { !it.isCancelled }

        // 1. Process all Owner transactions
        activeList.filter { it.isOwner }.forEach { ownerTx ->
            val name = ownerTx.contributorName.trim()
            val existing = ownersMap[name]
            if (existing != null) {
                ownersMap[name] = existing.copy(
                    ownerTransaction = ownerTx,
                    ownerAmount = existing.ownerAmount + ownerTx.amount,
                    totalCombinedAmount = existing.totalCombinedAmount + ownerTx.amount
                )
            } else {
                ownersMap[name] = OwnerWiseRecord(
                    ownerName = name,
                    ownerTransaction = ownerTx,
                    ownerAmount = ownerTx.amount,
                    tenantTransactions = emptyList(),
                    tenantAmount = 0.0,
                    totalCombinedAmount = ownerTx.amount
                )
            }
        }

        // 2. Process all Tenant transactions
        activeList.filter { it.isTenant }.forEach { tenantTx ->
            val ownerName = if (tenantTx.ownerName.isNotBlank()) tenantTx.ownerName.trim() else "अनिर्दिष्ट मालक"
            val existing = ownersMap[ownerName]
            if (existing != null) {
                val newTenants = existing.tenantTransactions + tenantTx
                val newTenantAmt = existing.tenantAmount + tenantTx.amount
                ownersMap[ownerName] = existing.copy(
                    tenantTransactions = newTenants,
                    tenantAmount = newTenantAmt,
                    totalCombinedAmount = existing.ownerAmount + newTenantAmt
                )
            } else {
                ownersMap[ownerName] = OwnerWiseRecord(
                    ownerName = ownerName,
                    ownerTransaction = null,
                    ownerAmount = 0.0,
                    tenantTransactions = listOf(tenantTx),
                    tenantAmount = tenantTx.amount,
                    totalCombinedAmount = tenantTx.amount
                )
            }
        }

        ownersMap.values.sortedByDescending { it.totalCombinedAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "नमस्कार! मी AKGMM चा सहाय्यक आहे. मी तुम्हाला मंडळ हिशोब, मालक/भाडेकरू वर्गणी व अहवाल पाहण्यात मदत करू शकेन.",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // --- Audit Logging Helper ---
    fun logAudit(
        action: String,
        recordType: String = "",
        recordIdentifier: String = "",
        oldValue: String = "",
        newValue: String = "",
        details: String = "",
        user: String = _currentUser.value
    ) {
        viewModelScope.launch {
            try {
                val log = AuditLog(
                    action = action,
                    recordType = recordType,
                    recordIdentifier = recordIdentifier,
                    oldValue = oldValue,
                    newValue = newValue,
                    performedBy = user,
                    timestamp = System.currentTimeMillis(),
                    details = details
                )
                repository.insertAuditLog(log)
            } catch (_: Exception) {}
        }
    }

    // --- Daily Closing Checks & Actions ---
    fun isDateClosed(timestamp: Long): Boolean {
        val iso = DateUtils.formatIsoDate(timestamp)
        return dailyClosings.value.any { it.dateString == iso && it.isClosed }
    }

    fun isDateStringClosed(dateString: String): Boolean {
        return dailyClosings.value.any { it.dateString == dateString && it.isClosed }
    }

    fun closeDay(
        dateString: String,
        notes: String = "",
        closedBy: String = _currentUser.value,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (isDateStringClosed(dateString)) {
                    onError("हा दिवस आधीच बंद करण्यात आला आहे.")
                    return@launch
                }

                // Compute day metrics
                val dayVargani = varganiList.value.filter { !it.isCancelled && DateUtils.formatIsoDate(it.timestamp) == dateString }
                val dayExpenses = expenseList.value.filter { !it.isCancelled && !it.isFree && DateUtils.formatIsoDate(it.timestamp) == dateString }

                val totalInc = dayVargani.sumOf { it.amount }
                val totalExp = dayExpenses.sumOf { it.amount }
                val cashTot = dayVargani.filter { it.paymentMode == "रोख" || it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
                val upiTot = dayVargani.filter { it.paymentMode != "रोख" && !it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
                val closingBal = totalInc - totalExp

                val closing = DailyClosing(
                    dateString = dateString,
                    closingTimestamp = System.currentTimeMillis(),
                    totalIncome = totalInc,
                    totalExpenses = totalExp,
                    cashTotal = cashTot,
                    upiTotal = upiTot,
                    closingBalance = closingBal,
                    totalPavtisCount = dayVargani.size,
                    totalExpensesCount = dayExpenses.size,
                    closedBy = closedBy,
                    isClosed = true,
                    notes = notes
                )

                repository.insertDailyClosing(closing)
                logAudit(
                    action = "दिवसाचा हिशोब बंद केला",
                    recordType = "DAY_CLOSING",
                    recordIdentifier = dateString,
                    newValue = "जमा: ₹${totalInc.toLong()}, खर्च: ₹${totalExp.toLong()}, शिल्लक: ₹${closingBal.toLong()}",
                    details = "तारीख: $dateString, बंद करणारा: $closedBy",
                    user = closedBy
                )
                onSuccess()
            } catch (e: Exception) {
                onError("दिवस बंद करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun performDailyClosing(
        dateStr: String,
        income: Double,
        expenses: Double,
        closingBal: Double,
        closedBy: String,
        notes: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        closeDay(dateString = dateStr, notes = notes, closedBy = closedBy, onSuccess = onSuccess, onError = onError)
    }

    fun reopenDay(
        dateString: String,
        reason: String = "",
        reopenedBy: String = _currentUser.value,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.deleteDailyClosingByDate(dateString)
                val reasonText = if (reason.isNotBlank()) "कारण: $reason" else "वापरकर्त्याच्या विनंतीनुसार"
                logAudit(
                    action = "दिवस अनलॉक केला (पुन्हा उघडला)",
                    recordType = "DAY_CLOSING",
                    recordIdentifier = dateString,
                    details = "अनलॉक करणारा: $reopenedBy, $reasonText",
                    user = reopenedBy
                )
                onSuccess()
            } catch (e: Exception) {
                onError("दिवस उघडताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    // --- Festival Event Actions ---
    fun addFestivalEvent(
        event: FestivalEvent,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (event.eventName.isBlank()) {
                    onError("कृपया कार्यक्रमाचे नाव प्रविष्ट करा.")
                    return@launch
                }
                repository.insertFestivalEvent(event)
                logAudit(
                    action = "कार्यक्रम जोडला",
                    recordType = "FESTIVAL_EVENT",
                    recordIdentifier = event.eventName,
                    newValue = "${event.dateString} (${event.eventTime}) - ${event.programType}",
                    details = "जबाबदार: ${event.responsibleMember}, यजमान: ${event.aartiContributorName}",
                    user = _currentUser.value
                )
                onSuccess()
            } catch (e: Exception) {
                onError("कार्यक्रम जोडताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun updateFestivalEvent(
        event: FestivalEvent,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (event.eventName.isBlank()) {
                    onError("कृपया कार्यक्रमाचे नाव प्रविष्ट करा.")
                    return@launch
                }
                repository.updateFestivalEvent(event)
                logAudit(
                    action = "कार्यक्रम संपादित केला",
                    recordType = "FESTIVAL_EVENT",
                    recordIdentifier = event.eventName,
                    newValue = "${event.dateString} (${event.eventTime}) - ${event.programType}",
                    details = "जबाबदार: ${event.responsibleMember}, स्थिती: ${event.status}",
                    user = _currentUser.value
                )
                onSuccess()
            } catch (e: Exception) {
                onError("कार्यक्रम अपडेट करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun deleteFestivalEvent(
        event: FestivalEvent,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.deleteFestivalEvent(event)
                logAudit(
                    action = "कार्यक्रम हटवला",
                    recordType = "FESTIVAL_EVENT",
                    recordIdentifier = event.eventName,
                    oldValue = "${event.dateString} (${event.eventTime})",
                    details = "कार्यक्रमाचे वेळापत्रक बदलले",
                    user = _currentUser.value
                )
                onSuccess()
            } catch (e: Exception) {
                onError("कार्यक्रम हटवताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    // --- Cash Reconciliation ---
    fun performCashReconciliation(
        physicalCash: Double,
        notes: String = "",
        performedBy: String = _currentUser.value,
        onSuccess: (CashReconciliation) -> Unit
    ) {
        viewModelScope.launch {
            val sysCash = netCashInHand.value
            val sysUpi = netUpiInBank.value
            val diff = physicalCash - sysCash
            val status = if (kotlin.math.abs(diff) < 0.01) "MATCHED" else "MISMATCH"

            val rec = CashReconciliation(
                dateString = DateUtils.getTodayIsoDate(),
                timestamp = System.currentTimeMillis(),
                systemCash = sysCash,
                physicalCash = physicalCash,
                difference = diff,
                systemUpi = sysUpi,
                notes = notes,
                status = status,
                performedBy = performedBy
            )

            repository.insertCashReconciliation(rec)
            logAudit(
                action = if (status == "MATCHED") "रोख हिशोब जुळवला (तंतोतंत)" else "रोख हिशोब जुळवला (तफावत: ₹${diff.toLong()})",
                recordType = "RECONCILIATION",
                recordIdentifier = DateUtils.getTodayIsoDate(),
                oldValue = "सिस्टम रोख: ₹${sysCash.toLong()}",
                newValue = "प्रत्यक्ष रोख: ₹${physicalCash.toLong()}, फरक: ₹${diff.toLong()}",
                details = notes,
                user = performedBy
            )
            onSuccess(rec)
        }
    }

    fun performReconciliation(
        physicalCash: Double,
        physicalBank: Double = 0.0,
        notes: String = "",
        performedBy: String = _currentUser.value,
        onSuccess: (CashReconciliation) -> Unit = {}
    ) {
        performCashReconciliation(physicalCash = physicalCash, notes = notes, performedBy = performedBy, onSuccess = onSuccess)
    }

    // --- Vargani Actions ---

    suspend fun getNextPavtiNumber(): String {
        return repository.getNextPavtiNumber()
    }

    fun addVargani(
        vargani: VarganiTransaction,
        onSuccess: (VarganiTransaction) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Validation Rules
                if (vargani.contributorName.isBlank()) {
                    onError("कृपया नाव प्रविष्ट करा.")
                    return@launch
                }
                if (vargani.personType == "मालक" && vargani.amount < 1000.0) {
                    onError("मालकांसाठी किमान वर्गणी ₹१,००० असणे आवश्यक आहे.")
                    return@launch
                }
                if (vargani.personType == "भाडेकरू" && vargani.ownerName.isBlank()) {
                    onError("भाडेकरूसाठी मालकाचे नाव असणे आवश्यक आहे.")
                    return@launch
                }
                if (vargani.amount <= 0.0) {
                    onError("कृपया वैध वर्गणी रक्कम प्रविष्ट करा.")
                    return@launch
                }

                if (isDateClosed(vargani.timestamp)) {
                    onError("हा दिवस आधीच बंद केला आहे. नवीन नोंद करता येणार नाही.")
                    return@launch
                }

                val pavtiNo = if (vargani.pavtiNumber.isBlank()) {
                    repository.getNextPavtiNumber()
                } else {
                    vargani.pavtiNumber
                }
                val toInsert = vargani.copy(
                    pavtiNumber = pavtiNo,
                    collectedBy = if (vargani.collectedBy.isBlank()) _currentUser.value else vargani.collectedBy
                )
                val id = repository.insertVargani(toInsert)
                val finalVargani = toInsert.copy(id = id)

                logAudit(
                    action = "पावती तयार केली",
                    recordType = "VARGANI",
                    recordIdentifier = finalVargani.pavtiNumber,
                    newValue = "₹${finalVargani.amount.toLong()} - ${finalVargani.contributorName} (${finalVargani.personType}, ${finalVargani.paymentMode})",
                    details = "पत्ता: ${finalVargani.address}, मोबाईल: ${finalVargani.mobileNumber}",
                    user = _currentUser.value
                )

                onSuccess(finalVargani)
            } catch (e: Exception) {
                onError("पावती जतन करताना त्रुटी आली: ${e.localizedMessage}")
            }
        }
    }

    fun updateVargani(
        vargani: VarganiTransaction,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (vargani.personType == "मालक" && vargani.amount < 1000.0) {
                    onError("मालकांसाठी किमान वर्गणी ₹१,००० असणे आवश्यक आहे.")
                    return@launch
                }
                if (vargani.personType == "भाडेकरू" && vargani.ownerName.isBlank()) {
                    onError("भाडेकरूसाठी मालकाचे नाव असणे आवश्यक आहे.")
                    return@launch
                }
                if (isDateClosed(vargani.timestamp)) {
                    onError("बंद केलेल्या दिवसाची पावती बदलता येणार नाही.")
                    return@launch
                }

                val existing = repository.getVarganiById(vargani.id)
                val oldVal = if (existing != null) "₹${existing.amount.toLong()} (${existing.contributorName})" else ""

                repository.updateVargani(vargani)

                logAudit(
                    action = "पावती बदलली (Edit)",
                    recordType = "VARGANI",
                    recordIdentifier = vargani.pavtiNumber,
                    oldValue = oldVal,
                    newValue = "₹${vargani.amount.toLong()} (${vargani.contributorName})",
                    details = "प्रकार: ${vargani.personType}, पेमेंट: ${vargani.paymentMode}",
                    user = _currentUser.value
                )

                onSuccess()
            } catch (e: Exception) {
                onError("पावती अपडेट करताना त्रुटी आली: ${e.localizedMessage}")
            }
        }
    }

    fun cancelVargani(
        vargani: VarganiTransaction,
        reason: String = "",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (isDateClosed(vargani.timestamp)) {
                    onError("बंद केलेल्या दिवसाची पावती रद्द करता येणार नाही.")
                    return@launch
                }

                val updated = vargani.copy(
                    isCancelled = true,
                    cancelledReason = reason.ifBlank { "वापरकर्त्याने रद्द केली" }
                )
                repository.updateVargani(updated)

                logAudit(
                    action = "पावती रद्द केली (Cancelled)",
                    recordType = "VARGANI",
                    recordIdentifier = vargani.pavtiNumber,
                    oldValue = "₹${vargani.amount.toLong()} (${vargani.contributorName})",
                    newValue = "रद्द (Cancelled)",
                    details = "कारण: ${updated.cancelledReason}",
                    user = _currentUser.value
                )

                onSuccess()
            } catch (e: Exception) {
                onError("पावती रद्द करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun deleteVargani(vargani: VarganiTransaction) {
        viewModelScope.launch {
            if (isDateClosed(vargani.timestamp)) return@launch
            repository.deleteVargani(vargani)
            logAudit(
                action = "पावती डेटाबेसमधून हटवली",
                recordType = "VARGANI",
                recordIdentifier = vargani.pavtiNumber,
                oldValue = "₹${vargani.amount.toLong()} (${vargani.contributorName})",
                details = "कायमस्वरूपी हटवली",
                user = _currentUser.value
            )
        }
    }

    // --- Expense Actions ---

    fun addExpense(
        expense: ExpenseTransaction,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (expense.title.isBlank()) {
                    onError("कृपया खर्चाचे नाव प्रविष्ट करा.")
                    return@launch
                }
                if (expense.amount <= 0.0) {
                    onError("कृपया वैध रक्कम प्रविष्ट करा.")
                    return@launch
                }
                if (isDateClosed(expense.timestamp)) {
                    onError("हा दिवस आधीच बंद केला आहे. नवीन खर्च नोंद करता येणार नाही.")
                    return@launch
                }

                val voucherNo = if (expense.voucherNumber.isBlank()) {
                    "EXP-${System.currentTimeMillis() % 100000}"
                } else expense.voucherNumber

                val toInsert = expense.copy(voucherNumber = voucherNo)
                repository.insertExpense(toInsert)

                // If this is a final settlement linked to an advance, mark advance as settled
                if (toInsert.expenseType == "FINAL_SETTLEMENT" && toInsert.linkedAdvanceId != null) {
                    val advance = repository.getExpenseById(toInsert.linkedAdvanceId)
                    if (advance != null) {
                        repository.updateExpense(advance.copy(isSettled = true))
                    }
                }

                logAudit(
                    action = when (toInsert.expenseType) {
                        "ADVANCE" -> "आगाऊ रक्कम (Advance) नोंदवली"
                        "FINAL_SETTLEMENT" -> "अंतिम खर्च (Final Settlement) नोंदवला"
                        "FREE_SPONSORED" -> "मोफत / प्रायोजित खर्च नोंदवला"
                        else -> "खर्च नोंदवला"
                    },
                    recordType = "EXPENSE",
                    recordIdentifier = toInsert.voucherNumber,
                    newValue = "₹${toInsert.amount.toLong()} - ${toInsert.title} (${toInsert.category})",
                    details = "प्रकार: ${toInsert.expenseType}, पेमेंट: ${toInsert.paymentMode}, कोणाला: ${toInsert.paidTo}, जबाबदार: ${toInsert.memberAttribution}",
                    user = _currentUser.value
                )

                onSuccess()
            } catch (e: Exception) {
                onError("खर्च जतन करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun updateExpense(
        expense: ExpenseTransaction,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (expense.title.isBlank()) {
                    onError("कृपया खर्चाचे नाव प्रविष्ट करा.")
                    return@launch
                }
                if (expense.amount <= 0.0) {
                    onError("कृपया वैध रक्कम प्रविष्ट करा.")
                    return@launch
                }
                if (isDateClosed(expense.timestamp)) {
                    onError("बंद केलेल्या दिवसाचा खर्च बदलता येणार नाही.")
                    return@launch
                }

                val existing = repository.getExpenseById(expense.id)
                val oldVal = if (existing != null) "₹${existing.amount.toLong()} (${existing.title})" else ""

                repository.updateExpense(expense)

                // Update linked advance if needed
                if (expense.expenseType == "FINAL_SETTLEMENT" && expense.linkedAdvanceId != null) {
                    val advance = repository.getExpenseById(expense.linkedAdvanceId)
                    if (advance != null && !advance.isSettled) {
                        repository.updateExpense(advance.copy(isSettled = true))
                    }
                }

                logAudit(
                    action = "खर्च बदलला (Edit)",
                    recordType = "EXPENSE",
                    recordIdentifier = expense.voucherNumber,
                    oldValue = oldVal,
                    newValue = "₹${expense.amount.toLong()} (${expense.title})",
                    details = "वर्ग: ${expense.category}, बिल क्र: ${expense.billReceiptNumber}, प्रकार: ${expense.expenseType}",
                    user = _currentUser.value
                )

                onSuccess()
            } catch (e: Exception) {
                onError("खर्च अपडेट करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun cancelExpense(
        expense: ExpenseTransaction,
        reason: String = "",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (isDateClosed(expense.timestamp)) {
                    onError("बंद केलेल्या दिवसाचा खर्च रद्द करता येणार नाही.")
                    return@launch
                }

                val updated = expense.copy(
                    isCancelled = true,
                    cancelledReason = reason.ifBlank { "वापरकर्त्याने रद्द केली" }
                )
                repository.updateExpense(updated)

                // If a final settlement is cancelled, un-settle the linked advance
                if (expense.expenseType == "FINAL_SETTLEMENT" && expense.linkedAdvanceId != null) {
                    val advance = repository.getExpenseById(expense.linkedAdvanceId)
                    if (advance != null) {
                        repository.updateExpense(advance.copy(isSettled = false))
                    }
                }

                logAudit(
                    action = "खर्च नोंद रद्द केली (Cancelled)",
                    recordType = "EXPENSE",
                    recordIdentifier = expense.voucherNumber,
                    oldValue = "₹${expense.amount.toLong()} (${expense.title})",
                    newValue = "रद्द (Cancelled)",
                    details = "कारण: ${updated.cancelledReason}",
                    user = _currentUser.value
                )

                onSuccess()
            } catch (e: Exception) {
                onError("खर्च रद्द करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun deleteExpense(expense: ExpenseTransaction) {
        viewModelScope.launch {
            if (isDateClosed(expense.timestamp)) return@launch
            repository.deleteExpense(expense)

            // If a final settlement is deleted, un-settle the linked advance
            if (expense.expenseType == "FINAL_SETTLEMENT" && expense.linkedAdvanceId != null) {
                val advance = repository.getExpenseById(expense.linkedAdvanceId)
                if (advance != null) {
                    repository.updateExpense(advance.copy(isSettled = false))
                }
            }

            logAudit(
                action = "खर्च डेटाबेसमधून हटवला",
                recordType = "EXPENSE",
                recordIdentifier = expense.voucherNumber,
                oldValue = "₹${expense.amount.toLong()} (${expense.title})",
                details = "कायमस्वरूपी हटवला",
                user = _currentUser.value
            )
        }
    }

    // --- Income Actions ---

    fun addIncome(
        income: IncomeTransaction,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (income.sourceName.isBlank() || income.amount <= 0.0) {
                    onError("कृपया माहिती अचूक भरा.")
                    return@launch
                }
                repository.insertIncome(income)
                logAudit(
                    action = "इतर उत्पन्न नोंदवले",
                    recordType = "INCOME",
                    recordIdentifier = income.sourceName,
                    newValue = "₹${income.amount.toLong()} - ${income.sourceName} (${income.category})",
                    user = _currentUser.value
                )
                onSuccess()
            } catch (e: Exception) {
                onError("उत्पन्न नोंदवताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun deleteIncome(income: IncomeTransaction) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    // --- Pending Vargani Actions ---

    fun addPendingVargani(
        pending: PendingVargani,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (pending.name.isBlank() || pending.expectedAmount <= 0.0) {
                    onError("कृपया नाव व अपेक्षित रक्कम प्रविष्ट करा.")
                    return@launch
                }
                repository.insertPendingVargani(pending)
                onSuccess()
            } catch (e: Exception) {
                onError("शिल्लक वर्गणी नोंदवताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun markPendingAsCollected(pending: PendingVargani, pavtiNumber: String) {
        viewModelScope.launch {
            val vargani = VarganiTransaction(
                pavtiNumber = pavtiNumber,
                contributorName = pending.name,
                personType = pending.personType,
                ownerName = pending.ownerName,
                mobileNumber = pending.mobileNumber,
                address = pending.address,
                amount = pending.expectedAmount,
                notes = if (pending.note.isNotBlank()) "शिल्लक वर्गणीतून जमा: ${pending.note}" else "शिल्लक वर्गणीतून जमा",
                collectedBy = _currentUser.value
            )
            repository.insertVargani(vargani)
            repository.deletePendingVargani(pending)
            logAudit(
                action = "शिल्लक वर्गणी वसूल झाली व पावती तयार केली",
                recordType = "VARGANI",
                recordIdentifier = pavtiNumber,
                newValue = "₹${pending.expectedAmount.toLong()} (${pending.name})",
                user = _currentUser.value
            )
        }
    }

    fun deletePendingVargani(pending: PendingVargani) {
        viewModelScope.launch {
            repository.deletePendingVargani(pending)
        }
    }

    // --- Settings Actions ---

    fun saveSettings(
        settings: MandalSettings,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.saveSettings(settings)
            logAudit(
                action = "मंडळ माहिती अद्ययावत केली",
                recordType = "SETTINGS",
                recordIdentifier = settings.mandalName,
                details = "अध्यक्ष: ${settings.presidentName}, खजिनदार: ${settings.treasurerName}",
                user = _currentUser.value
            )
            onSuccess()
        }
    }

    // --- Backup & Restore Actions ---

    fun createBackupJson(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val data = repository.exportData()
                val json = BackupManager.toJson(data)
                logAudit(
                    action = "संपूर्ण डेटाबॅकअप तयार केला (Export)",
                    recordType = "BACKUP",
                    recordIdentifier = "AKGMM_Backup_${DateUtils.getTodayIsoDate()}",
                    details = "एकूण पावत्या: ${data.varganiList.size}, खर्च: ${data.expenseList.size}",
                    user = _currentUser.value
                )
                onResult(json)
            } catch (_: Exception) {
                onResult(null)
            }
        }
    }

    fun restoreBackupJson(
        jsonString: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val data = BackupManager.parseBackupJson(jsonString)
                if (data != null) {
                    repository.restoreData(data)
                    logAudit(
                        action = "डेटा रिस्टोअर केला (Import)",
                        recordType = "BACKUP",
                        recordIdentifier = "RESTORED",
                        details = "पुनर्संचयित पावत्या: ${data.varganiList.size}, खर्च: ${data.expenseList.size}",
                        user = _currentUser.value
                    )
                    onSuccess()
                } else {
                    onError("बॅकअप फाईल अवैध आहे किंवा वाचता आली नाही.")
                }
            } catch (e: Exception) {
                onError("डेटा पुनर्संचयित करताना त्रुटी: ${e.localizedMessage}")
            }
        }
    }

    fun resetAllData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            logAudit(
                action = "सर्व डेटा रीसेट केला",
                recordType = "SYSTEM",
                recordIdentifier = "RESET_ALL",
                user = _currentUser.value
            )
            onSuccess()
        }
    }

    // --- AI Assistant ---
    fun sendAiMessage(userMessage: String) {
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage(text = userMessage, isUser = true))
        _chatMessages.value = current

        viewModelScope.launch {
            val totalV = totalVargani.value
            val ownerV = totalOwnerVargani.value
            val tenantV = totalTenantVargani.value
            val ownerC = totalOwnersCount.value
            val tenantC = totalTenantsCount.value
            val exp = totalExpense.value
            val bal = netBalance.value
            val cashBal = netCashInHand.value
            val upiBal = netUpiInBank.value

            val reply = when {
                userMessage.contains("हिशोब") || userMessage.contains("ताळेबंद") || userMessage.contains("शिल्लक") -> {
                    "📊 *AKGMM मंडळ ताळेबंद सद्यस्थिती:*\n\n" +
                            "• एकूण वर्गणी जमा: ₹${totalV.toLong()}\n" +
                            "  - मालकांकडून: ₹${ownerV.toLong()} ($ownerC मालक)\n" +
                            "  - भाडेकरूंकडून: ₹${tenantV.toLong()} ($tenantC भाडेकरू)\n" +
                            "• रोख जमा शिल्लक: ₹${cashBal.toLong()}\n" +
                            "• बँक/UPI शिल्लक: ₹${upiBal.toLong()}\n" +
                            "• एकूण खर्च: ₹${exp.toLong()}\n" +
                            "• निव्वळ शिल्लक: ₹${bal.toLong()}\n\n" +
                            "सर्व नोंदी स्थानिक डेटाबेसमध्ये सुरक्षित आहेत."
                }
                userMessage.contains("मालक") || userMessage.contains("भाडेकरू") -> {
                    "🏘️ *मालक व भाडेकरू वर्गणी नियम:*\n\n" +
                            "१. मालकांसाठी किमान वर्गणी ₹१,००० आहे. ($ownerC नोंदणीकृत मालक - एकूण जमा: ₹${ownerV.toLong()})\n" +
                            "२. भाडेकरूंसाठी मालकाचे नाव अनिवार्य आहे. ($tenantC नोंदणीकृत भाडेकरू - एकूण जमा: ₹${tenantV.toLong()})\n" +
                            "३. 'वर्गणी' किंवा 'डॅशबोर्ड' मध्ये मालकनिहाय तपशील पाहू शकता."
                }
                userMessage.contains("पावती") || userMessage.contains("pdf") || userMessage.contains("whatsapp") -> {
                    "📜 *पावती तयार व शेअर करणे:*\n\n" +
                            "• नवीन पावती जोडण्यासाठी खालील '+' बटण दाबा.\n" +
                            "• मालक किंवा भाडेकरू निवडा.\n" +
                            "• जतन केल्यावर लगेच डिजिटल पावती दिसेल.\n" +
                            "• WhatsApp किंवा PDF द्वारे पावती देणगीदाराला पाठवता येते."
                }
                else -> {
                    "🙏 *AKGMM सहाय्यक:*\n" +
                            "मी तुम्हाला वर्गणी नोंदवणे, दिवसाचा हिशोब बंद करणे, रोख-UPI पडताळणी, मालक/भाडेकरू हिशोब, ताळेबंद अहवाल, WhatsApp पावती आणि PDF डाऊनलोड करण्यात मदत करू शकतो. काय माहिती हवी आहे?"
                }
            }

            val updated = _chatMessages.value.toMutableList()
            updated.add(ChatMessage(text = reply, isUser = false))
            _chatMessages.value = updated
        }
    }
}

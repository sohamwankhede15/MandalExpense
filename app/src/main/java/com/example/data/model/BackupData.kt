package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BackupData(
    val exportTimestamp: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0",
    val settings: MandalSettings? = null,
    val varganiList: List<VarganiTransaction> = emptyList(),
    val expenseList: List<ExpenseTransaction> = emptyList(),
    val pendingList: List<PendingVargani> = emptyList(),
    val incomeList: List<IncomeTransaction> = emptyList(),
    val dailyClosingList: List<DailyClosing> = emptyList(),
    val auditLogList: List<AuditLog> = emptyList(),
    val reconciliationList: List<CashReconciliation> = emptyList(),
    val festivalEventList: List<FestivalEvent> = emptyList()
)

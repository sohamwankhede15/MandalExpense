package com.example.data.repository

import com.example.data.local.MandalDao
import com.example.data.model.AuditLog
import com.example.data.model.BackupData
import com.example.data.model.CashReconciliation
import com.example.data.model.DailyClosing
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.IncomeTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.PendingVargani
import com.example.data.model.VarganiTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MandalRepository(private val mandalDao: MandalDao) {

    // Vargani
    val allVargani: Flow<List<VarganiTransaction>> = mandalDao.getAllVargani()
    val totalVarganiAmount: Flow<Double?> = mandalDao.getTotalVarganiAmount()
    val totalOwnerVarganiAmount: Flow<Double?> = mandalDao.getTotalOwnerVarganiAmount()
    val totalTenantVarganiAmount: Flow<Double?> = mandalDao.getTotalTenantVarganiAmount()
    val totalOtherVarganiAmount: Flow<Double?> = mandalDao.getTotalOtherVarganiAmount()

    suspend fun getVarganiById(id: Long): VarganiTransaction? = mandalDao.getVarganiById(id)

    suspend fun insertVargani(vargani: VarganiTransaction): Long = mandalDao.insertVargani(vargani)

    suspend fun updateVargani(vargani: VarganiTransaction) = mandalDao.updateVargani(vargani)

    suspend fun deleteVargani(vargani: VarganiTransaction) = mandalDao.deleteVargani(vargani)

    suspend fun getNextPavtiNumber(): String {
        val count = mandalDao.getVarganiCount() + 1
        return "AKGMM-2026-${String.format("%05d", count)}"
    }

    // Expenses
    val allExpenses: Flow<List<ExpenseTransaction>> = mandalDao.getAllExpenses()
    val totalExpenseAmount: Flow<Double?> = mandalDao.getTotalExpenseAmount()

    suspend fun getExpenseById(id: Long): ExpenseTransaction? = mandalDao.getExpenseById(id)

    suspend fun insertExpense(expense: ExpenseTransaction): Long = mandalDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseTransaction) = mandalDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseTransaction) = mandalDao.deleteExpense(expense)

    // Income
    val allIncome: Flow<List<IncomeTransaction>> = mandalDao.getAllIncome()
    val totalIncomeAmount: Flow<Double?> = mandalDao.getTotalIncomeAmount()

    suspend fun insertIncome(income: IncomeTransaction): Long = mandalDao.insertIncome(income)

    suspend fun deleteIncome(income: IncomeTransaction) = mandalDao.deleteIncome(income)

    // Pending Vargani
    val allPendingVargani: Flow<List<PendingVargani>> = mandalDao.getAllPendingVargani()

    suspend fun insertPendingVargani(pending: PendingVargani): Long = mandalDao.insertPendingVargani(pending)

    suspend fun updatePendingVargani(pending: PendingVargani) = mandalDao.updatePendingVargani(pending)

    suspend fun deletePendingVargani(pending: PendingVargani) = mandalDao.deletePendingVargani(pending)

    // Settings
    val settings: Flow<MandalSettings?> = mandalDao.getSettings()

    suspend fun saveSettings(settings: MandalSettings) = mandalDao.saveSettings(settings)

    // Daily Closings
    val allDailyClosings: Flow<List<DailyClosing>> = mandalDao.getAllDailyClosings()

    suspend fun getDailyClosingByDate(dateString: String): DailyClosing? =
        mandalDao.getDailyClosingByDate(dateString)

    suspend fun insertDailyClosing(closing: DailyClosing): Long =
        mandalDao.insertDailyClosing(closing)

    suspend fun deleteDailyClosing(closing: DailyClosing) =
        mandalDao.deleteDailyClosing(closing)

    suspend fun deleteDailyClosingByDate(dateString: String) =
        mandalDao.deleteDailyClosingByDate(dateString)

    // Audit Logs
    val allAuditLogs: Flow<List<AuditLog>> = mandalDao.getAllAuditLogs()

    suspend fun insertAuditLog(log: AuditLog): Long =
        mandalDao.insertAuditLog(log)

    // Cash Reconciliations
    val allCashReconciliations: Flow<List<CashReconciliation>> = mandalDao.getAllCashReconciliations()

    suspend fun insertCashReconciliation(reconciliation: CashReconciliation): Long =
        mandalDao.insertCashReconciliation(reconciliation)

    // Festival Events
    val allFestivalEvents: Flow<List<FestivalEvent>> = mandalDao.getAllFestivalEvents()

    fun getEventsByDate(dateString: String): Flow<List<FestivalEvent>> =
        mandalDao.getEventsByDate(dateString)

    suspend fun getFestivalEventById(id: Long): FestivalEvent? =
        mandalDao.getFestivalEventById(id)

    suspend fun insertFestivalEvent(event: FestivalEvent): Long =
        mandalDao.insertFestivalEvent(event)

    suspend fun updateFestivalEvent(event: FestivalEvent) =
        mandalDao.updateFestivalEvent(event)

    suspend fun deleteFestivalEvent(event: FestivalEvent) =
        mandalDao.deleteFestivalEvent(event)

    // Backup and Restore
    suspend fun exportData(): BackupData {
        val vargani = mandalDao.getAllVargani().first()
        val expenses = mandalDao.getAllExpenses().first()
        val income = mandalDao.getAllIncome().first()
        val pending = mandalDao.getAllPendingVargani().first()
        val settings = mandalDao.getSettings().first() ?: MandalSettings()
        val dailyClosings = mandalDao.getAllDailyClosings().first()
        val auditLogs = mandalDao.getAllAuditLogs().first()
        val reconciliations = mandalDao.getAllCashReconciliations().first()
        val festivalEvents = mandalDao.getAllFestivalEvents().first()

        return BackupData(
            varganiList = vargani,
            expenseList = expenses,
            incomeList = income,
            pendingList = pending,
            settings = settings,
            dailyClosingList = dailyClosings,
            auditLogList = auditLogs,
            reconciliationList = reconciliations,
            festivalEventList = festivalEvents
        )
    }

    suspend fun restoreData(backupData: BackupData) {
        mandalDao.clearAllVargani()
        mandalDao.clearAllExpenses()
        mandalDao.clearAllIncome()
        mandalDao.clearAllPendingVargani()
        mandalDao.clearAllDailyClosings()
        mandalDao.clearAllAuditLogs()
        mandalDao.clearAllCashReconciliations()
        mandalDao.clearAllFestivalEvents()

        mandalDao.insertAllVargani(backupData.varganiList)
        mandalDao.insertAllExpenses(backupData.expenseList)
        mandalDao.insertAllIncome(backupData.incomeList)
        mandalDao.insertAllPendingVargani(backupData.pendingList)
        if (backupData.dailyClosingList.isNotEmpty()) {
            mandalDao.insertAllDailyClosings(backupData.dailyClosingList)
        }
        if (backupData.auditLogList.isNotEmpty()) {
            mandalDao.insertAllAuditLogs(backupData.auditLogList)
        }
        if (backupData.reconciliationList.isNotEmpty()) {
            mandalDao.insertAllCashReconciliations(backupData.reconciliationList)
        }
        if (backupData.festivalEventList.isNotEmpty()) {
            mandalDao.insertAllFestivalEvents(backupData.festivalEventList)
        }
        backupData.settings?.let { mandalDao.saveSettings(it) }
    }

    suspend fun clearAllData() {
        mandalDao.clearAllVargani()
        mandalDao.clearAllExpenses()
        mandalDao.clearAllIncome()
        mandalDao.clearAllPendingVargani()
        mandalDao.clearAllDailyClosings()
        mandalDao.clearAllAuditLogs()
        mandalDao.clearAllCashReconciliations()
        mandalDao.clearAllFestivalEvents()
    }
}

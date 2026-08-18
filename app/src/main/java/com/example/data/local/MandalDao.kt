package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLog
import com.example.data.model.CashReconciliation
import com.example.data.model.DailyClosing
import com.example.data.model.ExpenseTransaction
import com.example.data.model.FestivalEvent
import com.example.data.model.IncomeTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.PendingVargani
import com.example.data.model.VarganiTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MandalDao {
    // Vargani Transactions
    @Query("SELECT * FROM vargani_transactions ORDER BY timestamp DESC")
    fun getAllVargani(): Flow<List<VarganiTransaction>>

    @Query("SELECT * FROM vargani_transactions WHERE id = :id")
    suspend fun getVarganiById(id: Long): VarganiTransaction?

    @Query("SELECT * FROM vargani_transactions WHERE pavtiNumber = :pavtiNumber LIMIT 1")
    suspend fun getVarganiByPavtiNumber(pavtiNumber: String): VarganiTransaction?

    @Query("SELECT * FROM vargani_transactions WHERE personType = :personType ORDER BY timestamp DESC")
    fun getVarganiByPersonType(personType: String): Flow<List<VarganiTransaction>>

    @Query("SELECT * FROM vargani_transactions WHERE ownerName = :ownerName ORDER BY timestamp DESC")
    fun getVarganiByOwnerName(ownerName: String): Flow<List<VarganiTransaction>>

    @Query("SELECT * FROM vargani_transactions WHERE contributorName LIKE '%' || :query || '%' OR mobileNumber LIKE '%' || :query || '%' OR pavtiNumber LIKE '%' || :query || '%' OR ownerName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchVargani(query: String): Flow<List<VarganiTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVargani(vargani: VarganiTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVargani(list: List<VarganiTransaction>)

    @Update
    suspend fun updateVargani(vargani: VarganiTransaction)

    @Delete
    suspend fun deleteVargani(vargani: VarganiTransaction)

    @Query("DELETE FROM vargani_transactions")
    suspend fun clearAllVargani()

    @Query("SELECT COUNT(*) FROM vargani_transactions WHERE isCancelled = 0")
    suspend fun getVarganiCount(): Int

    @Query("SELECT SUM(amount) FROM vargani_transactions WHERE isCancelled = 0")
    fun getTotalVarganiAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM vargani_transactions WHERE (personType = 'घरमालक' OR personType = 'मालक' OR personType = 'OWNER') AND isCancelled = 0")
    fun getTotalOwnerVarganiAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM vargani_transactions WHERE (personType = 'भाडेकरू' OR personType = 'TENANT') AND isCancelled = 0")
    fun getTotalTenantVarganiAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM vargani_transactions WHERE (personType = 'इतर' OR personType = 'OTHER') AND isCancelled = 0")
    fun getTotalOtherVarganiAmount(): Flow<Double?>

    // Expenses
    @Query("SELECT * FROM expense_transactions ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseTransaction?

    @Query("SELECT * FROM expense_transactions WHERE expenseType = 'ADVANCE' AND isCancelled = 0 ORDER BY timestamp DESC")
    fun getAllAdvances(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE expenseType = 'ADVANCE' AND isSettled = 0 AND isCancelled = 0 ORDER BY timestamp DESC")
    fun getUnsettledAdvances(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE isFree = 1 AND isCancelled = 0 ORDER BY timestamp DESC")
    fun getFreeExpenses(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE (isMahaprasad = 1 OR category = 'महाप्रसाद व भोजन') AND isCancelled = 0 ORDER BY timestamp DESC")
    fun getMahaprasadExpenses(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR voucherNumber LIKE '%' || :query || '%' OR paidTo LIKE '%' || :query || '%' OR sponsorName LIKE '%' || :query || '%' OR memberAttribution LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchExpenses(query: String): Flow<List<ExpenseTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpenses(list: List<ExpenseTransaction>)

    @Update
    suspend fun updateExpense(expense: ExpenseTransaction)

    @Delete
    suspend fun deleteExpense(expense: ExpenseTransaction)

    @Query("DELETE FROM expense_transactions")
    suspend fun clearAllExpenses()

    @Query("SELECT SUM(amount) FROM expense_transactions WHERE isCancelled = 0 AND isFree = 0")
    fun getTotalExpenseAmount(): Flow<Double?>

    // Income (Other)
    @Query("SELECT * FROM income_transactions ORDER BY timestamp DESC")
    fun getAllIncome(): Flow<List<IncomeTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIncome(list: List<IncomeTransaction>)

    @Delete
    suspend fun deleteIncome(income: IncomeTransaction)

    @Query("DELETE FROM income_transactions")
    suspend fun clearAllIncome()

    @Query("SELECT SUM(amount) FROM income_transactions")
    fun getTotalIncomeAmount(): Flow<Double?>

    // Pending Vargani
    @Query("SELECT * FROM pending_vargani ORDER BY timestamp DESC")
    fun getAllPendingVargani(): Flow<List<PendingVargani>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingVargani(pending: PendingVargani): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPendingVargani(list: List<PendingVargani>)

    @Update
    suspend fun updatePendingVargani(pending: PendingVargani)

    @Delete
    suspend fun deletePendingVargani(pending: PendingVargani)

    @Query("DELETE FROM pending_vargani")
    suspend fun clearAllPendingVargani()

    // Mandal Settings
    @Query("SELECT * FROM mandal_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<MandalSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: MandalSettings)

    // Daily Closings
    @Query("SELECT * FROM daily_closings ORDER BY closingTimestamp DESC")
    fun getAllDailyClosings(): Flow<List<DailyClosing>>

    @Query("SELECT * FROM daily_closings WHERE dateString = :dateString LIMIT 1")
    suspend fun getDailyClosingByDate(dateString: String): DailyClosing?

    @Query("SELECT * FROM daily_closings WHERE dateString = :dateString LIMIT 1")
    fun getDailyClosingFlowByDate(dateString: String): Flow<DailyClosing?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyClosing(closing: DailyClosing): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDailyClosings(list: List<DailyClosing>)

    @Delete
    suspend fun deleteDailyClosing(closing: DailyClosing)

    @Query("DELETE FROM daily_closings WHERE dateString = :dateString")
    suspend fun deleteDailyClosingByDate(dateString: String)

    @Query("DELETE FROM daily_closings")
    suspend fun clearAllDailyClosings()

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAuditLogs(list: List<AuditLog>)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAllAuditLogs()

    // Cash Reconciliations
    @Query("SELECT * FROM cash_reconciliations ORDER BY timestamp DESC")
    fun getAllCashReconciliations(): Flow<List<CashReconciliation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashReconciliation(reconciliation: CashReconciliation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCashReconciliations(list: List<CashReconciliation>)

    @Query("DELETE FROM cash_reconciliations")
    suspend fun clearAllCashReconciliations()

    // Festival Events
    @Query("SELECT * FROM festival_events ORDER BY dateString ASC, eventTime ASC")
    fun getAllFestivalEvents(): Flow<List<FestivalEvent>>

    @Query("SELECT * FROM festival_events WHERE dateString = :dateString ORDER BY eventTime ASC")
    fun getEventsByDate(dateString: String): Flow<List<FestivalEvent>>

    @Query("SELECT * FROM festival_events WHERE id = :id")
    suspend fun getFestivalEventById(id: Long): FestivalEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestivalEvent(event: FestivalEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFestivalEvents(list: List<FestivalEvent>)

    @Update
    suspend fun updateFestivalEvent(event: FestivalEvent)

    @Delete
    suspend fun deleteFestivalEvent(event: FestivalEvent)

    @Query("DELETE FROM festival_events")
    suspend fun clearAllFestivalEvents()
}

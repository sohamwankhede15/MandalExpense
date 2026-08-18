package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "expense_transactions")
data class ExpenseTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val voucherNumber: String = "",
    val title: String = "",
    val category: String = "इतर खर्च",
    val amount: Double = 0.0,
    val paymentMode: String = "CASH",
    val paidTo: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val billReceiptNumber: String = "",
    val billImagePath: String = "",
    val isCancelled: Boolean = false,
    val cancelledReason: String = "",
    // Advanced Accounting Fields
    val expenseType: String = "REGULAR", // "REGULAR", "ADVANCE", "FINAL_SETTLEMENT", "FREE_SPONSORED"
    val linkedAdvanceId: Long? = null,
    val advancePaidAmount: Double = 0.0,
    val totalEstimatedCost: Double = 0.0,
    val isSettled: Boolean = false,
    val isFree: Boolean = false,
    val sponsorName: String = "",
    val isMahaprasad: Boolean = false,
    val memberAttribution: String = ""
)


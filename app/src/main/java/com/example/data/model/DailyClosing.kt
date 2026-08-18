package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "daily_closings")
data class DailyClosing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD" format, e.g. "2026-08-18"
    val closingTimestamp: Long = System.currentTimeMillis(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val cashTotal: Double = 0.0,
    val upiTotal: Double = 0.0,
    val closingBalance: Double = 0.0,
    val totalPavtisCount: Int = 0,
    val totalExpensesCount: Int = 0,
    val closedBy: String = "Akhil",
    val isClosed: Boolean = true,
    val notes: String = ""
)

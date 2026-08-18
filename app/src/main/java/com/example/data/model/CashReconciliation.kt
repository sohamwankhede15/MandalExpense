package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "cash_reconciliations")
data class CashReconciliation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val timestamp: Long = System.currentTimeMillis(),
    val systemCash: Double,
    val physicalCash: Double,
    val difference: Double, // physicalCash - systemCash
    val systemUpi: Double = 0.0,
    val notes: String = "",
    val status: String = "MATCHED", // "MATCHED", "MISMATCH"
    val performedBy: String = "Akhil"
)

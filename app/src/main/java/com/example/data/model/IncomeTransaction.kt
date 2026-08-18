package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "income_transactions")
data class IncomeTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String = "",
    val sourceName: String = "",
    val category: String = "इतर उत्पन्न", // लिलाव, प्रायोजक, स्टॉल भाडे, व्याज, देणगी
    val amount: Double = 0.0,
    val paymentMode: String = "CASH",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val receivedBy: String = ""
)

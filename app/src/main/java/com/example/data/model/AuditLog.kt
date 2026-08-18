package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String, // e.g. "पावती तयार केली", "पावती बदलली", "पावती रद्द केली", "खर्च नोंदवला", "हिशोब बंद केला", "दिवस अनलॉक केला", "रोख-UPI हिशोब जुळवला"
    val recordType: String = "", // "VARGANI", "EXPENSE", "INCOME", "DAY_CLOSING", "RECONCILIATION", "BACKUP", "AUTH"
    val recordIdentifier: String = "", // e.g. "AKGMM-2026-00042", "VOUCHER-001"
    val oldValue: String = "",
    val newValue: String = "",
    val performedBy: String = "Akhil",
    val timestamp: Long = System.currentTimeMillis(),
    val details: String = ""
)

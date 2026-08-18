package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "pending_vargani")
data class PendingVargani(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val personType: String = "घरमालक", // "घरमालक", "भाडेकरू", "इतर"
    val ownerName: String = "",
    val otherPersonType: String = "",
    val customCategoryName: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val expectedAmount: Double,
    val note: String = "",
    val isCollected: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isOwner: Boolean
        get() = PersonType.isOwner(personType)

    val isTenant: Boolean
        get() = PersonType.isTenant(personType)

    val isOther: Boolean
        get() = PersonType.isOther(personType)
}


package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "vargani_transactions")
data class VarganiTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pavtiNumber: String,
    val contributorName: String,
    val personType: String = "घरमालक", // "घरमालक" (Owner), "भाडेकरू" (Tenant), "इतर" (Other)
    val ownerName: String = "", // Applicable when personType == "भाडेकरू"
    val otherPersonType: String = "", // Applicable when personType == "इतर" ("व्यावसायिक / व्यापारी", "नगरसेवक", "आमदार", "राजकीय व्यक्ती", "देणगीदार", "प्रायोजक", "इतर")
    val customCategoryName: String = "", // Custom name when otherPersonType == "इतर"
    val mobileNumber: String = "",
    val address: String = "",
    val amount: Double,
    val amountInWords: String = "",
    val category: String = "घरगुती वर्गणी",
    val paymentMode: String = "रोख", // "रोख" (Cash), "UPI", "गुगल पे", "फोन पे", "चेक", "बँक"
    val notes: String = "",
    val collectedBy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isCancelled: Boolean = false,
    val cancelledReason: String = ""
) {
    val isOwner: Boolean
        get() = PersonType.isOwner(personType)

    val isTenant: Boolean
        get() = PersonType.isTenant(personType)

    val isOther: Boolean
        get() = PersonType.isOther(personType)

    val displayPersonType: String
        get() {
            return when {
                isOwner -> "घरमालक"
                isTenant -> "भाडेकरू"
                isOther -> {
                    val subLabel = OtherPersonType.getDisplayLabel(otherPersonType, customCategoryName)
                    if (subLabel.isNotBlank()) "इतर ($subLabel)" else "इतर"
                }
                else -> personType
            }
        }

    val displayHeading: String
        get() {
            return when {
                isOwner -> "घरमालक वर्गणी"
                isTenant -> "भाडेकरू वर्गणी"
                isOther -> OtherPersonType.getContributionHeading(otherPersonType, customCategoryName)
                else -> "वर्गणी पावती"
            }
        }
}


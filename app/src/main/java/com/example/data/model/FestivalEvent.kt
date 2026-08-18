package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "festival_events")
data class FestivalEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD" format, e.g. "2026-09-14"
    val eventName: String, // e.g. "गणपती स्थापना", "संध्याकाळची आरती", "नृत्य स्पर्धा", "महाप्रसाद"
    val eventTime: String = "07:30 PM", // e.g. "07:30 PM"
    val programType: String = "आरती", // "गणपती स्थापना", "आरती", "नृत्य स्पर्धा", "सांस्कृतिक कार्यक्रम", "भजन", "कीर्तन", "महाप्रसाद", "विसर्जन", "इतर"
    val customProgramType: String = "",
    val location: String = "मुख्य मंडप",
    val responsibleMember: String = "", // Assigned from 15 members
    val responsibleMobile: String = "",
    val aartiContributorName: String = "", // आरतीचे यजमान / योगदानकर्ता
    val aartiContributorType: String = "", // सदस्य, घरमालक, भाडेकरू, नगरसेवक, आमदार, व्यावसायिक / व्यापारी, देणगीदार, प्रायोजक, इतर
    val aartiTime: String = "",
    val mahaprasadContributorName: String = "", // महाप्रसाद कोणाकडून
    val mahaprasadContributorType: String = "",
    val flowerArrangementType: String = "मंडळाचा खर्च", // मंडळाचा खर्च, सदस्याकडून, घरमालकाकडून, भाडेकरूकडून, नगरसेवकाकडून, आमदाराकडून, व्यावसायिकाकडून, देणगीदाराकडून, प्रायोजकाकडून, इतर
    val flowerContributorName: String = "",
    val flowerEstimatedCost: Double = 0.0,
    val flowerActualExpense: Double = 0.0,
    val otherArrangements: String = "", // Sound, Stage, Lighting, etc.
    val status: String = "नियोजित", // "नियोजित", "तयारी सुरू", "पूर्ण", "रद्द"
    val notes: String = "",
    val estimatedBudget: Double = 0.0,
    val optionalFinancialAmount: Double = 0.0,
    val isLinkedToFinancials: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

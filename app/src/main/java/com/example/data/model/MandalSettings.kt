package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "mandal_settings")
data class MandalSettings(
    @PrimaryKey val id: Int = 1,
    val mandalName: String = "अखिल गणेशनगर मित्र मंडळ (AKGMM)",
    val subTitle: String = "गणेशनगर हडपसर, पुणे - २८",
    val festivalYear: String = "2025-2026",
    val registrationNumber: String = "MH/AKGMM/2010",
    val presidentName: String = "अध्यक्ष",
    val treasurerName: String = "खजिनदार",
    val authorizedSignatory: String = "कार्यवाह / खजिनदार",
    val address: String = "गणेशनगर, हडपसर, पुणे - ४११०२८",
    val receiptPrefix: String = "AKGMM-",
    val upiId: String = "akgmm@upi",
    val contactNumber: String = "9876543210",
    val isPinEnabled: Boolean = false,
    val securityPin: String = "",
    val selectedLanguage: String = "mr", // "mr" for Marathi, "en" for English
    val themeColorHex: Long = 0xFFD84315,
    val executiveMembers: String = "1. अध्यक्ष - सचिन सपकाळ\n2. उपाध्यक्ष - राहुल कदम\n3. खजिनदार - सागर शितोळे\n4. सह-खजिनदार - अमोल जगताप\n5. कार्यवाह - विकास मोरे\n6. सह-कार्यवाह - विशाल कांबळे\n7. मुख्य सल्लागार - बाबासाहेब माने\n8. सजावट प्रमुख - रोहन शिंदे\n9. महाप्रसाद प्रमुख - दीपक पाटील\n10. ध्वनी व लाईट प्रमुख - नितीन थोरात\n11. मिरवणूक प्रमुख - गणेश गायकवाड\n12. प्रसिद्धी प्रमुख - स्वप्नील भोसले\n13. सुरक्षा प्रमुख - किरण चव्हाण\n14. सदस्य - महेश सावंत\n15. सदस्य - प्रशांत पवार",
    val festivalName: String = "गणेशोत्सव २०२६",
    val festivalStartDate: String = "2026-09-14",
    val festivalEndDate: String = "2026-09-24",
    val accountingStartDate: String = "2026-09-12"
)

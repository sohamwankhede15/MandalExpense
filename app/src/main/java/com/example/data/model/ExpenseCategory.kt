package com.example.data.model

data class ExpenseCategory(
    val id: String,
    val nameMarathi: String,
    val nameEnglish: String,
    val iconName: String
) {
    companion object {
        val defaultCategories = listOf(
            ExpenseCategory("mandap", "मंडप व स्टेज सजावट", "Mandap & Stage Decoration", "HolidayVillage"),
            ExpenseCategory("sound_light", "लाईटिंग व ध्वनी (Sound/DJ)", "Lighting & Sound/DJ", "VolumeUp"),
            ExpenseCategory("murti", "श्रींची मूर्ती व पूजन", "Ganesh Idol & Puja Setup", "SelfImprovement"),
            ExpenseCategory("prasad", "महाप्रसाद व भोजन", "Maha Prasad & Food", "Restaurant"),
            ExpenseCategory("puja_samagri", "दैनिक पूजा साहित्य व फुले", "Daily Puja Items & Flowers", "LocalFlorist"),
            ExpenseCategory("visarjan", "मिरवणूक व विसर्जन", "Procession & Visarjan", "Festival"),
            ExpenseCategory("security_police", "सुरक्षा व पोलीस परवानगी", "Security & Permissions", "Security"),
            ExpenseCategory("memento", "बक्षीस व मानचिन्ह", "Prizes & Mementos", "EmojiEvents"),
            ExpenseCategory("advertisement", "प्रसिद्धी व बॅनर/फ्लेक्स", "Publicity & Banners", "Campaign"),
            ExpenseCategory("other", "इतर किरकोळ खर्च", "Miscellaneous / Other", "ReceiptLong")
        )
    }
}

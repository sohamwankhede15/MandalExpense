package com.example.data.model

/**
 * Defines the primary person categories for Mandal Vargani collection.
 */
enum class PersonType(val code: String, val marathiLabel: String) {
    OWNER("OWNER", "घरमालक"),
    TENANT("TENANT", "भाडेकरू"),
    OTHER("OTHER", "इतर");

    companion object {
        fun fromCodeOrLabel(value: String): PersonType {
            val clean = value.trim()
            return when {
                clean.equals("OWNER", ignoreCase = true) || clean == "घरमालक" || clean == "मालक" -> OWNER
                clean.equals("TENANT", ignoreCase = true) || clean == "भाडेकरू" -> TENANT
                clean.equals("OTHER", ignoreCase = true) || clean == "इतर" -> OTHER
                clean.contains("घरमालक") || clean.contains("मालक") -> OWNER
                clean.contains("भाडेकरू") -> TENANT
                clean.contains("इतर") -> OTHER
                else -> OWNER
            }
        }

        fun isOwner(value: String): Boolean = fromCodeOrLabel(value) == OWNER
        fun isTenant(value: String): Boolean = fromCodeOrLabel(value) == TENANT
        fun isOther(value: String): Boolean = fromCodeOrLabel(value) == OTHER

        fun getStandardMarathiLabel(value: String): String {
            return fromCodeOrLabel(value).marathiLabel
        }
    }
}

/**
 * Sub-categories for "इतर" (Other) contributors.
 */
enum class OtherPersonType(
    val code: String,
    val marathiLabel: String,
    val contributionHeading: String,
    val defaultIconName: String = ""
) {
    BUSINESS("BUSINESS", "व्यावसायिक / व्यापारी", "व्यावसायिक / व्यापारी योगदान"),
    CORPORATOR("CORPORATOR", "नगरसेवक", "नगरसेवक योगदान"),
    MLA("MLA", "आमदार", "आमदार योगदान"),
    POLITICAL_PERSON("POLITICAL_PERSON", "राजकीय व्यक्ती", "राजकीय व्यक्ती योगदान"),
    DONOR("DONOR", "देणगीदार", "देणगी"),
    SPONSOR("SPONSOR", "प्रायोजक", "प्रायोजक योगदान"),
    OTHER("OTHER", "इतर", "इतर योगदान");

    companion object {
        fun fromCodeOrLabel(value: String): OtherPersonType {
            val trimmed = value.trim()
            if (trimmed.isBlank()) return OTHER

            return entries.find {
                it.code.equals(trimmed, ignoreCase = true) ||
                        it.marathiLabel.equals(trimmed, ignoreCase = true)
            } ?: when {
                trimmed.contains("नगरसेवक") -> CORPORATOR
                trimmed.contains("आमदार") -> MLA
                trimmed.contains("व्यापारी") || trimmed.contains("व्यावसायिक") || trimmed.contains("दुकान") -> BUSINESS
                trimmed.contains("राजकीय") -> POLITICAL_PERSON
                trimmed.contains("देणगी") -> DONOR
                trimmed.contains("प्रायोजक") -> SPONSOR
                else -> OTHER
            }
        }

        fun getDisplayLabel(otherTypeStr: String, customCategoryName: String): String {
            val type = fromCodeOrLabel(otherTypeStr)
            return if (type == OTHER && customCategoryName.isNotBlank()) {
                customCategoryName.trim()
            } else {
                type.marathiLabel
            }
        }

        fun getContributionHeading(otherTypeStr: String, customCategoryName: String): String {
            val type = fromCodeOrLabel(otherTypeStr)
            return if (type == OTHER && customCategoryName.isNotBlank()) {
                "${customCategoryName.trim()} योगदान"
            } else {
                type.contributionHeading
            }
        }
    }
}

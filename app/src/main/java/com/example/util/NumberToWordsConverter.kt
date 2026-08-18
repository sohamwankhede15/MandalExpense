package com.example.util

object NumberToWordsConverter {

    private val unitsMarathi = arrayOf(
        "", "एक", "दोन", "तीन", "चार", "पाच", "सहा", "सात", "आठ", "नऊ", "दहा",
        "अकरा", "बारा", "तेरा", "चौदा", "पंधरा", "सोळा", "सतरा", "अठरा", "एकोणीस", "वीस",
        "एकवीस", "बावीस", "तेवीस", "चोवीस", "पंचवीस", "सव्वीस", "सत्तावीस", "अठ्ठावीस", "एकोणतीस", "तीस",
        "एकतीस", "बत्तीस", "तेहेतीस", "चौतीस", "पस्तीस", "छत्तीस", "सदतीस", "अडतीस", "एकोणचाळीस", "चाळीस",
        "एक्केचाळीस", "बेचाळीस", "त्रेचाळीस", "चव्वेचाळीस", "पंचेचाळीस", "शेचाळीस", "सत्तेचाळीस", "अठ्ठेचाळीस", "एकोणपन्नास", "पन्नास",
        "एक्कावन्न", "बावन्न", "त्रेपन्न", "चोपन्न", "पंचावन्न", "छप्पन्न", "सत्तावन्न", "अठ्ठावन्न", "एकोणसाठ", "साठ",
        "एकसष्ठ", "बासष्ठ", "त्रेसष्ठ", "चौसष्ठ", "पासष्ठ", "सहासष्ठ", "सदुसष्ठ", "अडुसष्ठ", "एकोणसत्तर", "सत्तर",
        "एकाहत्तर", "बाहत्तर", "त्र्याहत्तर", "चौर्‍याहत्तर", "पंच्याहत्तर", "शहात्तर", "सत्त्याहत्तर", "अठ्ठ्याहत्तर", "एकोणऐंशी", "ऐंशी",
        "एक्याऐंशी", "ब्याऐंशी", "त्र्याऐंशी", "चौऱ्याऐंशी", "पंच्याऐंशी", "शहाऐंशी", "सत्त्याऐंशी", "अठ्ठ्याऐंशी", "एकोणनव्वद", "नव्वद",
        "एक्याण्णव", "ब्याण्णव", "त्र्याण्णव", "चौऱ्याण्णव", "पंच्याण्णव", "शहाण्णव", "Component", "अठ्ठ्याण्णव", "नव्व्याण्णव"
    ).also {
        it[97] = "सत्त्याण्णव"
    }

    private val unitsEnglish = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    )

    private val tensEnglish = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    fun convertToMarathi(amount: Double): String {
        val longVal = amount.toLong()
        if (longVal == 0L) return "शून्य रुपये फक्त"
        if (longVal < 0) return "उणे " + convertToMarathi(-amount)

        val parts = mutableListOf<String>()
        var num = longVal

        val crore = num / 10000000
        if (crore > 0) {
            parts.add("${convertToMarathiSub(crore)} कोटी")
            num %= 10000000
        }

        val lakh = num / 100000
        if (lakh > 0) {
            parts.add("${convertToMarathiSub(lakh)} लाख")
            num %= 100000
        }

        val thousand = num / 1000
        if (thousand > 0) {
            parts.add("${convertToMarathiSub(thousand)} हजार")
            num %= 1000
        }

        val hundred = num / 100
        if (hundred > 0) {
            parts.add("${convertToMarathiSub(hundred)} शे")
            num %= 100
        }

        if (num > 0) {
            parts.add(convertToMarathiSub(num))
        }

        return parts.joinToString(" ") + " रुपये फक्त"
    }

    private fun convertToMarathiSub(n: Long): String {
        val intN = n.toInt()
        return if (intN in 1..99) {
            unitsMarathi[intN]
        } else if (intN >= 100) {
            convertToMarathi(n.toDouble()).replace(" रुपये फक्त", "")
        } else {
            ""
        }
    }

    fun convertToEnglish(amount: Double): String {
        val longVal = amount.toLong()
        if (longVal == 0L) return "Zero Rupees Only"
        if (longVal < 0) return "Minus " + convertToEnglish(-amount)

        val parts = mutableListOf<String>()
        var num = longVal

        val crore = num / 10000000
        if (crore > 0) {
            parts.add("${convertSubEnglish(crore)} Crore")
            num %= 10000000
        }

        val lakh = num / 100000
        if (lakh > 0) {
            parts.add("${convertSubEnglish(lakh)} Lakh")
            num %= 100000
        }

        val thousand = num / 1000
        if (thousand > 0) {
            parts.add("${convertSubEnglish(thousand)} Thousand")
            num %= 1000
        }

        val hundred = num / 100
        if (hundred > 0) {
            parts.add("${convertSubEnglish(hundred)} Hundred")
            num %= 100
        }

        if (num > 0) {
            parts.add(convertSubEnglish(num))
        }

        return parts.joinToString(" ") + " Rupees Only"
    }

    private fun convertSubEnglish(n: Long): String {
        var num = n.toInt()
        val result = StringBuilder()
        if (num >= 100) {
            result.append(unitsEnglish[num / 100]).append(" Hundred ")
            num %= 100
        }
        if (num in 1..19) {
            result.append(unitsEnglish[num])
        } else if (num >= 20) {
            result.append(tensEnglish[num / 10])
            if (num % 10 > 0) {
                result.append(" ").append(unitsEnglish[num % 10])
            }
        }
        return result.toString().trim()
    }
}

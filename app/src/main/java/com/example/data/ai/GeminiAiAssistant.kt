package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.ExpenseTransaction
import com.example.data.model.MandalSettings
import com.example.data.model.VarganiTransaction
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiAiAssistant {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val service = retrofit.create(GeminiApiService::class.java)

    suspend fun askAssistant(
        prompt: String,
        settings: MandalSettings,
        varganiList: List<VarganiTransaction>,
        expenseList: List<ExpenseTransaction>,
        language: String = "mr" // "mr" for Marathi, "en" for English
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext if (language == "mr") {
                "⚠️ कृपया AI सहाय्यक वापरण्यासाठी Settings मधून अथवा Secrets पॅनेलमधून वैध Gemini API Key प्रविष्ट करा."
            } else {
                "⚠️ Please provide a valid Gemini API Key in the AI Studio Secrets panel or Settings to use AI Assistant."
            }
        }

        val totalVargani = varganiList.sumOf { it.amount }
        val totalExpenses = expenseList.sumOf { it.amount }
        val balance = totalVargani - totalExpenses

        val mandalContext = """
            Mandal Name: ${settings.mandalName}
            Location: ${settings.address}
            Year: ${settings.festivalYear}
            Total Vargani Collected: ₹$totalVargani (${varganiList.size} receipts)
            Total Expenses: ₹$totalExpenses (${expenseList.size} vouchers)
            Current Balance: ₹$balance
            Key Expense Categories: ${expenseList.groupBy { it.category }.map { "${it.key}: ₹${it.value.sumOf { e -> e.amount }}" }.joinToString(", ")}
        """.trimIndent()

        val systemPrompt = if (language == "mr") {
            """
                तुम्ही एका गणेश उत्सव मंडळाचे (Ganesh Utsav Mandal) अनुभवी आर्थिक व सांस्कृतिक सल्लागार (Mandal Advisor AI) आहात.
                तुमचे काम मंडळाच्या कार्यकर्त्यांना ताळेबंद, हिशोब विश्लेषण, खर्चात बचत करण्याच्या टिप्स, बैठकीचे भाषण (Meeting Speech), व्हॉट्सअॅप मेसेज, विसर्जन मिरवणूक नियोजन आणि नियमावली यावर मराठी भाषेत आदरपूर्वक, स्पष्ट आणि नेमकी मदत करणे हे आहे.
                
                मंडळाचा चालू डेटा:
                $mandalContext
                
                नेहमी सस्नेह, आदरयुक्त व गणेशोत्सवाच्या परंपरेला साजेसा प्रतिसाद द्या.
            """.trimIndent()
        } else {
            """
                You are an expert cultural & financial advisor for an Indian Festival Organization / Ganesh Utsav Mandal.
                Help the mandal committee members with accounting audits, cost savings, committee meeting agendas, WhatsApp announcements, and event management.
                
                Current Mandal Data:
                $mandalContext
                
                Provide clear, structured, respectful answers.
            """.trimIndent()
        }

        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt))
                )
            )

            val response = service.generateContent(apiKey = apiKey, request = request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: if (language == "mr") "माहिती प्राप्त होऊ शकली नाही. कृपया पुन्हा प्रयत्न करा." else "No response generated. Please try again."
        } catch (e: Exception) {
            if (language == "mr") {
                "त्रुटी (Error): ${e.localizedMessage ?: "इंटरनेट किंवा API कनेक्शन तपासणी करा."}"
            } else {
                "Error: ${e.localizedMessage ?: "Failed to connect to AI server."}"
            }
        }
    }
}

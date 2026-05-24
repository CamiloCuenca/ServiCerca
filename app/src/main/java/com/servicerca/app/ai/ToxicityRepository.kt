package com.servicerca.app.ai

import android.util.Log
import com.servicerca.app.BuildConfig

object ToxicityRepository {
    private const val API_KEY = BuildConfig.PERSPECTIVE_API_KEY

    suspend fun isToxic(text: String): Boolean {
        Log.d("ToxicityRepository", "Checking toxicity for: $text")
        if (text.isBlank()) return false
        
        return try {
            val request = PerspectiveRequest(
                comment = CommentText(text = text),
                requestedAttributes = RequestedAttributes(),
            )
            val response = RetrofitInstance.api.analyzeComment(API_KEY, request)
            
            val scores = response.attributeScores
            val toxicity = scores.TOXICITY.summaryScore.value
            val severeToxicity = scores.SEVERE_TOXICITY.summaryScore.value
            val insult = scores.INSULT.summaryScore.value
            val profanity = scores.PROFANITY.summaryScore.value
            
            Log.d("ToxicityRepository", """
                Text: $text
                Scores -> Toxicity: $toxicity, Severe: $severeToxicity, Insult: $insult, Profanity: $profanity
            """.trimIndent())

            // Umbrales más estrictos:
            // 0.6 para toxicidad general
            // 0.5 para insultos o lenguaje vulgar (profanity)
            (toxicity > 0.6 || severeToxicity > 0.5 || insult > 0.5 || profanity > 0.5)
            
        } catch (e: Exception) {
            Log.e("ToxicityRepository", "Error analyzing toxicity", e)
            false
        }
    }
}

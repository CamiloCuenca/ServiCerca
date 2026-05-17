package com.servicerca.app.ai

data class PerspectiveResponse(
    val attributeScores: AttributeScores
)

data class AttributeScores(
    val TOXICITY: ToxicityScore,
    val SEVERE_TOXICITY: ToxicityScore,
    val INSULT: ToxicityScore,
    val PROFANITY: ToxicityScore
)

data class ToxicityScore(
    val summaryScore: SummaryScore
)

data class SummaryScore(
    val value: Double,
    val type: String
)

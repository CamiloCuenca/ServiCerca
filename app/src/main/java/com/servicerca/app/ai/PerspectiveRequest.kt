package com.servicerca.app.ai

data class PerspectiveRequest(
    val comment: CommentText,
    val requestedAttributes: RequestedAttributes,
    val languages: List<String> = listOf("es", "en")
)

data class CommentText(
    val text: String
)

data class RequestedAttributes(
    val TOXICITY: ToxicityAttribute = ToxicityAttribute(),
    val SEVERE_TOXICITY: ToxicityAttribute = ToxicityAttribute(),
    val INSULT: ToxicityAttribute = ToxicityAttribute(),
    val PROFANITY: ToxicityAttribute = ToxicityAttribute()
)

data class ToxicityAttribute(
    val scoreType: String = "PROBABILITY",
    val scoreThreshold: Double = 0.0
)

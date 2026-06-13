package com.example.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<MoshiContent>,
    val generationConfig: MoshiGenerationConfig? = null,
    val systemInstruction: MoshiContent? = null
)

@JsonClass(generateAdapter = true)
data class MoshiContent(
    val parts: List<MoshiPart>
)

@JsonClass(generateAdapter = true)
data class MoshiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class MoshiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<MoshiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class MoshiCandidate(
    val content: MoshiContent? = null
)

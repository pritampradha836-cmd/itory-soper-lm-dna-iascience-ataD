package com.example.data

import com.example.BuildConfig
import com.example.data.local.DataScienceDao
import com.example.data.model.ModelSession
import com.example.data.model.SavedNote
import com.example.data.network.GeminiRequest
import com.example.data.network.MoshiContent
import com.example.data.network.MoshiPart
import com.example.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DataScienceRepository(private val dao: DataScienceDao) {

    val allNotes: Flow<List<SavedNote>> = dao.getAllNotes()
    val allSessions: Flow<List<ModelSession>> = dao.getAllSessions()

    suspend fun insertNote(note: SavedNote) = withContext(Dispatchers.IO) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(note: SavedNote) = withContext(Dispatchers.IO) {
        dao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteNoteById(id)
    }

    suspend fun insertSession(session: ModelSession) = withContext(Dispatchers.IO) {
        dao.insertSession(session)
    }

    suspend fun clearSessions() = withContext(Dispatchers.IO) {
        dao.clearAllSessions()
    }

    /**
     * Calls Gemini 3.5 Flash Model using direct REST API.
     */
    suspend fun consultAI(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Gemini API Key is missing. Please enter your GEMINI_API_KEY into the Secrets panel in AI Studio."
        }

        val contents = listOf(MoshiContent(parts = listOf(MoshiPart(text = prompt))))
        val systemContent = systemInstruction?.let {
            MoshiContent(parts = listOf(MoshiPart(text = it)))
        }

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = systemContent
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No insights generated. Try detailing your variables or dataset."
        } catch (e: Exception) {
            "Error: ${e.message ?: "Failed to generate AI content"}"
        }
    }
}

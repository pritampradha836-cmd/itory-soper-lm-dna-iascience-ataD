package com.example.data.local

import androidx.room.*
import com.example.data.model.ModelSession
import com.example.data.model.SavedNote
import kotlinx.coroutines.flow.Flow

@Dao
interface DataScienceDao {
    // --- Saved Notes queries ---
    @Query("SELECT * FROM saved_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<SavedNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SavedNote)

    @Delete
    suspend fun deleteNote(note: SavedNote)

    @Query("DELETE FROM saved_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    // --- Model Sessions queries ---
    @Query("SELECT * FROM model_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ModelSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ModelSession)

    @Delete
    suspend fun deleteSession(session: ModelSession)

    @Query("DELETE FROM model_sessions")
    suspend fun clearAllSessions()
}

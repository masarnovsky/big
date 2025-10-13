package com.masarnovsky.big.mvvm.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {
    @Insert // LLM: why do we need separated annotation for the insert?. What suspend do?
    suspend fun insert(text: TextEntity)

    @Query("SELECT * FROM text_history ORDER BY timestamp DESC")
    fun getAllTexts(): Flow<List<TextEntity>> // LLM: explain Flow class

    @Query("DELETE FROM text_history WHERE id = :id")
    suspend fun delete(id: Int)
}
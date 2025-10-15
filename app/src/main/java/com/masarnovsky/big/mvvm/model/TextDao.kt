package com.masarnovsky.big.mvvm.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {
    @Insert
    suspend fun insert(text: TextEntity)

    @Query("SELECT * FROM text_history ORDER BY timestamp DESC")
    fun getAllTexts(): Flow<List<TextEntity>>

    @Delete
    suspend fun delete(textEntity: TextEntity)
}
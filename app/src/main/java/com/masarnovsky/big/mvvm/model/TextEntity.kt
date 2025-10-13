package com.masarnovsky.big.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "text_history")
data class TextEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
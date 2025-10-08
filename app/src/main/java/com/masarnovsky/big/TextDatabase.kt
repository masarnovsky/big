package com.masarnovsky.big

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TextEntity::class], version = 1, exportSchema = false)
abstract class TextDatabase : RoomDatabase() {
    abstract fun textDao(): TextDao

    companion object {
        @Volatile // LLM: what volatile do?
        private var INSTANCE: TextDatabase? = null

        fun getDatabase(context: Context): TextDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TextDatabase::class.java,
                    "text_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
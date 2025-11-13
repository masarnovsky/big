package com.masarnovsky.big.di

import android.content.Context
import com.masarnovsky.big.mvvm.model.TextDao
import com.masarnovsky.big.mvvm.model.TextDatabase
import com.masarnovsky.big.mvvm.model.TextRepository
import com.masarnovsky.big.mvvm.model.UserPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides application-level dependencies.
 * All dependencies provided here are singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the Room database instance.
     * @param context Application context
     * @return TextDatabase singleton instance
     */
    @Provides
    @Singleton
    fun provideTextDatabase(@ApplicationContext context: Context): TextDatabase {
        return TextDatabase.getDatabase(context)
    }

    /**
     * Provides the TextDao from the database.
     * @param database The TextDatabase instance
     * @return TextDao for database operations
     */
    @Provides
    @Singleton
    fun provideTextDao(database: TextDatabase): TextDao {
        return database.textDao()
    }

    /**
     * Provides the TextRepository for data operations.
     * @param textDao The DAO for database operations
     * @return TextRepository singleton instance
     */
    @Provides
    @Singleton
    fun provideTextRepository(textDao: TextDao): TextRepository {
        return TextRepository(textDao)
    }

    /**
     * Provides the UserPreferencesManager for app preferences.
     * @param context Application context
     * @return UserPreferencesManager singleton instance
     */
    @Provides
    @Singleton
    fun provideUserPreferencesManager(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferencesManager(context)
    }
}

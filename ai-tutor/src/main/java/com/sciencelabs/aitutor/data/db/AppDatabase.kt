package com.sciencelabs.aitutor.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The main Room Database for the AI Tutor.
 * Contains tables for Experiments and Messages.
 */
@Database(
    entities = [
        ExperimentEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun experimentDao(): ExperimentDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Get or create a singleton instance of the database.
         * Uses a double-checked lock pattern for thread safety.
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "science_tutor_db"
                )
                    .fallbackToDestructiveMigration() // For simplicity; use migrations in production
                    .build()
                    .also { instance = it }
            }
        }
    }
}

package com.example.walletapp.auth.di

// com.example.walletapp.core.di/DatabaseModule.kt (Yangi modul)

import android.content.Context
import androidx.room.Room
import com.example.walletapp.auth.data.local.AppDatabase
import com.example.walletapp.auth.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "expense_tracker_db"
        ).fallbackToDestructiveMigration().build()
    }
    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}
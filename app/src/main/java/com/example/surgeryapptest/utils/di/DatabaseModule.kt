package com.example.surgeryapptest.utils.di

import android.content.Context
import androidx.room.Room
import com.example.surgeryapptest.utils.constant.Constants.Companion.DATABASE
import com.example.surgeryapptest.utils.database.ProgressBookDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        ProgressBookDatabase::class.java,
        DATABASE
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: ProgressBookDatabase) = database.progressBookDao()

    //TODO: Should I add the singleton here as well?
    @Singleton
    @Provides
    fun provideToDoDao(database: ProgressBookDatabase) = database.toDoDao()


}
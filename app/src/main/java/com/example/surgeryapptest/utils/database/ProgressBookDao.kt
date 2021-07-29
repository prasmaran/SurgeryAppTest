package com.example.surgeryapptest.utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressBook(progressBookEntity: ProgressBookEntity)

    @Query("SELECT * FROM progressBook_table ORDER BY id ASC")
    fun readDatabase(): Flow<List<ProgressBookEntity>>

}
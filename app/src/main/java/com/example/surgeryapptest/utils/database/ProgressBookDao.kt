package com.example.surgeryapptest.utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressBookDao {

    // suspend keyword removed --? temp solution
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProgressBook(progressBookEntity: ProgressBookEntity)

    @Query("SELECT * FROM progressBook_table ORDER BY id ASC")
    fun readDatabase(): Flow<List<ProgressBookEntity>>

    // new query for search filer in the progress book fragment
    @Query("SELECT * FROM progressBook_table WHERE progressBook LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<ProgressBookEntity>>

}
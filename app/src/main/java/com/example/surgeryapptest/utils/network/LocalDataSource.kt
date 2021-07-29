package com.example.surgeryapptest.utils.network

import com.example.surgeryapptest.utils.database.ProgressBookDao
import com.example.surgeryapptest.utils.database.ProgressBookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val progressBookDao: ProgressBookDao
) {

    fun readDatabase(): Flow<List<ProgressBookEntity>> {
        return progressBookDao.readDatabase()
    }

    suspend fun insertProgressBook(progressBookEntity: ProgressBookEntity) {
        progressBookDao.insertProgressBook(progressBookEntity)
    }

}
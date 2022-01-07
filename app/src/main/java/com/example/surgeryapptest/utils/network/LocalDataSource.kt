package com.example.surgeryapptest.utils.network

import androidx.lifecycle.LiveData
import com.example.surgeryapptest.utils.database.ProgressBookDao
import com.example.surgeryapptest.utils.database.ProgressBookEntity
import com.example.surgeryapptest.utils.database.ToDoDao
import com.example.surgeryapptest.utils.database.ToDoData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val progressBookDao: ProgressBookDao,
    private val toDoDao: ToDoDao
) {

    fun readDatabase(): Flow<List<ProgressBookEntity>> {
        return progressBookDao.readDatabase()
    }

    suspend fun insertProgressBook(progressBookEntity: ProgressBookEntity) {
        progressBookDao.insertProgressBook(progressBookEntity)
    }

//    fun searchDatabase(searchQuery: String) : Flow<List<ProgressBookEntity>> {
//        return progressBookDao.searchDatabase(searchQuery)
//    }

    /**
     * To do dao functions
     */

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()
    val sortByHighPriority: LiveData<List<ToDoData>> = toDoDao.sortByHighPriority()
    val sortByLowPriority: LiveData<List<ToDoData>> = toDoDao.sortByLowPriority()

    suspend fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteItem(toDoData: ToDoData){
        toDoDao.deleteItem(toDoData)
    }

    suspend fun deleteAll(){
        toDoDao.deleteAll()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>> {
        return toDoDao.searchDatabase(searchQuery)
    }

}
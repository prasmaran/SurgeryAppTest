package com.example.surgeryapptest.utils.network

import com.example.surgeryapptest.model.network.AllProgressBookEntry
import com.example.surgeryapptest.utils.network.endpoints.ApiInterface
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun getAllProgressEntry() : Response<AllProgressBookEntry> {
        return apiInterface.getAllProgressEntry()
    }

}
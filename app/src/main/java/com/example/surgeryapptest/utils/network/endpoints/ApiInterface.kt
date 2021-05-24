package com.example.surgeryapptest.utils.network.endpoints

import com.example.surgeryapptest.model.network.AllProgressBookEntry
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    // To receive all the progress entry data
    @GET("/books/progress")
    suspend fun getAllProgressEntry() : Response<AllProgressBookEntry>

}
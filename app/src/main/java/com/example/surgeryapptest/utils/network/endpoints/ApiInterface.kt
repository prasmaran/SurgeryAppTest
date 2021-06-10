package com.example.surgeryapptest.utils.network.endpoints

import com.example.surgeryapptest.model.network.AllProgressBookEntry
import com.example.surgeryapptest.model.network.AllProgressBookEntryItem
import com.example.surgeryapptest.model.network.NetworkUploadNewEntryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    // To receive all the progress entry data
    @GET("/books/progress")
    suspend fun getAllProgressEntry() : Response<AllProgressBookEntry>

    // To receive one progress entry by ID
    @GET("/books/progress/{entryID}")
    suspend fun getOneProgressEntry() : Response<AllProgressBookEntryItem>

    // To upload new wound image
    @Multipart
    @POST("/upload/newform")
    suspend fun uploadNewEntry(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("painrate") painrate: RequestBody,
        @Part("fluid_drain") fluid_drain: RequestBody
    ) : Response<NetworkUploadNewEntryResponse>


}
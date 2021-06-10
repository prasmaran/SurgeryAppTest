package com.example.surgeryapptest.utils.network

import com.example.surgeryapptest.model.network.AllProgressBookEntry
import com.example.surgeryapptest.model.network.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.utils.network.endpoints.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun getAllProgressEntry() : Response<AllProgressBookEntry> {
        return apiInterface.getAllProgressEntry()
    }

    suspend fun uploadNewEntry(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("painrate") painrate: RequestBody,
        @Part("fluid_drain") fluid_drain: RequestBody
    ) : Response<NetworkUploadNewEntryResponse> {
        return apiInterface.uploadNewEntry(image, description, painrate, fluid_drain)
    }

}
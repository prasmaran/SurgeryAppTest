package com.example.surgeryapptest.utils.network

import com.example.surgeryapptest.model.network.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.model.network.uploadNewImageResponse.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.utils.network.endpoints.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun getAllProgressEntry(): Response<AllProgressBookEntry> {
        return apiInterface.getAllProgressEntry()
    }

    suspend fun uploadNewEntry(
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("fluid_drain") fluid_drain: RequestBody,
        @Part("painrate") painrate: RequestBody,
        @Part("redness") redness: RequestBody,
        @Part("swelling") swelling: RequestBody,
        @Part("odour") odour: RequestBody,
        @Part("fever") fever: RequestBody,
    ): Response<NetworkUploadNewEntryResponse> {
        return apiInterface.uploadNewEntry(
            image, title, description, fluid_drain, painrate,
            redness, swelling, odour, fever
        )
    }

    suspend fun updateUploadedEntry(
        @Part("entryID") entryID: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("fluid_drain") fluid_drain: RequestBody,
        @Part("painrate") painrate: RequestBody,
        @Part("redness") redness: RequestBody,
        @Part("swelling") swelling: RequestBody,
        @Part("odour") odour: RequestBody,
        @Part("fever") fever: RequestBody,
    ): Response<NetworkUpdateEntryResponse> {
        return apiInterface.updateUploadedEntry(
            entryID, title, description, fluid_drain, painrate,
            redness, swelling, odour, fever
        )
    }

    suspend fun deleteUploadedEntry(
        @Part("entryID") entryID: RequestBody
    ) : Response<NetworkDeleteEntryResponse> {
        return apiInterface.deleteUploadedEntry(entryID)
    }



}
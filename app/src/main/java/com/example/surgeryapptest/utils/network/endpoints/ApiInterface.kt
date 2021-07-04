package com.example.surgeryapptest.utils.network.endpoints

import com.example.surgeryapptest.model.network.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.model.network.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.model.network.uploadNewImageResponse.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.model.network.userNetworkResponse.UserLoginNetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    // To receive all the progress entry data
    @GET("/books/progress/getAll")
    suspend fun getAllProgressEntry() : Response<AllProgressBookEntry>

    // To receive one progress entry by ID
    @GET("/books/progress/{entryid}")
    suspend fun getOneProgressEntry() : Response<AllProgressBookEntryItem>

    // To upload new wound image
    @Multipart
    @POST("/books/progress/upload")
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
    ) : Response<NetworkUploadNewEntryResponse>

    // To edit selected image entry
    @Multipart
    @PUT("/books/progress/edit")
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
    ) : Response<NetworkUpdateEntryResponse>

    // Delete selected image entry
    @Multipart
    @PUT("/books/progress/delete")
    suspend fun deleteUploadedEntry(
        @Part("entryID") entryID: RequestBody
    ) : Response<NetworkDeleteEntryResponse>

    // Authenticate the user
    @POST("/user/auth")
    suspend fun loginUser(@Body params: Map<String, String>):
            Response<UserLoginNetworkResponse>
}
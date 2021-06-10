package com.example.surgeryapptest.model.network

import com.google.gson.annotations.SerializedName

data class NetworkUploadNewEntryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)
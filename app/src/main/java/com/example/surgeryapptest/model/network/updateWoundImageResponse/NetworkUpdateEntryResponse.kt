package com.example.surgeryapptest.model.network.updateWoundImageResponse


import com.google.gson.annotations.SerializedName

data class NetworkUpdateEntryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)
package com.example.surgeryapptest.model.network.deleteEntryNetworkResponse


import com.google.gson.annotations.SerializedName

data class NetworkDeleteEntryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)
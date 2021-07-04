package com.example.surgeryapptest.model.network.userNetworkResponse


import com.google.gson.annotations.SerializedName

data class UserLoginNetworkResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: List<Result>?,
    @SerializedName("success")
    val success: Boolean
)
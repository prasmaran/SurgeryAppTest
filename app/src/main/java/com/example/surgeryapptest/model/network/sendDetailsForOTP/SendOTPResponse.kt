package com.example.surgeryapptest.model.network.sendDetailsForOTP


import com.google.gson.annotations.SerializedName

data class SendOTPResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: String,
    @SerializedName("success")
    val success: Boolean
)
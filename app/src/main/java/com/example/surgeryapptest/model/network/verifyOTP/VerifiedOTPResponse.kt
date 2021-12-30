package com.example.surgeryapptest.model.network.verifyOTP


import com.google.gson.annotations.SerializedName

data class VerifiedOTPResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)
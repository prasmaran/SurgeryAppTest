package com.example.surgeryapptest.model.network.updateDetails


import com.google.gson.annotations.SerializedName

data class UpdateDetailResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)
package com.example.surgeryapptest.model.network.utilsResponse


import com.google.gson.annotations.SerializedName

data class GeneralInfoResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: ArrayList<GeneralInfoList>,
    @SerializedName("success")
    val success: Boolean
)
package com.example.surgeryapptest.model.network.pdfGenerationResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NetworkPDFGenerateResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: String,
    @SerializedName("success")
    val success: Boolean
) : Parcelable
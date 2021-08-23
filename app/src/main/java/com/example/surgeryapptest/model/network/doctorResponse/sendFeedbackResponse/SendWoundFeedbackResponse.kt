package com.example.surgeryapptest.model.network.doctorResponse.sendFeedbackResponse


import com.google.gson.annotations.SerializedName

data class SendWoundFeedbackResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
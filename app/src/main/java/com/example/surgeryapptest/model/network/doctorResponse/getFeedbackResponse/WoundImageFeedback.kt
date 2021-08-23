package com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse


import com.google.gson.annotations.SerializedName

data class WoundImageFeedback(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<FeedbackList>
)
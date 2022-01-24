package com.example.surgeryapptest.model.network.appointmentResponse


import com.google.gson.annotations.SerializedName

data class AppointmentNetworkResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("success")
    val success: Boolean
)
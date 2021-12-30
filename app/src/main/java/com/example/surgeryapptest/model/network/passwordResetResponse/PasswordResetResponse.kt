package com.example.surgeryapptest.model.network.passwordResetResponse


import com.google.gson.annotations.SerializedName

data class PasswordResetResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)
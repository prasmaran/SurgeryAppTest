package com.example.surgeryapptest.model.network.verifyOTP


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("accountSid")
    val accountSid: String,
    @SerializedName("amount")
    val amount: Any,
    @SerializedName("channel")
    val channel: String,
    @SerializedName("dateCreated")
    val dateCreated: String,
    @SerializedName("dateUpdated")
    val dateUpdated: String,
    @SerializedName("payee")
    val payee: Any,
    @SerializedName("serviceSid")
    val serviceSid: String,
    @SerializedName("sid")
    val sid: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("valid")
    val valid: Boolean
)
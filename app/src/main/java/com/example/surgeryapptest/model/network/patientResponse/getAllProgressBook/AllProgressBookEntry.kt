package com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook


import com.google.gson.annotations.SerializedName

data class AllProgressBookEntry(

    @SerializedName("success")
    val success: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: ArrayList<AllProgressBookEntryItem>?

)

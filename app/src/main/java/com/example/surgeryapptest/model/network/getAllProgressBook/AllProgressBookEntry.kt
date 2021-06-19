package com.example.surgeryapptest.model.network.getAllProgressBook


import com.google.gson.annotations.SerializedName

data class AllProgressBookEntry(
    @SerializedName("result")
    val result: ArrayList<AllProgressBookEntryItem>
)
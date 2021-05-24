package com.example.surgeryapptest.model.network


import com.google.gson.annotations.SerializedName

data class AllProgressBookEntry(
    @SerializedName("results")
    val result: ArrayList<AllProgressBookEntryItem>
)
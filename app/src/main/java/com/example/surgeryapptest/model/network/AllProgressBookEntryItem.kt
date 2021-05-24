package com.example.surgeryapptest.model.network


import com.google.gson.annotations.SerializedName

data class  AllProgressBookEntryItem(
    @SerializedName("entryID")
    val entryID: Int,
    @SerializedName("progressImage")
    val progressImage: String,
    @SerializedName("progressTitle")
    val progressTitle: String,
    @SerializedName("progressDescription")
    val progressDescription: String,
    @SerializedName("dateCreated")
    val dateCreated: String,
    @SerializedName("doctorAssigned")
    val doctorAssigned: String,
)
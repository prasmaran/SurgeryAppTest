package com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllProgressBookEntryItem(
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
    @SerializedName("quesFluid")
    val quesFluid: String,
    @SerializedName("quesPain")
    val quesPain: String,
    @SerializedName("quesRedness")
    val quesRedness: String,
    @SerializedName("quesSwelling")
    val quesSwelling: String,
    @SerializedName("quesOdour")
    val quesOdour: String,
    @SerializedName("quesFever")
    val quesFever: String,
    @SerializedName("doctorAssigned")
    val doctorAssigned: String
) : Parcelable
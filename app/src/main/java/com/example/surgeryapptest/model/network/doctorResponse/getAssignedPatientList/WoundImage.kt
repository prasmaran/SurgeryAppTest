package com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WoundImage(
    @SerializedName("dateCreated")
    val dateCreated: String,
    @SerializedName("doctorAssigned")
    val doctorAssigned: Int,
    @SerializedName("entryID")
    val entryID: Int,
    @SerializedName("flag")
    val flag: Int,
    @SerializedName("m_ic")
    val mIc: String,
    @SerializedName("m_name")
    val mName: String,
    @SerializedName("masterUserId_fk")
    val masterUserIdFk: Int,
    @SerializedName("progressDescription")
    val progressDescription: String,
    @SerializedName("progressImage")
    val progressImage: String,
    @SerializedName("progressTitle")
    val progressTitle: String,
    @SerializedName("quesFever")
    val quesFever: String,
    @SerializedName("quesFluid")
    val quesFluid: String,
    @SerializedName("quesOdour")
    val quesOdour: String,
    @SerializedName("quesPain")
    val quesPain: String,
    @SerializedName("quesRedness")
    val quesRedness: String,
    @SerializedName("quesSwelling")
    val quesSwelling: String
) : Parcelable
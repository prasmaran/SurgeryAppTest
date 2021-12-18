package com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AssignedPatientsList(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: ArrayList<PatientName>
) : Parcelable
package com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PatientName(
    @SerializedName("patient_id")
    val patientId: String,
    @SerializedName("wound_images")
    val woundImages: ArrayList<WoundImage>
) : Parcelable
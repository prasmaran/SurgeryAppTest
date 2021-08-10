package com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList


import com.google.gson.annotations.SerializedName

data class AssignedPatientsList(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: ArrayList<PatientName>
)
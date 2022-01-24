package com.example.surgeryapptest.model.network.appointmentResponse


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("a_id")
    val aId: Int,
    @SerializedName("appointment_date")
    val appointmentDate: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("doctor_id")
    val doctorId: Int,
    @SerializedName("with_name")
    val withName: String,
    @SerializedName("patient_id")
    val patientId: Int,
    @SerializedName("m_ic")
    val mIc: String,
)



package com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedbackList(
    @SerializedName("f_id")
    val fId: Int,
    @SerializedName("progress_entry_id")
    val progressEntryId: Int,
    @SerializedName("feedback_text")
    val feedbackText: String,
    @SerializedName("doctor_inCharge")
    val doctorInCharge: Int,
    @SerializedName("flag")
    val flag: Int,
    @SerializedName("dateCreated")
    val dateCreated: String
) : Parcelable
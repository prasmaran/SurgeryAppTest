package com.example.surgeryapptest.model.network.userNetworkResponse


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("flag")
    val flag: Int,
    @SerializedName("m_gender")
    val mGender: String,
    @SerializedName("m_ic")
    val mIc: String,
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("m_name")
    val mName: String,
    @SerializedName("m_type")
    val mType: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("m_contact1")
    val mContact1: String,
    @SerializedName("m_contact2")
    val mContact2: String,

)
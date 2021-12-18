package com.example.surgeryapptest.model.network.utilsResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeneralInfoList(
    @SerializedName("article_id")
    val articleId: Int,
    @SerializedName("article_link")
    val articleLink: String,
    @SerializedName("article_title")
    val articleTitle: String,
    @SerializedName("date_created")
    val dateCreated: String
) : Parcelable
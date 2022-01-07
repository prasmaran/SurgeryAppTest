package com.example.surgeryapptest.utils.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.surgeryapptest.utils.constant.Constants.Companion.TO_DO_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = TO_DO_TABLE)
@Parcelize
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String
): Parcelable
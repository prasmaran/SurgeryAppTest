package com.example.surgeryapptest.utils.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.utils.constant.Constants.Companion.PROGRESS_BOOK_TABLE

@Entity(tableName = PROGRESS_BOOK_TABLE)
class ProgressBookEntity(
    var progressBook: AllProgressBookEntry
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
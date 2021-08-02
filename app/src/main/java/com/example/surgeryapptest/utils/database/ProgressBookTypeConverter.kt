package com.example.surgeryapptest.utils.database

import androidx.room.TypeConverter
import com.example.surgeryapptest.model.network.getAllProgressBook.AllProgressBookEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProgressBookTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun progressBookToString(progressBookEntry: AllProgressBookEntry): String{
        return gson.toJson(progressBookEntry)
    }

    @TypeConverter
    fun stringToProgressBook(data: String): AllProgressBookEntry{
        val listType = object : TypeToken<AllProgressBookEntry>() {}.type
        return gson.fromJson(data, listType)
    }


}
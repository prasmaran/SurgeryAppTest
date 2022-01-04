package com.example.surgeryapptest.utils.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ProgressBookEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(ProgressBookTypeConverter::class)
abstract class ProgressBookDatabase : RoomDatabase(){

    abstract fun progressBookDao(): ProgressBookDao

}
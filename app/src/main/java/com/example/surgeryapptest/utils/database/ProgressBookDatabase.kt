package com.example.surgeryapptest.utils.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 2,
    entities = [ProgressBookEntity::class, ToDoData::class],
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    exportSchema = true
)
@TypeConverters(ProgressBookTypeConverter::class)
abstract class ProgressBookDatabase : RoomDatabase(){

    abstract fun progressBookDao(): ProgressBookDao

    abstract fun toDoDao(): ToDoDao

}
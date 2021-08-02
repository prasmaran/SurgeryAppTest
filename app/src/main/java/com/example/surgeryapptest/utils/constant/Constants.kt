package com.example.surgeryapptest.utils.constant

class Constants {

    companion object{

        const val BASE_URL = "http://192.168.1.107:5000" // 192.168.1.107
        const val BASE_URL2 = "http://192.168.43.119:5000"

        // Database
        const val DATABASE = "progressBook_database"
        const val PROGRESS_BOOK_TABLE = "progressBook_table"

        // Error
        const val NETWORK_ERROR_NO_INTERNET = "Network Error"
        const val UNAUTHENTICATED_USER = "Authentication Error"
        const val NO_PROGRESS_BOOK = "No Progress Book Found"

        // Preferences
        const val PREFERENCES_NAME = "my_settings"
        const val USER_NAME = "user_name"
        const val USER_ID = "user_id"
        const val USER_IC_NUMBER = "user_ic_number"
        const val USER_TYPE = "user_type"
        const val USER_CONTACT_1 = "user_contact_1"
        const val USER_CONTACT_2 = "user_contact_2"
        const val USER_ACCESS_TOKEN = "user_access_token"
        const val USER_GENDER = "user_gender"
        const val NOT_AVAILABLE = "NOT AVAILABLE"
        const val PREFERENCES_BACK_ONLINE = "backOnline"
        const val USER_LOGGED_IN = "userLoggedIn"

        // User Roles
        const val ADMIN = "ADMIN"
        const val PATIENT = "PATIENT"
        const val DOCTOR = "DOCTOR"
        const val RESEARCHER = "RESEARCHER"
        const val DOP = "PATHOLOGY"

    }
}
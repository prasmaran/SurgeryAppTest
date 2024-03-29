package com.example.surgeryapptest.utils.constant

class Constants {

    companion object{

        const val BASE_URL = "https://cute-ruby-cobra-veil.cyclic.app"
        //const val BASE_URL = "https://surgery-node-app-fyp.herokuapp.com"
        const val BASE_URL_BACKUP = "https://crucialtechno.educationhost.cloud"
        const val BASE_URL_LOCALHOST = "http://192.168.1.104:5000"

        // Database
        const val DATABASE = "progressBook_database"
        const val PROGRESS_BOOK_TABLE = "progressBook_table"
        const val TO_DO_TABLE = "to_do_table"

        // Error
        const val NETWORK_ERROR_NO_INTERNET = "Network Error"
        const val UNAUTHENTICATED_USER = "Authentication Error"
        const val NO_PROGRESS_BOOK = "No Progress Book Found"
        const val NO_ARCHIVE_BOOK = "No Archive Book Found"

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
        const val NOT_AVAILABLE = "N/A"
        const val PREFERENCES_BACK_ONLINE = "backOnline"
        const val USER_LOGGED_IN = "userLoggedIn"
        const val NO_OF_PHOTOS = "patientNumberOfPhotos"
        const val PATIENT_LIST_NAME = "patientListName"

        // User Roles
        const val ADMIN = "ADMIN"
        const val PATIENT = "PATIENT"
        const val DOCTOR = "DOCTOR"
        const val RESEARCHER = "RESEARCHER"
        const val DOP = "PATHOLOGY"

    }
}
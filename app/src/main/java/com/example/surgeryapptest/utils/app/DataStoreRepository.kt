package com.example.surgeryapptest.utils.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.surgeryapptest.utils.constant.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val userName = stringPreferencesKey(Constants.USER_NAME)
        val userID = stringPreferencesKey(Constants.USER_ID)
        val userIcNumber = stringPreferencesKey(Constants.USER_IC_NUMBER)
        val userType = stringPreferencesKey(Constants.USER_TYPE)
        val userGender = stringPreferencesKey(Constants.USER_GENDER)
        val userAccessToken = stringPreferencesKey(Constants.USER_ACCESS_TOKEN)
        val userContact1 = stringPreferencesKey(Constants.USER_CONTACT_1)
        val userContact2 = stringPreferencesKey(Constants.USER_CONTACT_2)
        val backOnline = booleanPreferencesKey(Constants.PREFERENCES_BACK_ONLINE)
        val userLoggedIn = booleanPreferencesKey(Constants.USER_LOGGED_IN)
        val patientNumberOfPhotos = intPreferencesKey(Constants.NO_OF_PHOTOS)
        val patientListName = stringPreferencesKey(Constants.PATIENT_LIST_NAME)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    // Functions to save the preferences

    suspend fun savePatientNameList(nameList : String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.patientListName] = nameList
        }
    }

    val readPatientNameList: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val patientNameList = preferences[PreferenceKeys.patientListName] ?: ""
            patientNameList
        }

    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline
        }

    suspend fun saveUserProfileDetails(
        userName: String,
        userID: String,
        userIcNumber: String,
        userGender: String,
        userType: String,
        userContact1: String,
        userContact2: String
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.userName] = userName
            preferences[PreferenceKeys.userID] = userID
            preferences[PreferenceKeys.userIcNumber] = userIcNumber
            preferences[PreferenceKeys.userGender] = userGender
            preferences[PreferenceKeys.userType] = userType
            preferences[PreferenceKeys.userContact1] = userContact1
            preferences[PreferenceKeys.userContact2] = userContact2
        }
    }

    suspend fun updateUserProfileDetails(userContact1: String, userContact2: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.userContact1] = userContact1
            preferences[PreferenceKeys.userContact2] = userContact2
        }
    }

    val readUserProfileDetail = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val receivedUserName = preferences[PreferenceKeys.userName] ?: Constants.NOT_AVAILABLE
            val receivedUserID = preferences[PreferenceKeys.userID] ?: Constants.NOT_AVAILABLE
            val receivedUserIcNumber =
                preferences[PreferenceKeys.userIcNumber] ?: Constants.NOT_AVAILABLE
            val receivedUserGender =
                preferences[PreferenceKeys.userGender] ?: Constants.NOT_AVAILABLE
            val receivedUserType = preferences[PreferenceKeys.userType] ?: Constants.NOT_AVAILABLE
            val receivedUserContact1 = preferences[PreferenceKeys.userContact1] ?: Constants.NOT_AVAILABLE
            val receivedUserContact2 = preferences[PreferenceKeys.userContact2] ?: Constants.NOT_AVAILABLE
            UserProfileDetail(
                receivedUserName,
                receivedUserID,
                receivedUserIcNumber,
                receivedUserGender,
                receivedUserType,
                receivedUserContact1,
                receivedUserContact2
            )
        }

    suspend fun saveUserAccessToken(
        userAccessToken: String
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.userAccessToken] = userAccessToken
        }
    }

    val readUserAccessToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val receivedUserAccessToken =
                preferences[PreferenceKeys.userAccessToken] ?: Constants.NOT_AVAILABLE
            receivedUserAccessToken
        }

    suspend fun setUserLoggedIn(userLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.userLoggedIn] = userLoggedIn
        }
    }

    val readUserLoggedIn: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userLoggedIn = preferences[PreferenceKeys.userLoggedIn] ?: false
            userLoggedIn
        }

    // This method will be used to set
    // number of photos for patient user
    // and
    // number of patients for doctor user
    suspend fun setNumberOfPhotos(noOfPhotos: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.patientNumberOfPhotos] = noOfPhotos
        }
    }

    val readNumberOfPhotos: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val noOfPhotos = preferences[PreferenceKeys.patientNumberOfPhotos] ?: 0
            noOfPhotos
        }


    suspend fun deleteAllPreferences() {
        dataStore.edit {
            it.clear()
        }
    }

}

data class UserProfileDetail(
    val userName: String,
    val userID: String,
    val userIcNumber: String,
    val userGender: String,
    val userType: String,
    val userContact1: String? = null,
    val userContact2: String? = null
)
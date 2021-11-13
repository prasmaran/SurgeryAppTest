package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val readUserProfileDetail = dataStoreRepository.readUserProfileDetail

    val readNumberOfPhotos = dataStoreRepository.readNumberOfPhotos.asLiveData()

    // TODO: Create API to update user contact details
    /** Listen to the changes and update in Ui
     * Send changes to server and return the updated response */
    fun updateUserProfileDetails(
        userContact1: String,
        userContact2: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.updateUserProfileDetails(userContact1, userContact2)
        }
    }

    // TODO: Delete all Data Store Preferences
    fun deleteAllPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.deleteAllPreferences()
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                //mutableInternetConnection.postValue(true)
                true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                //mutableInternetConnection.postValue(true)
                true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                //mutableInternetConnection.postValue(true)
                true
            }
            else -> false
        }
    }
}

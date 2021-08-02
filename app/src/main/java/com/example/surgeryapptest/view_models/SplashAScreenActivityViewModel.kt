package com.example.surgeryapptest.view_models

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
class SplashAScreenActivityViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val readUserLoggedIn = dataStoreRepository.readUserLoggedIn.asLiveData()

    // TODO: Delete all Data Store Preferences
    fun deleteAllPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.deleteAllPreferences()
        }
    }

}

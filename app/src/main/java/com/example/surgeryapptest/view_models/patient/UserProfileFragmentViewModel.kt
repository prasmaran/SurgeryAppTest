package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.model.network.updateDetails.UpdateDetailResponse
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

@HiltViewModel
class UserProfileFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    /** Network Listener*/
    var networkStatus: Boolean = false
    var backOnline: Boolean = false

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()
    val readUserProfileDetail = dataStoreRepository.readUserProfileDetail
    val readNumberOfPhotos = dataStoreRepository.readNumberOfPhotos.asLiveData()

    var updatedUserDetailResponse: MutableLiveData<NetworkResult<UpdateDetailResponse>> =
        MutableLiveData()

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    fun updateUserDetails(
        userContact1: RequestBody,
        userContact2: RequestBody,
        userID: RequestBody,
    ) =
        viewModelScope.launch {
            updateUserDetailsSafeCall(
                userContact1, userContact2, userID
            )
        }

    private suspend fun updateUserDetailsSafeCall(
        userContact1: RequestBody,
        userContact2: RequestBody,
        userID: RequestBody
    ) {

        updatedUserDetailResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.updatePhoneNumber(userContact1, userContact2, userID)
                updatedUserDetailResponse.value = handleUpdateUserDetailsResponse(response)
            } catch (e: Exception) {
                updatedUserDetailResponse.value = NetworkResult.Error(e.message.toString())
            }
        } else {
            updatedUserDetailResponse.value = NetworkResult.Error("No internet connection")
        }

    }

    private fun handleUpdateUserDetailsResponse(response: Response<UpdateDetailResponse>): NetworkResult<UpdateDetailResponse>? {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.message}")
            }
            response.isSuccessful -> {
                val data = response.body()
                NetworkResult.Success(data!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

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
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}

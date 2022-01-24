package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.model.network.appointmentResponse.AppointmentNetworkResponse
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
@RequiresApi(Build.VERSION_CODES.M)
class AppointmentViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    /** Network Listener*/
    var networkStatus: Boolean = false
    var backOnline: Boolean = false

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()
    val readUserProfileDetail = dataStoreRepository.readUserProfileDetail

    var appointmentNetworkResponse: MutableLiveData<NetworkResult<AppointmentNetworkResponse>> =
        MutableLiveData()

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    fun getAppointmentList(
        userType: String,
        userID: String
    ) =
        viewModelScope.launch {
            retrieveAppointmentsSafeCall(
                userType, userID
            )
        }

    private suspend fun retrieveAppointmentsSafeCall(
        userType: String,
        userID: String
    ) {

        appointmentNetworkResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAppointmentList(userType, userID)
                appointmentNetworkResponse.value = handleAppointmentResponse(response)
            } catch (e: Exception) {
                appointmentNetworkResponse.value = NetworkResult.Error(e.message.toString())
            }
        } else {
            appointmentNetworkResponse.value = NetworkResult.Error("No internet connection")
        }

    }

    private fun handleAppointmentResponse(response: Response<AppointmentNetworkResponse>): NetworkResult<AppointmentNetworkResponse>? {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error(response.body()!!.message)
            }
            response.body()!!.result.isNullOrEmpty() -> {
                NetworkResult.Error(response.body()!!.message)
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

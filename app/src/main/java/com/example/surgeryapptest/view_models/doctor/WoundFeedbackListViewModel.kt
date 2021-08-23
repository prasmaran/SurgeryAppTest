package com.example.surgeryapptest.view_models.doctor

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.WoundImageFeedback
import com.example.surgeryapptest.model.network.doctorResponse.sendFeedbackResponse.SendWoundFeedbackResponse
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WoundFeedbackListViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    /** Network Listener*/
    var networkStatus: Boolean = false
    var backOnline: Boolean = false

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    /** RETROFIT */
    var feedbackListResponse: MutableLiveData<NetworkResult<WoundImageFeedback>> =
        MutableLiveData()

    var sendFeedbackResponse: MutableLiveData<NetworkResult<SendWoundFeedbackResponse>> =
        MutableLiveData()

    fun getWoundFeedbackList(woundImageID: String) = viewModelScope.launch {
        getWoundFeedbackListSafeCall(woundImageID)
    }

    // post feedback in the fragment
    fun postFeedback(params: Map<String, String>) = viewModelScope.launch {
        postFeedbackSafeCall(params)
    }

    private suspend fun getWoundFeedbackListSafeCall(woundImageID: String) {
        feedbackListResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFeedbackList(woundImageID)
                feedbackListResponse.value = handleFeedbackListResponse(response)

            } catch (e: Exception) {
                feedbackListResponse.value = NetworkResult.Error(e.message.toString())
                println("Error0: ${e.message.toString()}")
            }
        } else {
            feedbackListResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    // send feedback safe call
    private suspend fun postFeedbackSafeCall(params: Map<String, String>) {
        sendFeedbackResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.sendFeedback(params)
                sendFeedbackResponse.value = handleSendFeedbackResponse(response)

            } catch (e: Exception) {
                sendFeedbackResponse.value = NetworkResult.Error(e.message.toString())
                println("Error0: ${e.message.toString()}")
            }
        } else {
            sendFeedbackResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }


    private fun handleFeedbackListResponse(response: Response<WoundImageFeedback>): NetworkResult<WoundImageFeedback> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout Error")
            }
            response.body()!!.message.contains("Invalid Token") || response.body()!!.message.contains(
                "Session Expired"
            ) -> {
                NetworkResult.Error("Error1: ${response.body()!!.message}")
            }
            response.body()!!.result.isNullOrEmpty() -> {
                NetworkResult.Error("No feedback found")
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


    // handle send feedback response
    private fun handleSendFeedbackResponse(response: Response<SendWoundFeedbackResponse>): NetworkResult<SendWoundFeedbackResponse> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout Error")
            }
            response.body()!!.message.contains("Invalid Token") || response.body()!!.message.contains(
                "Session Expired"
            ) -> {
                NetworkResult.Error("Error1: ${response.body()!!.message}")
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
            println("WoundFeedbackListViewModel: $networkStatus")
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}

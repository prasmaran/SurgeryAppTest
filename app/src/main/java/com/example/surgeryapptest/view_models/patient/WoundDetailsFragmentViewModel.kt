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
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.WoundImageFeedback
import com.example.surgeryapptest.model.network.patientResponse.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WoundDetailsFragmentViewModel @Inject constructor(
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

    var updatedEntryResponse: MutableLiveData<NetworkResult<NetworkUpdateEntryResponse>> =
        MutableLiveData()

    var deletedEntryResponse: MutableLiveData<NetworkResult<NetworkDeleteEntryResponse>> =
        MutableLiveData()

    var archivedEntryResponse: MutableLiveData<NetworkResult<NetworkDeleteEntryResponse>> =
        MutableLiveData()

    var feedbackListResponse: MutableLiveData<NetworkResult<WoundImageFeedback>> =
        MutableLiveData()

    fun updateUploadedEntry(
        entryID: RequestBody,
        title: RequestBody,
        description: RequestBody,
        fluid_drain: RequestBody,
        painrate: RequestBody,
        redness: RequestBody,
        swelling: RequestBody,
        odour: RequestBody,
        fever: RequestBody,
    ) =
        viewModelScope.launch {
            updateUploadedEntrySafeCall(
                entryID, title, description, fluid_drain, painrate,
                redness, swelling, odour, fever
            )
        }

    fun deleteUploadedEntry(entryID: RequestBody) = viewModelScope.launch {
        deleteUploadedEntrySafeCall(entryID)
    }

    fun archiveUploadedEntry(entryID: RequestBody) = viewModelScope.launch {
        archiveUploadedEntrySafeCall(entryID)
    }

    fun getWoundFeedbackList(woundImageID: String) = viewModelScope.launch {
        getWoundFeedbackListSafeCall(woundImageID)
    }

    private suspend fun updateUploadedEntrySafeCall(
        entryID: RequestBody,
        title: RequestBody,
        description: RequestBody,
        fluid_drain: RequestBody,
        painrate: RequestBody,
        redness: RequestBody,
        swelling: RequestBody,
        odour: RequestBody,
        fever: RequestBody,
    ) {
        updatedEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.updateUploadedEntry(
                    entryID, title, description, fluid_drain, painrate,
                    redness, swelling, odour, fever
                )
                updatedEntryResponse.value = handleUpdateUploadedEntryResponse(response)
            } catch (e: Exception) {
                updatedEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            updatedEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun deleteUploadedEntrySafeCall(entryID: RequestBody) {
        deletedEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.deleteUploadedEntry(entryID)
                deletedEntryResponse.value = handleDeleteUploadedEntryResponse(response)
            } catch (e: Exception) {
                deletedEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            deletedEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    //TODO : Safe call for archiving API call
    private suspend fun archiveUploadedEntrySafeCall(entryID: RequestBody) {
        archivedEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.archiveUploadedEntry(entryID)
                archivedEntryResponse.value = handleArchiveUploadedEntryResponse(response)
            } catch (e: Exception) {
                archivedEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            archivedEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
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

    private fun handleUpdateUploadedEntryResponse(response: Response<NetworkUpdateEntryResponse>): NetworkResult<NetworkUpdateEntryResponse> {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.success}")
            }
            response.body()!!.message.toString().contains("Please") -> {
                NetworkResult.Error("Please upload an image!")
            }
            response.isSuccessful -> {
                val data = response.body()
                println("Successfully updated!!")
                NetworkResult.Success(data!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleDeleteUploadedEntryResponse(response: Response<NetworkDeleteEntryResponse>): NetworkResult<NetworkDeleteEntryResponse> {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.success}")
            }
            response.body()!!.message.toString().contains("Please") -> {
                NetworkResult.Error("Please upload an image!")
            }
            response.isSuccessful -> {
                val data = response.body()
                println("Successfully deleted !!!")
                NetworkResult.Success(data!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleArchiveUploadedEntryResponse(response: Response<NetworkDeleteEntryResponse>): NetworkResult<NetworkDeleteEntryResponse> {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.success}")
            }
            response.body()!!.message.toString().contains("Please") -> {
                NetworkResult.Error("Please upload an image!")
            }
            response.isSuccessful -> {
                val data = response.body()
                println("Successfully archived !!!")
                NetworkResult.Success(data!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
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
            println("WoundDetailsFragmentViewModel: $networkStatus")
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}
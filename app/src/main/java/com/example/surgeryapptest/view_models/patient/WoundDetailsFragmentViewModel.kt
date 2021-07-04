package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.model.network.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WoundDetailsFragmentViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var updatedEntryResponse: MutableLiveData<NetworkResult<NetworkUpdateEntryResponse>> =
        MutableLiveData()

    var deletedEntryResponse: MutableLiveData<NetworkResult<NetworkDeleteEntryResponse>> =
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
                val response = repository.remoteDataSource.updateUploadedEntry(
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
                val response = repository.remoteDataSource.deleteUploadedEntry(entryID)
                deletedEntryResponse.value = handleDeleteUploadedEntryResponse(response)
            } catch (e: Exception) {
                deletedEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            deletedEntryResponse.value = NetworkResult.Error("No Internet Connection")
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
                println("Successfully updated !!!")
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

}
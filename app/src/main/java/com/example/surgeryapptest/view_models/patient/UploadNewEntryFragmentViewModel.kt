package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.model.network.uploadNewImageResponse.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UploadNewEntryFragmentViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var uploadedNewEntryResponse: MutableLiveData<NetworkResult<NetworkUploadNewEntryResponse>> =
        MutableLiveData()

    fun uploadNewWoundEntry(
        image: MultipartBody.Part,
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
            getAllProgressEntrySafeCall(
                image,
                title,
                description,
                fluid_drain,
                painrate,
                redness,
                swelling,
                odour,
                fever
            )
        }

    private suspend fun getAllProgressEntrySafeCall(
        image: MultipartBody.Part,
        title: RequestBody,
        description: RequestBody,
        fluid_drain: RequestBody,
        painrate: RequestBody,
        redness: RequestBody,
        swelling: RequestBody,
        odour: RequestBody,
        fever: RequestBody,
    ) {
        uploadedNewEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.uploadNewEntry(
                    image, title, description, fluid_drain, painrate,
                    redness, swelling, odour, fever
                )
                uploadedNewEntryResponse.value = handleAllProgressEntryResponse(response)
            } catch (e: Exception) {
                uploadedNewEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            uploadedNewEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleAllProgressEntryResponse(response: Response<NetworkUploadNewEntryResponse>): NetworkResult<NetworkUploadNewEntryResponse> {

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
                println("Received data: \n $data")
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
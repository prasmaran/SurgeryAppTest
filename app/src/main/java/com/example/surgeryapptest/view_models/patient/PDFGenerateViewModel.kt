package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.surgeryapptest.model.network.pdfGenerationResponse.NetworkPDFGenerateResponse
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.M)
class PDFGenerateViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    var pdfGenerateResponse: MutableLiveData<NetworkResult<NetworkPDFGenerateResponse>> = MutableLiveData()

    fun generateWoundPDF(entryID : String) = viewModelScope.launch {
        getPDFGenerateSafeCall(entryID)
    }

    private suspend fun getPDFGenerateSafeCall(entryID : String) {
        pdfGenerateResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getWoundImagePDF(entryID)
                pdfGenerateResponse.value = handlePDFGenerateResponse(response)
            } catch (e: Exception) {
                pdfGenerateResponse.value = NetworkResult.Error(e.message.toString())
            }
        } else {
            pdfGenerateResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handlePDFGenerateResponse(response: Response<NetworkPDFGenerateResponse>): NetworkResult<NetworkPDFGenerateResponse> {
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
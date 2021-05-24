package com.example.surgeryapptest.view_models

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.surgeryapptest.model.network.AllProgressBookEntry
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var allProgressEntryResponse: MutableLiveData<NetworkResult<AllProgressBookEntry>> = MutableLiveData()

    fun getAllProgressEntry() = viewModelScope.launch {
        getAllProgressEntrySafeCall()
    }

    private suspend fun getAllProgressEntrySafeCall() {
        allProgressEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()){
            try {
                val response = repository.remoteDataSource.getAllProgressEntry()
                allProgressEntryResponse.value = handleAllProgressEntryResponse(response)
            } catch (e: Exception){
                allProgressEntryResponse.value = NetworkResult.Error("Data not found")
            }
        } else {
            allProgressEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleAllProgressEntryResponse(response: Response<AllProgressBookEntry>): NetworkResult<AllProgressBookEntry>? {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.result.isNullOrEmpty()-> {
                NetworkResult.Error("Data not found")
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
package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.surgeryapptest.model.network.userNetworkResponse.UserLoginNetworkResponse
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val readUserProfileDetail = dataStoreRepository.readUserProfileDetail

    fun saveUserProfileDetails(
        userName: String,
        userID: String,
        userIcNumber: String,
        userGender: String,
        userType: String,
        userContact1: String,
        userContact2: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveUserProfileDetails(userName, userID, userIcNumber, userGender, userType, userContact1, userContact2)
        }

        println("SAVED DATA IN VIEW MODEL: $userName $userID $userIcNumber $userGender $userType")
    }

    fun saveUserAccessToken(userAccessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveUserAccessToken(userAccessToken)
        }
    }

    fun setUserLoggedIn(userLoggedIn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setUserLoggedIn(userLoggedIn)
        }
    }

    //var userLoginResponse: MutableLiveData<NetworkResult<UserLoginNetworkResponse>> = MutableLiveData()
    val loginResponse: LiveData<NetworkResult<UserLoginNetworkResponse>> get() = userLoginResponse
    private val userLoginResponse = MutableLiveData<NetworkResult<UserLoginNetworkResponse>>()


    fun loginUser(params: Map<String, String>) =
        viewModelScope.launch {
            getUserLoginSafeCall(params)
        }

    private suspend fun getUserLoginSafeCall(
        params: Map<String, String>
    ) {
        userLoginResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.loginUser(params)
                userLoginResponse.value = handleUserLoginResponse(response)
            } catch (e: Exception) {
                userLoginResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            userLoginResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleUserLoginResponse(response: Response<UserLoginNetworkResponse>): NetworkResult<UserLoginNetworkResponse> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.message}")
            }
//            response.body()!!.message.contains("or") -> {
//                NetworkResult.Error("Error: ${response.body()!!.message}")
//            }
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
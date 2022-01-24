package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.surgeryapptest.model.network.passwordResetResponse.PasswordResetResponse
import com.example.surgeryapptest.model.network.sendDetailsForOTP.SendOTPResponse
import com.example.surgeryapptest.model.network.userNetworkResponse.UserLoginNetworkResponse
import com.example.surgeryapptest.model.network.verifyOTP.VerifiedOTPResponse
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.M)
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
            dataStoreRepository.saveUserProfileDetails(
                userName,
                userID,
                userIcNumber,
                userGender,
                userType,
                userContact1,
                userContact2
            )
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

    var sendRegistrationIdPhoneNumber: MutableLiveData<NetworkResult<SendOTPResponse>> =
        MutableLiveData()
    var verifiedOTPResponse: MutableLiveData<NetworkResult<VerifiedOTPResponse>> = MutableLiveData()
    var passwordResetResponse: MutableLiveData<NetworkResult<PasswordResetResponse>> =
        MutableLiveData()

    // invoke safe calls

    fun loginUser(params: Map<String, String>) =
        viewModelScope.launch {
            getUserLoginSafeCall(params)
        }

    fun sendRegistrationAndPhone(params: Map<String, String>) =
        viewModelScope.launch {
            getSendDetailsForOTPSafeCall(params)
        }

    fun sendOTPAndPhone(params: Map<String, String>) =
        viewModelScope.launch {
            getVerifiedOTPSafeCall(params)
        }

    fun sendResetPassword(params: Map<String, String>) =
        viewModelScope.launch {
            getResetPasswordSafeCall(params)
        }


    // safe calls

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

    private suspend fun getSendDetailsForOTPSafeCall(
        params: Map<String, String>
    ) {
        sendRegistrationIdPhoneNumber.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.sendRegistrationIdPhoneNumber(params)
                sendRegistrationIdPhoneNumber.value = handleSendDetailsForOTPResponse(response)
            } catch (e: Exception) {
                sendRegistrationIdPhoneNumber.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            sendRegistrationIdPhoneNumber.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getVerifiedOTPSafeCall(
        params: Map<String, String>
    ) {
        verifiedOTPResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.sendOTPWithPhoneNumber(params)
                verifiedOTPResponse.value = handleVerifiedOTPResponse(response)
            } catch (e: Exception) {
                verifiedOTPResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            verifiedOTPResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getResetPasswordSafeCall(
        params: Map<String, String>
    ) {
        passwordResetResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.resetPassword(params)
                passwordResetResponse.value = handleResetPasswordResponse(response)
            } catch (e: Exception) {
                passwordResetResponse.value = NetworkResult.Error(e.message.toString())
                println("Error : ${e.message.toString()}")
            }
        } else {
            passwordResetResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    // handle safe call functions

    private fun handleUserLoginResponse(response: Response<UserLoginNetworkResponse>): NetworkResult<UserLoginNetworkResponse> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.result.isNullOrEmpty() && response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error(response.body()!!.message)
            }
            response.body()!!.success.toString().contains("false") -> {
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

    private fun handleSendDetailsForOTPResponse(response: Response<SendOTPResponse>): NetworkResult<SendOTPResponse> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.message} : ${response.body()!!.result}")
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

    private fun handleVerifiedOTPResponse(response: Response<VerifiedOTPResponse>): NetworkResult<VerifiedOTPResponse> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.success.toString().contains("false") -> {
                NetworkResult.Error("Error: ${response.body()!!.message} : ${response.body()!!.data.status}")
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

    private fun handleResetPasswordResponse(response: Response<PasswordResetResponse>): NetworkResult<PasswordResetResponse> {
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

    // Check network status

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
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
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PatientListViewModel @Inject constructor(
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

    /** DATA STORE */

    val readUserProfileDetail = dataStoreRepository.readUserProfileDetail

    // Delete all Data Store Preferences
    fun deleteAllPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.deleteAllPreferences()
        }
    }

    fun setPatientNumber(patientNumber: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setNumberOfPhotos(patientNumber)
        }
    }

    fun setUserLoggedIn(userLoggedIn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setUserLoggedIn(userLoggedIn)
        }
    }


    /** RETROFIT */
    var allPatientsListResponse: MutableLiveData<NetworkResult<AssignedPatientsList>> =
        MutableLiveData()

    // Research List
    var allResearcherPatientsListResponse: MutableLiveData<NetworkResult<AssignedPatientsList>> =
        MutableLiveData()

    fun getAssignedPatientsList(doctorId: String) = viewModelScope.launch {
        getAssignedPatientsListSafeCall(doctorId)
    }

    fun getResearcherPatientsList() = viewModelScope.launch {
        getResearcherPatientsListSafeCall()
    }

    private suspend fun getAssignedPatientsListSafeCall(doctorId: String) {
        allPatientsListResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAssignedPatientsList(doctorId)
                allPatientsListResponse.value = handlePatientListResponse(response)

            } catch (e: Exception) {
                allPatientsListResponse.value = NetworkResult.Error(e.message.toString())
                println("Error0: ${e.message.toString()}")
            }
        } else {
            allPatientsListResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getResearcherPatientsListSafeCall() {
        allResearcherPatientsListResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllPatientsList()
                allResearcherPatientsListResponse.value = handleResearcherPatientListResponse(response)

            } catch (e: Exception) {
                allResearcherPatientsListResponse.value = NetworkResult.Error(e.message.toString())
                println("Error0: ${e.message.toString()}")
            }
        } else {
            allResearcherPatientsListResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }


    private fun handlePatientListResponse(response: Response<AssignedPatientsList>): NetworkResult<AssignedPatientsList> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout Error")
            }
            response.body()!!.message.contains("Invalid Token") || response.body()!!.message.contains(
                "Session Expired") -> {
                NetworkResult.Error("Error1: ${response.body()!!.message}")
            }
            response.body()!!.result.isNullOrEmpty() || response.body()!!.result.size < 1 -> {
                NetworkResult.Error("No patient list assigned under your name. Please contact the Pathology Department.")
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

    private fun handleResearcherPatientListResponse(response: Response<AssignedPatientsList>): NetworkResult<AssignedPatientsList> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout Error")
            }
            response.body()!!.message.contains("Invalid Token") || response.body()!!.message.contains(
                "Session Expired") -> {
                NetworkResult.Error("Error1: ${response.body()!!.message}")
            }
            response.body()!!.result.isNullOrEmpty() || response.body()!!.result.size < 1 -> {
                NetworkResult.Error("No patient list available. Please contact the Pathology Department.")
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
            println("MainActivityViewModel: $networkStatus")
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}

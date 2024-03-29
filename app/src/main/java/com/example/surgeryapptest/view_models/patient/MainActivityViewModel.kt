package com.example.surgeryapptest.view_models.patient

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.utils.app.DataStoreRepository
import com.example.surgeryapptest.utils.database.ProgressBookEntity
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.utils.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.M)
class MainActivityViewModel @Inject constructor(
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

    // TODO: Delete all Data Store Preferences
    fun deleteAllPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.deleteAllPreferences()
        }
    }

    fun setUserLoggedIn(userLoggedIn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setUserLoggedIn(userLoggedIn)
        }
    }

    // Set number of photos for a patient
    fun setNumberOfPhotos(noOfPhotos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setNumberOfPhotos(noOfPhotos)
        }
    }

    /** ROOM DATABASE */
    var readDatabase: LiveData<List<ProgressBookEntity>> =
        repository.local.readDatabase().asLiveData()

    private fun insertProgressBook(progressBookEntity: ProgressBookEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertProgressBook(progressBookEntity)
        }
    }

    // Newly added to search database
//    fun searchDatabase(searchQuery: String): LiveData<List<ProgressBookEntity>> {
//        return repository.local.searchDatabase(searchQuery).asLiveData()
//    }

    /** RETROFIT */
    var allProgressEntryResponse: MutableLiveData<NetworkResult<AllProgressBookEntry>> =
        MutableLiveData()

    var allArchivedEntryResponse: MutableLiveData<NetworkResult<AllProgressBookEntry>> =
        MutableLiveData()

    fun getAllProgressEntry(userId: String) = viewModelScope.launch {
        getAllProgressEntrySafeCall(userId)
    }

    private suspend fun getAllProgressEntrySafeCall(userId: String) {
        allProgressEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllProgressEntry(userId)
                allProgressEntryResponse.value = handleAllProgressEntryResponse(response)

                val progressBook = allProgressEntryResponse.value!!.data
                if (progressBook != null) {
                    offlineProgressBookCache(progressBook)
                }

            } catch (e: Exception) {
                allProgressEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error0: ${e.message.toString()}")
            }
        } else {
            allProgressEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleAllProgressEntryResponse(response: Response<AllProgressBookEntry>): NetworkResult<AllProgressBookEntry> {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout Error")
            }
            response.body()!!.message.contains("Invalid Token") || response.body()!!.message.contains(
                "Session Expired"
            ) -> {
                NetworkResult.Error("Error 1: ${response.body()!!.message}")
            }
            response.body()!!.result.isNullOrEmpty() && response.body()!!.success.contains("true") -> {
                //NetworkResult.Error("Error Special: ${response.body()!!.message}")
                NetworkResult.Error("No progress book found under your name.") // Error 2
            }
            response.body()!!.result.isNullOrEmpty() -> {
                NetworkResult.Error("${response.body()!!.message}")  // Error 3
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

    /**
     * This part is for returning archived files
     */

    fun getAllArchivedEntry(userId: String) = viewModelScope.launch {
        getAllArchivedEntrySafeCall(userId)
    }

    private suspend fun getAllArchivedEntrySafeCall(userId: String) {
        allProgressEntryResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllArchivedEntry(userId)
                allArchivedEntryResponse.value = handleAllArchivedEntryResponse(response)

            } catch (e: Exception) {
                allArchivedEntryResponse.value = NetworkResult.Error(e.message.toString())
                println("Error0: ${e.message.toString()}")
            }
        } else {
            allArchivedEntryResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleAllArchivedEntryResponse(response: Response<AllProgressBookEntry>): NetworkResult<AllProgressBookEntry> {

        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout Error")
            }
            response.body()!!.message.contains("Invalid Token") || response.body()!!.message.contains(
                "Session Expired"
            ) -> {
                NetworkResult.Error("Error 1: ${response.body()!!.message}")
            }
            response.body()!!.result.isNullOrEmpty() && response.body()!!.success.contains("true") -> {
                //NetworkResult.Error("Error Special: ${response.body()!!.message}")
                NetworkResult.Error("Error: No archive files found.")
            }
            response.body()!!.result.isNullOrEmpty() -> {
                NetworkResult.Error("Error3: ${response.body()!!.message}")
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

    /**
     *
     */

    private fun offlineProgressBookCache(progressBook: AllProgressBookEntry) {
        val progressBookEntity = ProgressBookEntity(progressBook)
        insertProgressBook(progressBookEntity)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                //mutableInternetConnection.postValue(true)
                true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                //mutableInternetConnection.postValue(true)
                true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                //mutableInternetConnection.postValue(true)
                true
            }
            else -> false
        }
    }

    /**
     * This is for network checking
     */

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

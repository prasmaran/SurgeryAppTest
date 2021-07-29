package com.example.surgeryapptest.ui.fragments.patientFrags

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.patientActivities.LoginActivity
import com.example.surgeryapptest.utils.adapter.Adapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.constant.Constants.Companion.NETWORK_ERROR_NO_INTERNET
import com.example.surgeryapptest.utils.constant.Constants.Companion.UNAUTHENTICATED_USER
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_patient_progress_books.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PatientProgressBooksFragment : Fragment() {

    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var loginViewModel: LoginActivityViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { Adapter() }
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        loginViewModel =
            ViewModelProvider(requireActivity()).get(LoginActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_patient_progress_books, container, false)

        setupRecyclerView()
        // requestApiData()

        mainViewModel.readBackOnline.observe(viewLifecycleOwner, {
            mainViewModel.backOnline = it
        })

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener_ProgressBookGFrag", status.toString())
                    mainViewModel.networkStatus = status
                    mainViewModel.showNetworkStatus()
                    requestApiData()
                }
        }

        mView.floatingActionButton.setOnClickListener {
            if (mainViewModel.networkStatus) {
                val action = PatientProgressBooksFragmentDirections.actionPatientProgressBooksFragmentToUploadNewEntryFragment()
                findNavController().navigate(action)
            }
            else {
                Toast.makeText(
                    requireContext(),
                    "No Internet Connection. Cannot upload new photo at the moment.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // If no Internet connection
        /**
         * readDatabase()
         * */

        return mView
    }

    // Testing Data Store User Profile Value

//    private fun readSavedUserProfileDetails() {
//        var uName = ""
//        var uIC = ""
//        var uGender = ""
//        var uType = ""
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            loginViewModel.readUserProfileDetail.collect {
//                uName = it.userName
//                uIC = it.userIcNumber
//                uGender = it.userGender
//                uType = it.userType
//            }
//        }
//
//        println("RETRIEVED DATA FROM DS IN PROGRESS BOOK: $uName $uIC $uGender $uType")
//    }

    private fun requestApiData() {
        mainViewModel.getAllProgressEntry()
        mainViewModel.allProgressEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val progressBookResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        progressBookResponse,
                        //response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    // Loading cache from database

                    val progressBookResponse =
                        response.message.toString() //response.data?.message.toString()
                    if (progressBookResponse.contains("Unauthorized User")) {
                        unAuthenticateDialog(progressBookResponse)
                    } else {
                        refreshToReadFromCacheDialog(progressBookResponse)
                    }
//                    Toast.makeText(
//                        requireContext(),
//                        response.message.toString(),
//                        //response.message.toString() + ". Loading from cache if exists",
//                        Toast.LENGTH_SHORT
//                    ).show()

                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    // Use this function whenever there is no Internet connection
    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readDatabase.observe(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].progressBook)
                    hideShimmerEffect()
                }
            })
        }
    }

    // To show a dialog fragment to refresh the page
    private fun refreshToReadFromCacheDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(NETWORK_ERROR_NO_INTERNET)
        builder.setMessage("\n$errorMessage. Want to view previously stored data, (if exists) ?")
        builder.setIcon(R.drawable.ic_local_disk)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            readDatabase()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // To show a dialog to redirect to login page
    private fun unAuthenticateDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(UNAUTHENTICATED_USER)
        //builder.setMessage("\nNo internet connection. Want to view previously stored data, (if exists) ?")
        builder.setMessage("\n$errorMessage. Do you want to login again?")
        builder.setIcon(R.drawable.ic_unauthorized_person)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            goToLoginPage()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

//    private fun loadDataFromCache() {
//        lifecycleScope.launch {
//            mainViewModel.readDatabase.observe(viewLifecycleOwner, { database ->
//                if (database.isNotEmpty()) {
//                    mAdapter.setData(database[0].progressBook)
//                    hideShimmerEffect()
//                }
//            })
//        }
//    }

    // Disable FAB when no Internet connection
    private fun disableFab() {
        mView.floatingActionButton.backgroundTintList =
            ColorStateList.valueOf(Color.rgb(75, 75, 75))
        println("CANNOT CLICK FAB SINCE NO INTERNET CONNECTION")
        Toast.makeText(
            requireContext(),
            "Cannot upload due to no Internet connection",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupRecyclerView() {
        mView.recyclerView.adapter = mAdapter
        mView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        mView.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        mView.recyclerView.hideShimmer()
    }
}
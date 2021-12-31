package com.example.surgeryapptest.ui.fragments.patientFrags

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientProgressBooksBinding
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.adapter.Adapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants.Companion.NETWORK_ERROR_NO_INTERNET
import com.example.surgeryapptest.utils.constant.Constants.Companion.NO_PROGRESS_BOOK
import com.example.surgeryapptest.utils.constant.Constants.Companion.UNAUTHENTICATED_USER
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class PatientProgressBooksFragment : Fragment() {

    private var _binding: FragmentPatientProgressBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { Adapter() }
    private var userId: String = ""
    private var tokenValid = true

    // Passing data for charts
    private var chartData = emptyList<AllProgressBookEntryItem>()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        sessionManager = SessionManager(requireContext())
    }

    @SuppressLint("LongLogTag", "NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientProgressBooksBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        // searchProgressBookFilter()
        // requestApiData()

        mainViewModel.readBackOnline.observe(viewLifecycleOwner, {
            mainViewModel.backOnline = it
        })

        // Read userId to get specific progress book data
        lifecycleScope.launch {
            mainViewModel.readUserProfileDetail.collect { values ->
                userId = values.userID
            }
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener_ProgressBookGFrag", status.toString())
                    mainViewModel.networkStatus = status
                    mainViewModel.showNetworkStatus()
                    requestApiData(userId)
                }
        }

        binding.floatingActionButton.setOnClickListener {
            if (mainViewModel.networkStatus) {
                val action =
                    PatientProgressBooksFragmentDirections.actionPatientProgressBooksFragmentToUploadNewEntryFragment()
                findNavController().navigate(action)
            } else {
                MotionToast.createColorToast(
                    requireActivity(), "No Internet",
                    "Cannot upload new photo at the moment",
                    MotionToastStyle.INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                )
            }
        }

        binding.archiveTitleTv.setOnClickListener {
            val action =
                PatientProgressBooksFragmentDirections.actionPatientProgressBooksFragmentToPatientArchiveBookFragment()
            findNavController().navigate(action)
        }

        // If no Internet connection
        /**
         * readDatabase()
         * */

        return view
    }

    // Search filter testing
    // Hide this if not working
//    private fun searchProgressBookFilter() {
//        binding.patientBookSearchView.setOnQueryTextListener(object :
//            SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                if (query != null) {
//                    val searchQuery = "%$query%"
//                    mainViewModel.searchDatabase(searchQuery)
//                        .observe(viewLifecycleOwner, { database ->
//                            if (database.isNotEmpty()) {
//                                mAdapter.setData(database[0].progressBook)
//                            }
//                        })
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(query: String?): Boolean {
//
//                if (query != null) {
//                    val searchQuery = "%$query%"
//                    mainViewModel.searchDatabase(searchQuery)
//                        .observe(viewLifecycleOwner, { database ->
//                            if (database.isNotEmpty()) {
//                                mAdapter.setData(database[0].progressBook)
//                                //database[0].progressBook.result?.get(0)
//                            }
//                        })
//                }
//                return true
//            }
//        })
//    }

    @SuppressLint("NewApi")
    private fun requestApiData(userId: String) {
        mainViewModel.getAllProgressEntry(userId)
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
                    val noOfPhotos = response.data?.result?.size
                    if (noOfPhotos != null) {
                        mainViewModel.setNumberOfPhotos(noOfPhotos)
                    }

                    response.data?.let {
                        chartData = it.result!!
                        mAdapter.setData(it)
                    }

                    //Log.d("PatientProgressBooksFrag", "chartData : $chartData")
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    // Loading cache from database

                    val progressBookResponse =
                        response.message.toString() //response.data?.message.toString()

                    if (progressBookResponse.contains("Unauthorized User") || progressBookResponse.contains(
                            "Invalid Token"
                        )
                    ) {
                        unAuthenticateDialog(progressBookResponse)
                        tokenValid = false
                        binding.floatingActionButton.isClickable = false
                    }
                    if (progressBookResponse.contains("No progress book found")) {
                        noProgressBookFound(progressBookResponse)
                    }
                    if (progressBookResponse.contains("No Internet Connection")) {
                        refreshToReadFromCacheDialog(progressBookResponse)
                    }

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
        //val builder = AlertDialog.Builder(requireContext())
        val builder = MaterialAlertDialogBuilder(requireContext())
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
    @SuppressLint("NewApi")
    private fun unAuthenticateDialog(errorMessage: String) {
        //val builder = AlertDialog.Builder(requireContext())
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(UNAUTHENTICATED_USER)
        //builder.setMessage("\nNo internet connection. Want to view previously stored data, (if exists) ?")
        builder.setMessage("\n$errorMessage. Do you want to login again?")
        builder.setIcon(R.drawable.ic_unauthorized_person)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            sessionManager.saveAuthToken(null)
            mainViewModel.deleteAllPreferences()
            goToLoginPage()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // To show a dialog to redirect to login page
    private fun noProgressBookFound(errorMessage: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val builder2 = AlertDialog.Builder(requireContext())
        builder.setTitle(NO_PROGRESS_BOOK)
        builder.setMessage("\n$errorMessage")
        builder.setIcon(R.drawable.ic_empty)

        builder.setPositiveButton(R.string.yes) { _, _ ->
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
        binding.floatingActionButton.backgroundTintList =
            ColorStateList.valueOf(Color.rgb(75, 75, 75))
        println("CANNOT CLICK FAB SINCE NO INTERNET CONNECTION")
        Toast.makeText(
            requireContext(),
            "Cannot upload due to no Internet connection",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerView.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

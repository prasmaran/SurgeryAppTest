package com.example.surgeryapptest.ui.fragments.patientFrags

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientArchiveBookBinding
import com.example.surgeryapptest.databinding.FragmentPatientProgressBooksBinding
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.adapter.Adapter
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PatientArchiveBookFragment : Fragment() {

    private var _binding: FragmentPatientArchiveBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { Adapter() }

    private var userId: String = ""
    private var tokenValid = true

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        sessionManager = SessionManager(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientArchiveBookBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()

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


        return view
    }

    private fun requestApiData(userId: String) {
        mainViewModel.getAllArchivedEntry(userId)
        mainViewModel.allArchivedEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val archivedBookResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        archivedBookResponse,
                        Toast.LENGTH_SHORT
                    ).show()
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    // Loading cache from database

                    val archivedBookResponse =
                        response.message.toString() //response.data?.message.toString()

                    if (archivedBookResponse.contains("Unauthorized User") || archivedBookResponse.contains(
                            "Invalid Token"
                        )
                    ) {
                        unAuthenticateDialog(archivedBookResponse)
                        tokenValid = false
                    }
                    if (archivedBookResponse.contains("No archive files")) {
                        noProgressBookFound(archivedBookResponse)
                    }
                    if (archivedBookResponse.contains("No Internet Connection")) {
                        AppUtils.showToast(requireContext(), "No Internet Connection")
                    }
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    // To show a dialog to redirect to login page
    private fun unAuthenticateDialog(errorMessage: String) {
        //val builder = AlertDialog.Builder(requireContext())
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(Constants.UNAUTHENTICATED_USER)
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
        builder.setTitle(Constants.NO_ARCHIVE_BOOK)
        builder.setMessage("\n$errorMessage")
        builder.setIcon(R.drawable.ic_archive)
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

    private fun setupRecyclerView() {
        binding.archivedBookRecyclerView.adapter = mAdapter
        binding.archivedBookRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.archivedBookRecyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.archivedBookRecyclerView.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
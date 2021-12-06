package com.example.surgeryapptest.ui.fragments.patientFrags

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientArchiveBookBinding
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.adapter.ArchivedAdapter
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import com.example.surgeryapptest.view_models.patient.WoundDetailsFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@AndroidEntryPoint
class PatientArchiveBookFragment : Fragment() {

    private var _binding: FragmentPatientArchiveBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var updateUploadedEntryViewModel: WoundDetailsFragmentViewModel

    private lateinit var networkListener: NetworkListener

    private val mAdapter by lazy {
        ArchivedAdapter {
            createAlertDialogUnarchive(it)
        }
    }

    private var userId: String = ""
    private var tokenValid = true
    private var prevFlag: String = "2"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        updateUploadedEntryViewModel =
            ViewModelProvider(requireActivity()).get(WoundDetailsFragmentViewModel::class.java)
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

        reCallAPI()

//        lifecycleScope.launch {
//            networkListener = NetworkListener()
//            networkListener.checkNetworkAvailability(requireContext())
//                .collect { status ->
//                    Log.d("NetworkListener_ProgressBookGFrag", status.toString())
//                    mainViewModel.networkStatus = status
//                    mainViewModel.showNetworkStatus()
//                    requestApiData(userId)
//                }
//        }

        swipeToRefresh(userId)

        return view
    }

    private fun reCallAPI(){
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
    }

    private fun requestApiData(userId: String) {
        mainViewModel.getAllArchivedEntry(userId)
        progressBarVisible(true)
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
                    progressBarVisible(false)
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    progressBarVisible(false)

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
//                    if (archivedBookResponse.contains("No archive files")) {
//                        noProgressBookFound(archivedBookResponse)
//                    }
                    if (archivedBookResponse.contains("No Internet Connection")) {
                        AppUtils.showToast(requireContext(), "No Internet Connection")
                    }
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                    progressBarVisible(true)
                }
            }
        })
    }

    private fun swipeToRefresh(userId: String) {
        binding.archivedFragSwipeToRefresh.setOnRefreshListener {
            mainViewModel.getAllArchivedEntry(userId)
            mainViewModel.allArchivedEntryResponse.observe(viewLifecycleOwner, { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        val archivedBookResponse = response.data?.message.toString()
//                        Toast.makeText(
//                            requireContext(),
//                            archivedBookResponse,
//                            Toast.LENGTH_SHORT
//                        ).show()
                        binding.archivedFragSwipeToRefresh.isRefreshing = false
                        hideShimmerEffect()
                        response.data?.let { mAdapter.setData(it) }

                    }
                    is NetworkResult.Error -> {
                        binding.archivedFragSwipeToRefresh.isRefreshing = false
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
                    //if (archivedBookResponse.contains("No archive files")) {
                    //    noProgressBookFound(archivedBookResponse)
                    //}
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

    //TODO: Move to AppUtils later on
    private fun createAlertDialogUnarchive(data: ArrayList<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Restore?")
        builder.setMessage("\nAre you sure you want to restore entry: ${data[1]}?")
        builder.setIcon(R.drawable.ic_restore)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            restoreArchivedEntry(data[0], data[2])
            //AppUtils.showToast(requireContext(), "Image ID : ${data[0]} has been restored")
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->

        }
        builder.setNeutralButton("DELETE PERMANENTLY") { _, _ ->
            deleteArchivedEntry(data[0], data[2])
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }

    private fun deleteArchivedEntry(woundID: String, pos: String) {

        updateUploadedEntryViewModel.deleteUploadedEntry(
            woundID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        progressBarVisible(true)

        updateUploadedEntryViewModel.deletedEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.archiveRootLayout.showSnackBar("${response.data?.message}")
                    progressBarVisible(false)
                    hideShimmerEffect()
                    //mAdapter.notifyItemRemoved(pos.toInt())
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarVisible(false)
                }
                is NetworkResult.Loading -> {
                    //TODO: Add loading fragment here
                }
            }
        })
    }

    private fun restoreArchivedEntry(woundID: String, pos: String) {

        updateUploadedEntryViewModel.archiveUploadedEntry(
            woundID.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            prevFlag.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        progressBarVisible(true)

        updateUploadedEntryViewModel.archivedEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.archiveRootLayout.showSnackBar("${response.data?.message}")
                    progressBarVisible(false)
                    reCallAPI()
                    //mAdapter.notifyItemRemoved(pos.toInt())
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarVisible(false)
                }
                is NetworkResult.Loading -> {
                    //TODO: Add loading fragment here
                }
            }
        })
        //reCallAPI()
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

    private fun progressBarVisible(show: Boolean) {
        if (show) {
            binding.archiveFragProgressBar.visibility = View.VISIBLE
        } else {
            binding.archiveFragProgressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.adapter.Adapter
import com.example.surgeryapptest.utils.adapter.PatientListAdapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.doctor.PatientListViewModel
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_doctor_patient_list.view.*
import kotlinx.android.synthetic.main.fragment_patient_progress_books.view.*
import kotlinx.android.synthetic.main.fragment_patient_progress_books.view.recyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DoctorPatientListFragment : Fragment() {

    private lateinit var dView: View
    private lateinit var patientListViewModel: PatientListViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { PatientListAdapter() }
    private var doctorId: String = ""

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientListViewModel =
            ViewModelProvider(requireActivity()).get(PatientListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dView = inflater.inflate(R.layout.fragment_doctor_patient_list, container, false)

        setupRecyclerView()

        patientListViewModel.readBackOnline.observe(viewLifecycleOwner, {
            patientListViewModel.backOnline = it
        })

        // Read doctorId to get assigned patients list
        lifecycleScope.launch {
            patientListViewModel.readUserProfileDetail.collect { values ->
                doctorId = values.userID
            }
        }

        // Listen to network connection
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener_PatientListFrag", status.toString())
                    patientListViewModel.networkStatus = status
                    patientListViewModel.showNetworkStatus()
                    requestPatientList(doctorId)
                }
        }


        return dView
    }

    private fun requestPatientList(doctorId: String) {
        patientListViewModel.getAssignedPatientsList(doctorId)
        patientListViewModel.allPatientsListResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    setErrorAttributesVisible(true)
                    val patientListResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        patientListResponse,
                        Toast.LENGTH_SHORT
                    ).show()
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    val patientListResponse = response.message.toString()

                    Toast.makeText(
                        requireContext(),
                        patientListResponse,
                        //response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (patientListResponse.contains("No patient list")) {
                        setErrorAttributesVisible(false)
                    }

                    if (patientListResponse.contains("Unauthorized User") || patientListResponse.contains("Invalid Token") ) {
                        unAuthenticateDialog(patientListResponse)
                    }

                    hideShimmerEffect()

                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })

    }

    private fun unAuthenticateDialog(errorMessage: String) {
        //val builder = AlertDialog.Builder(requireContext())
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(Constants.UNAUTHENTICATED_USER)
        //builder.setMessage("\nNo internet connection. Want to view previously stored data, (if exists) ?")
        builder.setMessage("\n$errorMessage. Do you want to login again?")
        builder.setIcon(R.drawable.ic_unauthorized_person)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            sessionManager.saveAuthToken(null)
            patientListViewModel.deleteAllPreferences()
            goToLoginPage()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun setErrorAttributesVisible(hasList: Boolean){
        if (hasList) {
            dView.no_patient_list_tv.visibility = View.GONE
            dView.no_patient_list_image.visibility = View.GONE
        } else {
            dView.no_patient_list_tv.visibility = View.VISIBLE
            dView.no_patient_list_image.visibility = View.VISIBLE
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        dView.recyclerViewPatientList.adapter = mAdapter
        //dView.recyclerViewPatientList.layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL, false)
        dView.recyclerViewPatientList.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        dView.recyclerViewPatientList.showShimmer()
    }

    private fun hideShimmerEffect() {
        dView.recyclerViewPatientList.hideShimmer()
    }
}
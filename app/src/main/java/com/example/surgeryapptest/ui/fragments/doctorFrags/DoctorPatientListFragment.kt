package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.surgeryapptest.databinding.FragmentDoctorPatientListBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.adapter.PatientListAdapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.doctor.PatientListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DoctorPatientListFragment : Fragment() {

    private var _binding: FragmentDoctorPatientListBinding? = null
    private val binding get() = _binding!!

    private lateinit var patientListViewModel: PatientListViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { PatientListAdapter() }
    private var doctorId: String = ""

    private lateinit var chartDataPatientNameList : AssignedPatientsList
    private lateinit var patientList : ArrayList<PatientName>

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientListViewModel =
            ViewModelProvider(requireActivity()).get(PatientListViewModel::class.java)
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorPatientListBinding.inflate(inflater, container, false)
        val view = binding.root


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
                    Log.d("NetworkPatientListFrag", status.toString())
                    patientListViewModel.networkStatus = status
                    patientListViewModel.showNetworkStatus()
                    requestPatientList(doctorId)
                }
        }

        summaryChartFAB()

        return view
    }

    private fun summaryChartFAB() {
        binding.summaryChartFAB.setOnClickListener {
            val action =
                DoctorPatientListFragmentDirections.actionDoctorPatientListFragmentToChartingActivity(chartDataPatientNameList)
            findNavController().navigate(action)
        }
    }

    @SuppressLint("NewApi")
    private fun extractPatientDataForChat(chartData: ArrayList<PatientName>) {
        val patientArrayList = ArrayList<String>()
        for (i in chartData) {
            /**
             * trying new format after stream chat error
             */
            //var newFormat = "${i.woundImages[0].mName}_${i.woundImages[0].masterUserIdFk}_p_${i.woundImages[0].gen}"
            patientArrayList.add("${i.woundImages[0].mName}_${i.woundImages[0].masterUserIdFk}_P")
        }
        val patientList = patientArrayList.joinToString(",")
        patientListViewModel.savePatientNameList(patientList)
    }

    @SuppressLint("NewApi")
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

                    val noOfPatients = response.data?.result?.size
                    if (noOfPatients != null) {
                        patientListViewModel.setPatientNumber(noOfPatients)
                    }
                    response.data?.let {
                        binding.summaryChartFAB.visibility = View.VISIBLE
                        chartDataPatientNameList = it
                        mAdapter.setData(it)
                        patientList = it.result
                        //extractPatientDataForChat(patientList)
                    }

                    Log.d("DPListFrag", "requestPatientList: $chartDataPatientNameList")
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

                    if (patientListResponse.contains("Unauthorized User") || patientListResponse.contains(
                            "Invalid Token"
                        )
                    ) {
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

    @SuppressLint("NewApi")
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

    private fun setErrorAttributesVisible(hasList: Boolean) {
        if (hasList) {
            binding.noPatientListImage.visibility = View.GONE
            binding.noPatientListImage.visibility = View.GONE
        } else {
            binding.noPatientListImage.visibility = View.VISIBLE
            binding.noPatientListImage.visibility = View.VISIBLE
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPatientList.adapter = mAdapter
        //dView.recyclerViewPatientList.layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL, false)
        binding.recyclerViewPatientList.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.recyclerViewPatientList.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerViewPatientList.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

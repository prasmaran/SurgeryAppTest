package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentDoctorAppointmentBinding
import com.example.surgeryapptest.ui.dialog_fragments.PatientDetailFeedback
import com.example.surgeryapptest.ui.dialog_fragments.WoundFeedbackFrag
import com.example.surgeryapptest.utils.adapter.appointmentAdaptes.DoctorAppointmentAdapter
import com.example.surgeryapptest.utils.adapter.appointmentAdaptes.PatientAppointmentAdapter
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.constant.DialogFragConstants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.AppointmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class DoctorAppointmentFragment : Fragment() {

    private var _binding: FragmentDoctorAppointmentBinding? = null
    private val binding get() = _binding!!
    private var userType: String = ""
    private var userId: String = ""

    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var networkListener: NetworkListener

    private val mAdapter by lazy { DoctorAppointmentAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        appointmentViewModel = ViewModelProvider(requireActivity()).get(AppointmentViewModel::class.java)
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorAppointmentBinding.inflate(inflater, container, false)

        setupRecyclerView()
        //setupMockData()

        appointmentViewModel.readBackOnline.observe(viewLifecycleOwner) {
            appointmentViewModel.backOnline = it
        }

        // Read userId to get specific progress book data
        lifecycleScope.launch {
            appointmentViewModel.readUserProfileDetail.collect { values ->
                userType = values.userType
                userId = values.userID
            }
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    appointmentViewModel.networkStatus = status
                    appointmentViewModel.showNetworkStatus()
                    requestApiData(userType, userId)
                }
        }

        return binding.root
    }

    @SuppressLint("NewApi")
    private fun requestApiData(userType: String, userId: String) {
        appointmentViewModel.getAppointmentList(userType, userId)
        appointmentViewModel.appointmentNetworkResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    if (response.data?.result?.size!! < 1) {
                        binding.noAppointmentDoctorTv.visibility = View.VISIBLE
                        binding.noAppointmentDoctorIcon.visibility = View.VISIBLE
                    }
                    response.data.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()

                    binding.noAppointmentDoctorTv.visibility = View.VISIBLE
                    binding.noAppointmentDoctorIcon.visibility = View.VISIBLE

                    if (response.message.toString().contains("No appointment")) {
                        MotionToast.darkColorToast(
                            requireActivity(),
                            "Nothing found!",
                            response.message.toString(),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                        )
                    } else {
                        MotionToast.darkColorToast(
                            requireActivity(),
                            "Something went wrong!",
                            response.message.toString(),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                        )
                    }
                    // response.message.toString()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.doctorAppointmentRecyclerView.adapter = mAdapter
        binding.doctorAppointmentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.doctorAppointmentRecyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.doctorAppointmentRecyclerView.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
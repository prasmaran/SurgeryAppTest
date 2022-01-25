package com.example.surgeryapptest.ui.fragments.patientFrags

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientAppointmentBinding
import com.example.surgeryapptest.model.network.appointmentResponse.Result
import com.example.surgeryapptest.utils.adapter.appointmentAdaptes.PatientAppointmentAdapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.AppointmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class PatientAppointmentFragment : Fragment() {

    private var _binding: FragmentPatientAppointmentBinding? = null
    private val binding get() = _binding!!
    private var userType: String = ""
    private var userId: String = ""

    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var networkListener: NetworkListener

    private val mAdapter by lazy { PatientAppointmentAdapter() }

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
        _binding = FragmentPatientAppointmentBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        //setupMockData()

        appointmentViewModel.readBackOnline.observe(viewLifecycleOwner, {
            appointmentViewModel.backOnline = it
        })

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

        return view
    }

    @SuppressLint("NewApi")
    private fun requestApiData(userType: String, userId: String) {
        appointmentViewModel.getAppointmentList(userType, userId)
        appointmentViewModel.appointmentNetworkResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    if (response.data?.result?.size!! < 1) {
                        binding.noAppointmentPatientTv.visibility = View.VISIBLE
                        binding.noAppointmentPatientIcon.visibility = View.VISIBLE
                    }
                    response.data.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()

                    binding.noAppointmentPatientTv.visibility = View.VISIBLE
                    binding.noAppointmentPatientIcon.visibility = View.VISIBLE

                    if(response.message.toString().contains("No appointment")) {
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
        })
    }

    private fun setupMockData() {
        var patientAppointmentList = mutableListOf<Result>()
//        patientAppointmentList.add(
//            Result(
//                1,
//                "2022-01-19T01:30:00.000Z",
//                "2022-01-16T17:30:15.000Z",
//                "Stomach checkup",
//                21,
//                "Kagan",
//                15,
//            )
//        )
//        patientAppointmentList.add(
//            Result(
//                2,
//                "2022-01-20T01:30:00.000Z",
//                "2022-01-16T17:30:15.000Z",
//                "Stomach checkup",
//                21,
//                "Kagan",
//                15,
//            )
//        )

        //mAdapter.setData(patientAppointmentList)
        hideShimmerEffect()
    }

    private fun setupRecyclerView() {
        binding.patientAppointmentRecyclerView.adapter = mAdapter
        binding.patientAppointmentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.patientAppointmentRecyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.patientAppointmentRecyclerView.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
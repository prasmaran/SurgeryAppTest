package com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.databinding.FragmentDoctorPatientWoundFeedbackBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.ui.dialog_fragments.PatientDetailFeedback
import com.example.surgeryapptest.ui.dialog_fragments.WoundFeedbackFrag
import com.example.surgeryapptest.ui.interfaces.SendFeedback
import com.example.surgeryapptest.utils.adapter.FeedbackListAdapter
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.constant.DialogFragConstants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.doctor.WoundFeedbackListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class DoctorPatientWoundFeedback : Fragment(), SendFeedback {

    private var _binding: FragmentDoctorPatientWoundFeedbackBinding? = null
    private val binding get() = _binding!!

    private lateinit var woundFeedbackListViewModel: WoundFeedbackListViewModel
    private lateinit var networkListener: NetworkListener
    private val feedbackAdapter by lazy { FeedbackListAdapter() }

    private var woundImageEntryId: String = ""
    private var currentPatientDetailFeedback = PatientDetailFeedback("", "", "")
    private lateinit var doctorFeedback: String
    private lateinit var doctorID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        woundFeedbackListViewModel =
            ViewModelProvider(requireActivity()).get(WoundFeedbackListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorPatientWoundFeedbackBinding.inflate(inflater, container, false)
        val view = binding.root


        val args = arguments
        val woundBundleFeedback: WoundImage? = args?.getParcelable("woundImageBundle")
        woundImageEntryId = woundBundleFeedback?.entryID.toString()
        doctorID = woundBundleFeedback?.doctorAssigned.toString()

        // Set patient details here
        currentPatientDetailFeedback.patientName = woundBundleFeedback?.mName.toString()
        currentPatientDetailFeedback.woundTitle = woundBundleFeedback?.progressTitle.toString()
        currentPatientDetailFeedback.painRate = woundBundleFeedback?.quesPain.toString()

        setupRecyclerView()
        swipeToRefresh()

        woundFeedbackListViewModel.readBackOnline.observe(viewLifecycleOwner, {
            woundFeedbackListViewModel.backOnline = it
        })

        // Pop up feedback fragment
        binding.sendFeedbackBtn.setOnClickListener {
            showFeedbackDialogFrag(currentPatientDetailFeedback)
        }

        // Listen to network connection
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener_PatientListFrag", status.toString())
                    woundFeedbackListViewModel.networkStatus = status
                    woundFeedbackListViewModel.showNetworkStatus()
                    requestFeedbackList(woundImageEntryId)
                }
        }

        AppUtils.showToast(requireContext(), "Entry ID: $woundImageEntryId")
        return view
    }

    private fun swipeToRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {

            woundFeedbackListViewModel.getWoundFeedbackList(woundImageEntryId)
            woundFeedbackListViewModel.feedbackListResponse.observe(
                viewLifecycleOwner,
                { response ->
                    when (response) {
                        is NetworkResult.Success -> {
                            setErrorAttributesVisible(true)
                            val feedbackListResponse = response.data?.message.toString()
//                        Toast.makeText(
//                            requireContext(),
//                            feedbackListResponse,
//                            Toast.LENGTH_SHORT
//                        ).show()
                            binding.swipeToRefresh.isRefreshing = false
                            hideShimmerEffect()
                            response.data?.let { feedbackAdapter.setData(it) }
                        }
                        is NetworkResult.Error -> {
                            setErrorAttributesVisible(false)
                            val feedbackListResponse = response.message.toString()
                            Toast.makeText(
                                requireContext(),
                                feedbackListResponse,
                                //response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            hideShimmerEffect()
                            binding.swipeToRefresh.isRefreshing = false
                        }
                        is NetworkResult.Loading -> {
                            showShimmerEffect()
                            binding.swipeToRefresh.isRefreshing = true
                        }
                    }
                })
        }
    }

    private fun requestFeedbackList(woundImageEntryId: String) {

        woundFeedbackListViewModel.getWoundFeedbackList(woundImageEntryId)
        woundFeedbackListViewModel.feedbackListResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    setErrorAttributesVisible(true)
                    val feedbackListResponse = response.data?.message.toString()
//                    Toast.makeText(
//                        requireContext(),
//                        feedbackListResponse,
//                        Toast.LENGTH_SHORT
//                    ).show()
                    hideShimmerEffect()
                    response.data?.let { feedbackAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    setErrorAttributesVisible(false)
                    val feedbackListResponse = response.message.toString()
                    Toast.makeText(
                        requireContext(),
                        feedbackListResponse,
                        //response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    hideShimmerEffect()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    private fun sendFeedback(params: Map<String, String>) {

        woundFeedbackListViewModel.postFeedback(params)
        woundFeedbackListViewModel.sendFeedbackResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val sendFeedbackResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        sendFeedbackResponse,
                        Toast.LENGTH_SHORT
                    ).show()
                    requestFeedbackList(woundImageEntryId)
                }
                is NetworkResult.Error -> {
                    val sendFeedbackResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        sendFeedbackResponse,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun showFeedbackDialogFrag(patientDetail: PatientDetailFeedback) {
        AppUtils.showDialogFragment(
            WoundFeedbackFrag.newInstance(patientDetail),
            childFragmentManager,
            DialogFragConstants.WOUND_IMAGE_FEEDBACK_FRAG.key
        )
    }

    override fun sendData(feedback: String, isEmpty: Boolean) {
        doctorFeedback = feedback
        if (isEmpty) {
            AppUtils.showToast(requireContext(), "Nothing was sent as feedback")
        } else {
            // AppUtils.showToast(requireContext(), doctorFeedback)

            val feedbackParams: Map<String, String> = mapOf(
                "progressEntryID" to woundImageEntryId,
                "feedbackText" to doctorFeedback,
                "doctorID" to doctorID,
                "patientName" to currentPatientDetailFeedback.patientName
            )
            sendFeedback(feedbackParams)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFeedbackList.adapter = feedbackAdapter
        binding.recyclerViewFeedbackList.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun setErrorAttributesVisible(hasList: Boolean) {
        if (hasList) {
            binding.noFeedbackImage.visibility = View.GONE
            binding.noFeedbackTv.visibility = View.GONE
            binding.swipeToRefreshTv.visibility = View.GONE
        } else {
            binding.noFeedbackImage.visibility = View.VISIBLE
            binding.noFeedbackTv.visibility = View.VISIBLE
            binding.swipeToRefreshTv.visibility = View.VISIBLE
        }
    }

    private fun showShimmerEffect() {
        binding.recyclerViewFeedbackList.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerViewFeedbackList.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
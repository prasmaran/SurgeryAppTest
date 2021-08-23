package com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.ui.dialog_fragments.PatientDetailFeedback
import com.example.surgeryapptest.ui.dialog_fragments.WoundFeedbackFrag
import com.example.surgeryapptest.ui.interfaces.SendFeedback
import com.example.surgeryapptest.utils.adapter.Adapter
import com.example.surgeryapptest.utils.adapter.FeedbackListAdapter
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.constant.DialogFragConstants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.doctor.WoundFeedbackListViewModel
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_doctor_patient_list.view.*
import kotlinx.android.synthetic.main.fragment_doctor_patient_wound_feedback.view.*
import kotlinx.android.synthetic.main.fragment_patient_progress_books.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class DoctorPatientWoundFeedback : Fragment(), SendFeedback {

    private lateinit var dwView: View
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
        dwView = inflater.inflate(R.layout.fragment_doctor_patient_wound_feedback, container, false)

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
        dwView.send_feedback_btn.setOnClickListener {
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

        return dwView
    }

    private fun swipeToRefresh() {
        dwView.swipeToRefresh.setOnRefreshListener {

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
                            dwView.swipeToRefresh.isRefreshing = false
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
                            dwView.swipeToRefresh.isRefreshing = false
                        }
                        is NetworkResult.Loading -> {
                            showShimmerEffect()
                            dwView.swipeToRefresh.isRefreshing = true
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
                }
                is NetworkResult.Error -> {
                    val sendFeedbackResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        sendFeedbackResponse,
                        Toast.LENGTH_SHORT
                    ).show()
                    swipeToRefresh()
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
        dwView.recyclerView_FeedbackList.adapter = feedbackAdapter
        dwView.recyclerView_FeedbackList.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun setErrorAttributesVisible(hasList: Boolean) {
        if (hasList) {
            dwView.no_feedback_image.visibility = View.GONE
            dwView.no_feedback_tv.visibility = View.GONE
            dwView.swipe_to_refresh_tv.visibility = View.GONE
        } else {
            dwView.no_feedback_image.visibility = View.VISIBLE
            dwView.no_feedback_tv.visibility = View.VISIBLE
            dwView.swipe_to_refresh_tv.visibility = View.VISIBLE
        }
    }

    private fun showShimmerEffect() {
        dwView.recyclerView_FeedbackList.showShimmer()
    }

    private fun hideShimmerEffect() {
        dwView.recyclerView_FeedbackList.hideShimmer()
    }


}
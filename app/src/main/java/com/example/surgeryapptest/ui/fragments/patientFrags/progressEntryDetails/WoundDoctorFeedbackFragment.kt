package com.example.surgeryapptest.ui.fragments.patientFrags.progressEntryDetails

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
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.utils.adapter.FeedbackListAdapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.WoundDetailsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_wound_doctor_feedback.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WoundDoctorFeedbackFragment : Fragment() {

    private lateinit var updateUploadedEntryViewModel: WoundDetailsFragmentViewModel
    private lateinit var pwView: View
    private lateinit var networkListener: NetworkListener
    private val feedbackAdapter by lazy { FeedbackListAdapter() }

    private var woundID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        updateUploadedEntryViewModel =
            ViewModelProvider(requireActivity()).get(WoundDetailsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        pwView = inflater.inflate(R.layout.fragment_wound_doctor_feedback, container, false)


        val args = arguments
        val myBundle: AllProgressBookEntryItem? = args?.getParcelable("progressEntryBundle")

        // Setup image, title, description, entryID
        if (myBundle != null) {
            woundID = myBundle.entryID.toString()
        }

        setupRecyclerView()
        swipeToRefresh()

        updateUploadedEntryViewModel.readBackOnline.observe(viewLifecycleOwner, {
            updateUploadedEntryViewModel.backOnline = it
        })

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener_PatientListFrag", status.toString())
                    updateUploadedEntryViewModel.networkStatus = status
                    updateUploadedEntryViewModel.showNetworkStatus()
                    requestFeedbackList(woundID)
                }
        }

        return pwView
    }

    private fun swipeToRefresh() {
        pwView.pf_swipeToRefresh.setOnRefreshListener {
            updateUploadedEntryViewModel.getWoundFeedbackList(woundID)
            updateUploadedEntryViewModel.feedbackListResponse.observe(
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
                            pwView.pf_swipeToRefresh.isRefreshing = false
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
                            pwView.pf_swipeToRefresh.isRefreshing = false
                        }
                        is NetworkResult.Loading -> {
                            showShimmerEffect()
                            pwView.pf_swipeToRefresh.isRefreshing = true
                        }
                    }
                })
        }
    }

    private fun requestFeedbackList(woundImageEntryId: String) {

        updateUploadedEntryViewModel.getWoundFeedbackList(woundImageEntryId)
        updateUploadedEntryViewModel.feedbackListResponse.observe(viewLifecycleOwner, { response ->
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


    private fun setupRecyclerView() {
        pwView.pf_recyclerView_FeedbackList.adapter = feedbackAdapter
        pwView.pf_recyclerView_FeedbackList.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun setErrorAttributesVisible(hasList: Boolean) {
        if (hasList) {
            pwView.pf_no_feedback_image.visibility = View.GONE
            pwView.pf_no_feedback_tv.visibility = View.GONE
            pwView.pf_swipe_to_refresh_tv.visibility = View.GONE
        } else {
            pwView.pf_no_feedback_image.visibility = View.VISIBLE
            pwView.pf_no_feedback_tv.visibility = View.VISIBLE
            pwView.pf_swipe_to_refresh_tv.visibility = View.VISIBLE
        }
    }

    private fun showShimmerEffect() {
        pwView.pf_recyclerView_FeedbackList.showShimmer()
    }

    private fun hideShimmerEffect() {
        pwView.pf_recyclerView_FeedbackList.hideShimmer()
    }


}
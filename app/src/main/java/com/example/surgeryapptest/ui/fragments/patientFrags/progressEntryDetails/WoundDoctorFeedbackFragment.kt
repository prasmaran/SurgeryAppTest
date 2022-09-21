package com.example.surgeryapptest.ui.fragments.patientFrags.progressEntryDetails

import android.annotation.SuppressLint
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
import com.example.surgeryapptest.databinding.FragmentWoundDoctorFeedbackBinding
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.utils.adapter.FeedbackListAdapter
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.WoundDetailsFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WoundDoctorFeedbackFragment : Fragment() {

    private var _binding: FragmentWoundDoctorFeedbackBinding? = null
    private val binding get() = _binding!!

    private lateinit var updateUploadedEntryViewModel: WoundDetailsFragmentViewModel
    private lateinit var networkListener: NetworkListener
    private val feedbackAdapter by lazy { FeedbackListAdapter() }

    private var woundID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        updateUploadedEntryViewModel =
            ViewModelProvider(requireActivity()).get(WoundDetailsFragmentViewModel::class.java)
    }

    @SuppressLint("LongLogTag", "NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWoundDoctorFeedbackBinding.inflate(inflater, container, false)
        val view = binding.root



        val args = arguments
        val myBundle: AllProgressBookEntryItem? = args?.getParcelable("progressEntryBundle")

        // Setup image, title, description, entryID
        if (myBundle != null) {
            woundID = myBundle.entryID.toString()
        }

        setupRecyclerView()
        swipeToRefresh()

        updateUploadedEntryViewModel.readBackOnline.observe(viewLifecycleOwner) {
            updateUploadedEntryViewModel.backOnline = it
        }

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

        return view
    }

    @SuppressLint("NewApi")
    private fun swipeToRefresh() {
        binding.pfSwipeToRefresh.setOnRefreshListener {
            updateUploadedEntryViewModel.getWoundFeedbackList(woundID)
            updateUploadedEntryViewModel.feedbackListResponse.observe(
                viewLifecycleOwner
            ) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        setErrorAttributesVisible(true)
                        val feedbackListResponse = response.data?.message.toString()
//                        Toast.makeText(
//                            requireContext(),
//                            feedbackListResponse,
//                            Toast.LENGTH_SHORT
//                        ).show()
                        binding.pfSwipeToRefresh.isRefreshing = false
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
                        binding.pfSwipeToRefresh.isRefreshing = false
                    }
                    is NetworkResult.Loading -> {
                        showShimmerEffect()
                        binding.pfSwipeToRefresh.isRefreshing = true
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun requestFeedbackList(woundImageEntryId: String) {

        updateUploadedEntryViewModel.getWoundFeedbackList(woundImageEntryId)
        updateUploadedEntryViewModel.feedbackListResponse.observe(viewLifecycleOwner) { response ->
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
        }
    }


    private fun setupRecyclerView() {
        binding.pfRecyclerViewFeedbackList.adapter = feedbackAdapter
        binding.pfRecyclerViewFeedbackList.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun setErrorAttributesVisible(hasList: Boolean) {
        if (hasList) {
            binding.pfNoFeedbackImage.visibility = View.GONE
            binding.pfNoFeedbackTv.visibility = View.GONE
            binding.pfSwipeToRefreshTv.visibility = View.GONE
        } else {
            binding.pfNoFeedbackImage.visibility = View.VISIBLE
            binding.pfNoFeedbackTv.visibility = View.VISIBLE
            binding.pfSwipeToRefreshTv.visibility = View.VISIBLE
        }
    }

    private fun showShimmerEffect() {
        binding.pfRecyclerViewFeedbackList.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.pfRecyclerViewFeedbackList.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
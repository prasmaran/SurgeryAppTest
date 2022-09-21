package com.example.surgeryapptest.ui.fragments.patientFrags

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.databinding.FragmentGeneralInfoBinding
import com.example.surgeryapptest.utils.adapter.GeneralInfoAdapter
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.GeneralListViewModel

class GeneralInfoFragment : Fragment() {

    private var _binding: FragmentGeneralInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var generalListViewModel: GeneralListViewModel
    private val mAdapter by lazy { GeneralInfoAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generalListViewModel =
            ViewModelProvider(requireActivity()).get(GeneralListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGeneralInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()

        requestApiData()

        return view
    }

    @SuppressLint("NewApi")
    private fun requestApiData() {
        generalListViewModel.generalInfoAPI()
        generalListViewModel.generalListResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val generalInfoResponse = response.data?.message.toString()
                    Toast.makeText(
                        requireContext(),
                        generalInfoResponse,
                        //response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        requireContext(),
                        response.data?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.generalInfoRecyclerView.adapter = mAdapter
        binding.generalInfoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.generalInfoRecyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.generalInfoRecyclerView.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
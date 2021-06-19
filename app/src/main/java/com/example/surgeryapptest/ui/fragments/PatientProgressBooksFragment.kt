package com.example.surgeryapptest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.utils.adapter.Adapter
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_patient_progress_books.view.*
import java.sql.Timestamp
import java.util.*

@AndroidEntryPoint
class PatientProgressBooksFragment : Fragment() {

    private lateinit var mainViewModel: MainActivityViewModel
    private val mAdapter by lazy { Adapter() }
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        mainViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_patient_progress_books, container, false)
        setupRecyclerView()
        requestApiData()
        fabUploadImage()
        
        return mView
    }

    private fun fabUploadImage(){
        mView.floatingActionButton.setOnClickListener {
            //println("FAB has been clicked to upload image from phone ...")
            //findNavController().navigate(R.id.uploadNewEntryFragment)
            val action = PatientProgressBooksFragmentDirections.actionPatientProgressBooksFragmentToUploadNewEntryFragment()
            findNavController().navigate(action)
        }
    }

    private fun requestApiData(){
        mainViewModel.getAllProgressEntry()
        mainViewModel.allProgressEntryResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    private fun setupRecyclerView(){
        mView.recyclerView.adapter = mAdapter
        mView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect(){
        mView.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect(){
        mView.recyclerView.hideShimmer()
    }
}
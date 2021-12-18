package com.example.surgeryapptest.ui.fragments.researcherFrags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentDoctorSelectedPatientProgressBookBinding
import com.example.surgeryapptest.databinding.FragmentResearcherPatientListBinding
import com.example.surgeryapptest.databinding.FragmentResearcherSelectedPatientProgressBookBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.ui.fragments.doctorFrags.DoctorSelectedPatientProgressBookFragmentArgs
import com.example.surgeryapptest.utils.adapter.PatientProgressBookListAdapter
import com.example.surgeryapptest.utils.adapter.ResearcherPatientProgressBookListAdapter


class ResearcherSelectedPatientProgressBookFragment : Fragment() {

//    private var _binding: FragmentResearcherSelectedPatientProgressBookBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        _binding = FragmentResearcherSelectedPatientProgressBookBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        return view
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private var _binding: FragmentDoctorSelectedPatientProgressBookBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DoctorSelectedPatientProgressBookFragmentArgs>()
    private var selectedPatientProgressBook: PatientName? = null
    private val mAdapter by lazy { ResearcherPatientProgressBookListAdapter() }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorSelectedPatientProgressBookBinding.inflate(inflater, container, false)
        val view = binding.root

        selectedPatientProgressBook = args.patientProgressBook

        (activity as AppCompatActivity).supportActionBar?.title = "${args.patientProgressBook.woundImages[0].mName}'s progress book"

        setupRecyclerView()
        setListData()

        return view
    }

    private fun setListData(){
        mAdapter.setData(selectedPatientProgressBook!!)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSelectedPatientProgressBook.adapter = mAdapter
        binding.recyclerViewSelectedPatientProgressBook.layoutManager = LinearLayoutManager(requireContext())
        //showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.recyclerViewSelectedPatientProgressBook.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerViewSelectedPatientProgressBook.hideShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
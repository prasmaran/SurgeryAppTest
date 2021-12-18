package com.example.surgeryapptest.ui.fragments.researcherFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentResearcherHomeBinding

class ResearcherHomeFragment : Fragment() {

    private var _binding: FragmentResearcherHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResearcherHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        navigateToPatientList()
        navigateToProfile()

        return view
    }

    private fun navigateToProfile() {
        binding.cardViewProfileResearcher.setOnClickListener {
            findNavController().navigate(R.id.researcherProfileFragment)
        }
    }

    private fun navigateToPatientList() {
        binding.cardViewPatientsListResearcher.setOnClickListener {
            findNavController().navigate(R.id.researcherPatientListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
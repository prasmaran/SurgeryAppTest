package com.example.surgeryapptest.ui.fragments.researcherFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentResearcherHomeBinding
import com.example.surgeryapptest.ui.fragments.doctorFrags.DoctorHomeFragmentDirections
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ResearcherHomeFragment : Fragment() {

    private var _binding: FragmentResearcherHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userProfileViewModel: UserProfileFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileViewModel =
            ViewModelProvider(requireActivity()).get(UserProfileFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResearcherHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.elevation = 0f

        readSavedUserProfileDetails()
        navigateToPatientList()
        navigateToToDoList()

        return view
    }

    private fun readSavedUserProfileDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                val welcomeBack = getString(R.string.welcome_back_user) + values.userName + " 👋"
                binding.researcherWelcomeTv.text = welcomeBack
            }
        }
    }

    private fun navigateToToDoList() {
        binding.cardViewNoteResearcher.setOnClickListener {
            val action =
                ResearcherHomeFragmentDirections.actionResearcherHomeFragmentToToDoActivity()
            findNavController().navigate(action)
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
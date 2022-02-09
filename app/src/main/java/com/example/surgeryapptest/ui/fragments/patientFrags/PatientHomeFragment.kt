package com.example.surgeryapptest.ui.fragments.patientFrags

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
import com.example.surgeryapptest.databinding.FragmentPatientHomeBinding
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class PatientHomeFragment : Fragment() {

    private var _binding: FragmentPatientHomeBinding? = null
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
        _binding = FragmentPatientHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.elevation = 0f

        readSavedUserProfileDetails()

        navigateToProgressBook()
        navigateToGeneralInfo()
        navigateToEmergencyCall()
        navigateToNotification()

        return view
    }

    private fun readSavedUserProfileDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                val welcomeBack = getString(R.string.welcome_back_user) + values.userName + " 👋"
                binding.patientWelcomeTv.text = welcomeBack
            }
        }
    }

    private fun navigateToProgressBook() {
        binding.cardViewProgressBook.setOnClickListener {
            findNavController().navigate(R.id.patientProgressBooksFragment)
        }
    }

    // have not implemented yet
    private fun navigateToNotification() {
        binding.cardViewNotification.setOnClickListener {
            val action = PatientHomeFragmentDirections.actionPatientHomeFragmentToStreamChatActivity()
            findNavController().navigate(action)
        }
    }

    private fun navigateToEmergencyCall() {
        binding.cardViewEmergency.setOnClickListener {
            val action = PatientHomeFragmentDirections.actionPatientHomeFragmentToPatientEmergencyCallFragment()
            findNavController().navigate(action)
        }
    }

    // have not implemented yet
    private fun navigateToGeneralInfo() {
        binding.cardViewGeneralInfo.setOnClickListener {
            val action = PatientHomeFragmentDirections.actionPatientHomeFragmentToGeneralInfoFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
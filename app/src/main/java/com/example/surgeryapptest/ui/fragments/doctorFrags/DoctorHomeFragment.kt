package com.example.surgeryapptest.ui.fragments.doctorFrags

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
import com.example.surgeryapptest.databinding.FragmentDoctorHomeBinding
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientHomeFragmentDirections
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class DoctorHomeFragment : Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
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
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.elevation = 0f

        readSavedUserProfileDetails()

        navigateToPatientList()
        navigateToChats()
        navigateToAppointments()
        navigateToProfile()

        return view
    }

    private fun readSavedUserProfileDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                val welcomeBack = getString(R.string.welcome_back_user) + "Dr. " + values.userName + " 👋"
                binding.doctorWelcomeTv.text = welcomeBack
            }
        }
    }

    private fun navigateToPatientList() {
        binding.cardViewPatientsList.setOnClickListener {
            findNavController().navigate(R.id.doctorPatientListFragment)
        }
    }

    private fun navigateToChats() {
        binding.cardViewChats.setOnClickListener {
            val action = DoctorHomeFragmentDirections.actionDoctorHomeFragmentToStreamChatActivity2()
            findNavController().navigate(action)
        }
    }

    private fun navigateToAppointments() {
        binding.cardViewAppointments.setOnClickListener {
            findNavController().navigate(R.id.doctorAppointmentFragment)
        }
    }

    private fun navigateToProfile() {
        binding.cardViewProfile.setOnClickListener {
            findNavController().navigate(R.id.doctorProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
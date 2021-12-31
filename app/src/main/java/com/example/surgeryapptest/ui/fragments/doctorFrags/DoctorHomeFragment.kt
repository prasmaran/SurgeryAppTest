package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentDoctorHomeBinding
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientHomeFragmentDirections
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar


class DoctorHomeFragment : Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        navigateToPatientList()
        navigateToChats()
        navigateToAppointments()
        navigateToProfile()

        return view
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
            binding.doctorHomeFragmentLayout.showSnackBar("This feature has not been implemented yet")
        }
    }

    private fun navigateToProfile() {
        binding.cardViewProfile.setOnClickListener {
            binding.doctorHomeFragmentLayout.showSnackBar("This feature has not been implemented yet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.example.surgeryapptest.ui.fragments.patientFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientHomeBinding
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar


class PatientHomeFragment : Fragment() {

    private var _binding: FragmentPatientHomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        navigateToProgressBook()
        navigateToGeneralInfo()
        navigateToEmergencyCall()
        navigateToNotification()

        return view
    }

    private fun navigateToProgressBook() {
        binding.cardViewProgressBook.setOnClickListener {
            findNavController().navigate(R.id.patientProgressBooksFragment)
        }
    }

    // have not implemented yet
    private fun navigateToNotification() {
        binding.cardViewNotification.setOnClickListener {
            binding.patientHomeFragmentLayout
                .showSnackBar("This feature has not been implemented yet")
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
            binding.patientHomeFragmentLayout
                .showSnackBar("This feature has not been implemented yet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
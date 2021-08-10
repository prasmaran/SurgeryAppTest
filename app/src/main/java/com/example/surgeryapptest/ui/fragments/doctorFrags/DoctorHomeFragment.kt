package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import kotlinx.android.synthetic.main.fragment_doctor_home.*
import kotlinx.android.synthetic.main.fragment_doctor_home.view.*


class DoctorHomeFragment : Fragment() {

    private lateinit var dhView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dhView = inflater.inflate(R.layout.fragment_doctor_home, container, false)

        navigateToPatientList()
        navigateToChats()
        navigateToAppointments()
        navigateToProfile()

        return dhView
    }

    private fun navigateToPatientList() {
        dhView.cardViewPatientsList.setOnClickListener {
            findNavController().navigate(R.id.doctorPatientListFragment)
        }
    }

    private fun navigateToChats() {
        dhView.cardViewChats.setOnClickListener {
            doctorHomeFragmentLayout.showSnackBar("This feature has not been implemented yet")
        }
    }

    private fun navigateToAppointments() {
        dhView.cardViewAppointments.setOnClickListener {
            doctorHomeFragmentLayout.showSnackBar("This feature has not been implemented yet")
        }
    }

    private fun navigateToProfile() {
        dhView.cardViewProfile.setOnClickListener {
            doctorHomeFragmentLayout.showSnackBar("This feature has not been implemented yet")
        }
    }

}
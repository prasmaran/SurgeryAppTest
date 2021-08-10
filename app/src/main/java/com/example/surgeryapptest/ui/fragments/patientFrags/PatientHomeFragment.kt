package com.example.surgeryapptest.ui.fragments.patientFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import kotlinx.android.synthetic.main.fragment_patient_home.view.*


class PatientHomeFragment : Fragment() {

    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_patient_home, container, false)

        navigateToProgressBook()
        navigateToGeneralInfo()
        navigateToEmergencyCall()
        navigateToNotification()

        return mView
    }

    private fun navigateToProgressBook() {
        mView.cardViewProgressBook.setOnClickListener {
            findNavController().navigate(R.id.patientProgressBooksFragment)
        }
    }

    // have not implemented yet
    private fun navigateToNotification() {
        mView.cardViewNotification.setOnClickListener {
            mView.patientHomeFragmentLayout
                .showSnackBar("This feature has not been implemented yet")
        }
    }

    private fun navigateToEmergencyCall() {
        mView.cardViewEmergency.setOnClickListener {
            val action = PatientHomeFragmentDirections.actionPatientHomeFragmentToPatientEmergencyCallFragment()
            findNavController().navigate(action)
        }
    }

    // have not implemented yet
    private fun navigateToGeneralInfo() {
        mView.cardViewGeneralInfo.setOnClickListener {
            mView.patientHomeFragmentLayout
                .showSnackBar("This feature has not been implemented yet")
        }
    }

}
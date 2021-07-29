package com.example.surgeryapptest.ui.fragments.patientFrags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.surgeryapptest.R
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import com.example.surgeryapptest.view_models.patient.MainActivityViewModel
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import kotlinx.android.synthetic.main.fragment_patient_profile.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PatientProfileFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var userProfileViewModel: UserProfileFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        userProfileViewModel = ViewModelProvider(requireActivity()).get(UserProfileFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_patient_profile, container, false)

        readSavedUserProfileDetails()

        return mView
    }

    private fun readSavedUserProfileDetails(){
        var uName = ""
        var uIC = ""
        var uGender = ""
        var uType = ""

        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                mView.user_name_main_tv.text = values.userName
                mView.user_type_tv.text = values.userType
                mView.user_gender_tv.text = values.userGender
                mView.user_registration_id_tv.text = values.userIcNumber

                when(values.userGender) {
                    "M" -> mView.user_icon_pic.setImageResource(R.drawable.ic_user_male)
                    "F" -> mView.user_icon_pic.setImageResource(R.drawable.ic_user_female)
                }
            }
        }

        println("RETRIEVED DATA FROM DS IN PROGRESS BOOK: $uName $uIC $uGender $uType")
    }
}
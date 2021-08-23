package com.example.surgeryapptest.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_patient_profile.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PatientProfileFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var mView: View
    private lateinit var userProfileViewModel: UserProfileFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        userProfileViewModel = ViewModelProvider(requireActivity()).get(UserProfileFragmentViewModel::class.java)

        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_patient_profile, container, false)

        readSavedUserProfileDetails()

        mView.logOut_btn.setOnClickListener {
            createAlertDialogSignOut()
        }

        return mView
    }

    private fun readSavedUserProfileDetails(){
        val uName = ""
        val uIC = ""
        val uGender = ""
        val uType = ""

        // TODO: Create API to update user contact details
        /** Listen to the changes and update in Ui
         * Send changes to server and return the updated response */
        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                mView.user_name_main_tv.text = values.userName
                mView.user_gender_tv.text = values.userGender
                mView.user_registration_id_tv.text = values.userIcNumber

                when(values.userGender) {
                    "M" -> mView.user_icon_pic.setImageResource(R.drawable.ic_user_male)
                    "F" -> mView.user_icon_pic.setImageResource(R.drawable.ic_user_female)
                }

                when(values.userType) {
                    "A" -> mView.user_type_tv.text = Constants.ADMIN
                    "P" -> mView.user_type_tv.text = Constants.PATIENT
                    "D" -> mView.user_type_tv.text = Constants.DOCTOR
                    "R" -> mView.user_type_tv.text = Constants.RESEARCHER
                    "DOP" -> mView.user_type_tv.text = Constants.DOP
                }

                println("RETRIEVED DATA FROM DS IN PROFILE FRAGMENT: ${values.userName} ${values.userID} ${values.userGender} ${values.userType} ")
            }
        }
    }

    private fun createAlertDialogSignOut() {
        //val builder = AlertDialog.Builder(requireContext())
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Confirm log out?")
        builder.setMessage("\nAre you sure you want to log out? All your locally saved data will be removed for privacy purposes.")
        builder.setIcon(R.drawable.ic_log_out)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            sessionManager.saveAuthToken(null)
            userProfileViewModel.deleteAllPreferences()
            goToLoginPage()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun goToLoginPage() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}
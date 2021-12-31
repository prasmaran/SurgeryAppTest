package com.example.surgeryapptest.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientProfileBinding
import com.example.surgeryapptest.ui.activity.LoginActivity
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.NetworkListener
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

class PatientProfileFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    // Trying View Binding in Fragment proper way
    private var _binding: FragmentPatientProfileBinding? = null
    private val binding get() = _binding!!

    //private late in it var mView: View
    private lateinit var userProfileViewModel: UserProfileFragmentViewModel
    private lateinit var networkListener: NetworkListener
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileViewModel =
            ViewModelProvider(requireActivity()).get(UserProfileFragmentViewModel::class.java)
        sessionManager = SessionManager(requireContext())
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPatientProfileBinding.inflate(inflater, container, false)
        val mView = binding.root

        checkNetworkStatus()
        readSavedUserProfileDetails()
        textInputLayout(false)

        binding.logOutBtn.setOnClickListener {
            createAlertDialogLogOut()
        }

        binding.updateUserDetailsBtn.setOnClickListener {
            toggleButtons(true)
            textInputLayout(true)
        }

        /**
         * Hidden until update button is clicked
         */
        binding.saveUserDetailsBtn.setOnClickListener {
            val userContact1 = binding.infoLayout2.editText?.text.toString()
            val userContact2 = binding.infoLayout3.editText?.text.toString()

            if (userContact1.isEmpty() || userContact1.length < 10 || userContact2.isEmpty() || userContact2.length < 10) {

                binding.infoLayout2.error = "Please enter valid phone number"
                binding.infoLayout3.error = "Please enter valid phone number"

            } else {
                userProfileViewModel.updateUserProfileDetails(userContact1, userContact2)
                updatePhoneNumber(userContact1, userContact2)
                //AppUtils.showToast(requireContext(), "$userContact1 -- $userContact2")
            }
        }

        return mView
    }

    private fun toggleButtons(toggle: Boolean) {
        if (toggle) {
            binding.updateUserDetailsBtn.visibility = View.GONE
            binding.saveUserDetailsBtn.visibility = View.VISIBLE
        } else {
            binding.updateUserDetailsBtn.visibility = View.VISIBLE
            binding.saveUserDetailsBtn.visibility = View.GONE
        }
    }

    private fun textInputLayout(set: Boolean) {
        binding.infoLayout2.isEnabled = set
        binding.infoLayout3.isEnabled = set
    }

    @SuppressLint("NewApi")
    private fun updatePhoneNumber(contact1: String, contact2: String) {

        userProfileViewModel.updateUserDetails(
            contact1.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            contact2.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            userID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        userProfileViewModel.updatedUserDetailResponse.observe(viewLifecycleOwner, { response ->

            when (response) {

                is NetworkResult.Success -> {

                    MotionToast.darkColorToast(
                        requireActivity(),
                        "Update Successful!",
                        response.data!!.message,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(),R.font.helvetica_regular))

                    toggleButtons(false)
                    textInputLayout(false)
                }

                is NetworkResult.Error -> {

                    MotionToast.darkColorToast(
                        requireActivity(),
                        "Update Failed!",
                        response.data!!.message,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(),R.font.helvetica_regular))

                    toggleButtons(false)
                    textInputLayout(false)
                }

                is NetworkResult.Loading -> {
                    //TODO: Add loading fragment here
                }
            }
        })

    }

    private fun readSavedUserProfileDetails() {

        // TODO: Create API to update user contact details
        /** Listen to the changes and update in Ui
         * Send changes to server and return the updated response */
        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                binding.userNameMainTv.text = values.userName
                binding.userGenderTv.text = values.userGender
                userID = values.userID
                binding.infoLayout1.editText!!.setText(values.userIcNumber)
                binding.infoLayout2.editText!!.setText(values.userContact1)
                binding.infoLayout3.editText!!.setText(values.userContact2)

//                binding.userRegistrationIdTv.text = values.userIcNumber
//                binding.userContact1Tv.text = values.userContact1
//                binding.userContact2Tv.text = values.userContact2

                when (values.userGender) {
                    "M" -> binding.userIconPic.setImageResource(R.drawable.ic_user_male)
                    "F" -> binding.userIconPic.setImageResource(R.drawable.ic_user_female)
                }

                when (values.userType) {
                    "P" -> binding.userTypeTv.text = Constants.PATIENT
                    "D" -> {
                        binding.noOfPhotosTitle.text = getString(R.string.doctor_patient_list)
                        binding.userTypeTv.text = Constants.DOCTOR
                    }
                    "R" -> {
                        binding.noOfPhotosTitle.text = getString(R.string.doctor_patient_list)
                        binding.userTypeTv.text = Constants.RESEARCHER
                    }
                }

                println(
                    "RETRIEVED DATA FROM DS IN PROFILE FRAGMENT: " +
                            "${values.userName} ${values.userID} ${values.userGender} ${values.userType} ${values.userContact1} ${values.userContact1}  "
                )
            }
        }

        userProfileViewModel.readNumberOfPhotos.observe(viewLifecycleOwner, {
            binding.noOfPhotosTv.text = it.toString()
        })
    }

    @SuppressLint("LongLogTag", "NewApi")
    private fun checkNetworkStatus() {
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener_UserProfileFrag", status.toString())
                    userProfileViewModel.networkStatus = status
                    userProfileViewModel.showNetworkStatus()
                    binding.updateUserDetailsBtn.isEnabled = status
                }
        }
    }

    @SuppressLint("NewApi")
    private fun createAlertDialogLogOut() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        //Fragments outlive their views. Make sure you clean up any references to the
        //binding class instance in the fragment's onDestroyView() method.
        _binding = null
    }

}
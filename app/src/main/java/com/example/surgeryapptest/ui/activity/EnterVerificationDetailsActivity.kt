package com.example.surgeryapptest.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityEnterVerificationDetailsBinding
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class EnterVerificationDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterVerificationDetailsBinding

    private val loginViewModel: LoginActivityViewModel by viewModels()

    private var countryCode: String = ""
    private var userPhone: String = ""
    private var formattedPhoneNo: String = ""
    private var validPhoneNumber: Boolean = false
    private var userRegistrationID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterVerificationDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Verification Details"
        supportActionBar?.subtitle = "Enter Registration ID & Phone Number"

        phoneNumberInputListener()

        binding.verifyDetailsBtn.setOnClickListener {
            if (binding.registrationIdEt.text.trim().length <= 5 || !validPhoneNumber) {
                AppUtils.showToast(this, "Enter valid registration ID and phone number")
            } else {
                userRegistrationID = binding.registrationIdEt.text.toString()
                val userDetailsParams: Map<String, String> = mapOf(
                    "userRegistrationID" to userRegistrationID,
                    "toUser" to formattedPhoneNo.substring(2)
                )
                userRegistrationPhoneNumber(userDetailsParams)
            }
        }

    }

    @SuppressLint("NewApi")
    private fun userRegistrationPhoneNumber(params: Map<String, String>) {

        loginViewModel.sendRegistrationAndPhone(params)

        loginViewModel.sendRegistrationIdPhoneNumber.observe(this@EnterVerificationDetailsActivity) { response ->
            binding.enterDetailsProgressBar.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {

                    binding.enterDetailsProgressBar.visibility = View.GONE
                    val userString = response.data?.message.toString()
                    MotionToast.darkColorToast(
                        this,
                        "Verification Successful!",
                        userString,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )

                    goToEnterOTP(formattedPhoneNo, userRegistrationID)
                }
                is NetworkResult.Error -> {

                    binding.enterDetailsProgressBar.visibility = View.GONE
                    MotionToast.darkColorToast(
                        this,
                        "Verification Failed!",
                        "Registration ID or phone number not found",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                }
                is NetworkResult.Loading -> {
                    //binding.enterDetailsProgressBar.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun goToEnterOTP(phoneNumber: String, registrationID: String) {
        val intent = Intent(this, EnterOTPActivity::class.java)
        intent.putExtra("formatted_phone_number", phoneNumber)
        intent.putExtra("user_registration_id", registrationID)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun phoneNumberInputListener() {
        binding.phoneNumber.addTextChangedListener {
            it?.let {
                if (it.length in 9..12) {
                    val mCountryCode = binding.countryCodePicker.selectedCountryNameCode
                    binding.phoneNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_check,
                        0
                    )
                    userPhone = it.toString()
                    countryCode = mCountryCode
                    formattedPhoneNo = AppUtils.formatPhoneNumberToE164(it.toString(), mCountryCode)
                    validPhoneNumber = true
                } else {
                    userPhone = ""
                    binding.phoneNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_error_,
                        0
                    )
                    validPhoneNumber = false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
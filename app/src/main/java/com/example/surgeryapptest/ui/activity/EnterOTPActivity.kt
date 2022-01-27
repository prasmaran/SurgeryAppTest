package com.example.surgeryapptest.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityEnterOtpactivityBinding
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EnterOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterOtpactivityBinding
    private var userRegistrationID: String = ""
    private var userPhoneNumber: String = ""

    private val loginViewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterOtpactivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Enter 4-digit Pin"
        supportActionBar?.subtitle = "Check your inbox in a few seconds"

        intent.getStringExtra("formatted_phone_number")?.let {
            binding.phoneNumberTv.text = it
            userPhoneNumber = it
        }
        intent.getStringExtra("user_registration_id")?.let {
            userRegistrationID = it
        }

        countDownTimer.start()

        // GenericTextWatcher here works only for moving to next EditText when a number is entered
        // first parameter is the current EditText and second parameter is next EditText
        binding.n1.addTextChangedListener(GenericTextWatcher(binding.n1, binding.n2))
        binding.n2.addTextChangedListener(GenericTextWatcher(binding.n2, binding.n3))
        binding.n3.addTextChangedListener(GenericTextWatcher(binding.n3, binding.n4))
        binding.n4.addTextChangedListener(GenericTextWatcher(binding.n4, null))

        // GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        // first parameter is the current EditText and second parameter is previous EditText
        binding.n1.setOnKeyListener(GenericKeyEvent(binding.n1, null))
        binding.n2.setOnKeyListener(GenericKeyEvent(binding.n2, binding.n1))
        binding.n3.setOnKeyListener(GenericKeyEvent(binding.n3, binding.n2))
        binding.n4.setOnKeyListener(GenericKeyEvent(binding.n4, binding.n3))

        binding.timerTv.setOnClickListener {
            countDownTimer.start()
            resendOTP()
            // Send the OTP again
            // After 30 seconds
        }

        binding.submitOtpBtn.setOnClickListener {
            if (validateTAC().trim().length == 4) {
                val otp = validateTAC()
                val userOTPDetailsParams: Map<String, String> = mapOf(
                    "toUser" to userPhoneNumber,
                    "verifyToken" to otp
                )
                sendOTPAndPhoneNumber(userOTPDetailsParams)
            } else {
                AppUtils.showToast(this, "Fill in all fields")
            }
        }
    }

    @SuppressLint("NewApi")
    private fun resendOTP() {
        val resendOtpParams: Map<String, String> = mapOf(
            "userRegistrationID" to userRegistrationID,
            "toUser" to userPhoneNumber.substring(2)
        )

        //AppUtils.showToast(this, resendOtpParams.toString())
        loginViewModel.sendRegistrationAndPhone(resendOtpParams)
    }

    @SuppressLint("NewApi")
    private fun sendOTPAndPhoneNumber(params: Map<String, String>) {

        loginViewModel.sendOTPAndPhone(params)

        loginViewModel.verifiedOTPResponse.observe(this@EnterOTPActivity, { response ->
            binding.otpProgressBar.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {

                    binding.otpProgressBar.visibility = View.GONE
                    val userString = response.data?.message.toString()
                    MotionToast.darkColorToast(
                        this,
                        "OTP Verification Successful!",
                        userString,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )

                    goToResetPassword()
                }
                is NetworkResult.Error -> {

                    binding.otpProgressBar.visibility = View.GONE
                    MotionToast.darkColorToast(
                        this,
                        "OTP Verification Failed!",
                        "Invalid PIN or expired PIN",
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
        })

    }

    private fun goToResetPassword() {
        val intent = Intent(this, EnterNewPasswordActivity::class.java)
        intent.putExtra("user_registration_for_reset", userRegistrationID)
        startActivity(intent)
    }

    private fun validateTAC(): String {
        return "${binding.n1.text}${binding.n2.text}${binding.n3.text}${binding.n4.text}"
    }

    private var countDownTimer = object : CountDownTimer(30000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            binding.timerTv.isClickable = false
            binding.timerTv.text = resources.getString(
                R.string.formatted_time,
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
            )
        }

        override fun onFinish() {
            binding.timerTv.isClickable = true
            binding.timerTv.text = resources.getString(R.string.get_otp_again)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

class GenericKeyEvent internal constructor(
    private val currentView: EditText,
    private val previousView: EditText?
) : View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.n1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }


}

class GenericTextWatcher internal constructor(
    private val currentView: View,
    private val nextView: View?
) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.n1 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.n2 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.n3 -> if (text.length == 1) nextView!!.requestFocus()
            //You can use EditText4 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

}

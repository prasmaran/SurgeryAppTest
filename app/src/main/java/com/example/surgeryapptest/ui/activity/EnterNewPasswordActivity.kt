package com.example.surgeryapptest.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityEnterNewPasswordBinding
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class EnterNewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterNewPasswordBinding
    private var userRegistrationID: String = ""

    private val loginViewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterNewPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        intent.getStringExtra("user_registration_for_reset")?.let {
            userRegistrationID = it
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Password Reset"
        supportActionBar?.subtitle = "Enter new password to reset"

        binding.doneReset.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.passwordSubmitBtn.setOnClickListener {
            val userPassword = binding.newPasswordLayout.editText?.text.toString()
            val userPassword2 = binding.confirmPasswordLayout.editText?.text.toString()

            if (userPassword.isEmpty() || userPassword.length < 6 || userPassword2.isEmpty() || userPassword2.length < 6) {
                binding.newPasswordLayout.error = "Please enter valid password"
                binding.confirmPasswordLayout.error = "Please enter valid password"
            } else {

                if (userPassword != userPassword2) {
                    AppUtils.showToast(this, "Password is not matching")
                } else {
                    val userResetPasswordParams: Map<String, String> = mapOf(
                        "userRegistration" to userRegistrationID,
                        "password" to userPassword,
                        "password1" to userPassword2
                    )
                    sendResetPassword(userResetPasswordParams)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun sendResetPassword(params: Map<String, String>) {

        loginViewModel.sendResetPassword(params)

        loginViewModel.passwordResetResponse.observe(this@EnterNewPasswordActivity, { response ->
            binding.resetProgressBar.visibility = View.VISIBLE
            when (response) {
                is NetworkResult.Success -> {
                    binding.resetProgressBar.visibility = View.GONE
                    val userString = response.data?.message.toString()
                    MotionToast.darkColorToast(
                        this,
                        "Password Reset Successful!",
                        userString,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )

                    goToLoginPage()
                }
                is NetworkResult.Error -> {
                    binding.resetProgressBar.visibility = View.GONE
                    MotionToast.darkColorToast(
                        this,
                        "Password Reset Failed!",
                        response.data?.message.toString(),
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

    private fun goToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
package com.example.surgeryapptest.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityLoginBinding
import com.example.surgeryapptest.ui.activity.doctorActivities.MainActivityDoctor
import com.example.surgeryapptest.ui.activity.patientActivities.MainActivity
import com.example.surgeryapptest.ui.activity.researcherActivities.ResearcherMainActivity
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Testing data store values
    private var userName = ""
    private var userID = ""
    private var userIcNumber = ""
    private var userGender = ""
    private var userType = ""
    private var userContact1 = ""
    private var userContact2 = ""

    // Replace later with Data Store
    @Inject
    lateinit var sessionManager: SessionManager

    private val loginViewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //loginViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)
        sessionManager = SessionManager(this)

        // set up the authentication later
        binding.loginBtn.setOnClickListener {
            hideSoftKeyboard(this)
            val username = binding.usernameLoginEt.text.toString()
            val password = binding.passwordLoginEt.text.toString()
            if (fieldsValidation(username, password)) {
                //AppUtils.showToast(this, "Successfully logged in")
                //setCheckIcon(true)
                //params or Multi.part
                val userParams: Map<String, String> = mapOf(
                    "username" to username,
                    "password" to password
                )
                userLogin(userParams)
            } else {
                setCheckIcon(false)
                binding.usernameLoginEt.text = null
                binding.passwordLoginEt.text = null
            }
        }

        // go to enter otp --> reset password
        binding.forgotPasswordTv.setOnClickListener {
            goToEnterDetails()
        }

    }

    // TODO: Delete after checking the deleted DS values
    private fun readSavedUserProfileDetails() {
    }

    private fun goToEnterDetails() {
        val intent = Intent(this, EnterVerificationDetailsActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun goToMain(userType: String) {
        when (userType) {
            "P" -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            "D" -> {
                val intent = Intent(this, MainActivityDoctor::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            else -> {
                val intent = Intent(this, ResearcherMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun userLogin(params: Map<String, String>) {

        loginViewModel.loginUser(params)

        loginViewModel.loginResponse.observe(this@LoginActivity, { response ->

            binding.loginProgressBar.visibility = View.VISIBLE

            when (response) {
                is NetworkResult.Success -> {

                    binding.loginProgressBar.visibility = View.GONE

                    setCheckIcon(true)

                    // save the auth token to sharedPrefs
                    sessionManager.saveAuthToken(response.data?.accessToken.toString())
                    // loginViewModel.saveUserAccessToken(response.data?.accessToken.toString())

//                    Toast.makeText(
//                        this@LoginActivity,
//                        response.data?.message.toString() + " " + response.data?.accessToken.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()

                    userName = response.data?.result?.get(0)?.mName.toString()
                    userID = response.data?.result?.get(0)?.mId.toString()
                    userIcNumber = response.data?.result?.get(0)?.mIc.toString()
                    userGender = response.data?.result?.get(0)?.mGender.toString()
                    userType = response.data?.result?.get(0)?.mType.toString()
                    userContact1 = response.data?.result?.get(0)?.mContact1.toString()
                    userContact2 = response.data?.result?.get(0)?.mContact2.toString()

                    // save user logged in flag = true
                    loginViewModel.setUserLoggedIn(true)
                    // save user profile details to data store
                    loginViewModel.saveUserProfileDetails(
                        userName, userID, userIcNumber, userGender, userType, userContact1, userContact2
                    )
                    // save access token to data store
                    loginViewModel.saveUserAccessToken(response.data?.accessToken.toString())

                    // get all the user detail
                    // println("Access Token: " + sessionManager.fetchAuthToken())

                    val userString = response.data?.message.toString()
                        //"${response.data?.result?.get(0)?.mName} = ${response.data?.result?.get(0)?.mType}"

                    MotionToast.darkColorToast(
                        this,
                        "Login Successful!",
                        userString,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this,R.font.helvetica_regular))

                    goToMain(userType)
                }
                is NetworkResult.Error -> {

                    val userString = response.data?.message.toString()

                    binding.loginProgressBar.visibility = View.GONE

                    setCheckIcon(false)

                    MotionToast.darkColorToast(
                        this,
                        "Login Failed!",
                        "Incorrect Username or Password",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this,R.font.helvetica_regular))
                }
                is NetworkResult.Loading -> {
                    //binding.loginProgressBar.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus == null) {
            return
        }
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }

    private fun fieldsValidation(username: String, password: String): Boolean {
        return when {
            username.isEmpty() or password.isEmpty() -> {
                AppUtils.showToast(
                    this@LoginActivity,
                    resources.getString(R.string.fill_in_all_fields)
                )
                false
            }
            else -> true
        }
    }

    private fun setCheckIcon(valid: Boolean) {
        if (valid) {
            binding.usernameLoginEt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_check,
                0
            )
            binding.passwordLoginEt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_check,
                0
            )
        } else {
            binding.usernameLoginEt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_cancel,
                0
            )
            binding.passwordLoginEt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_cancel,
                0
            )
        }
    }

}
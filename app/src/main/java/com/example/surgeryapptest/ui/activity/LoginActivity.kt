package com.example.surgeryapptest.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.doctorActivities.MainActivityDoctor
import com.example.surgeryapptest.ui.activity.patientActivities.MainActivity
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_patient_profile.view.*
import kotlinx.android.synthetic.main.fragment_upload_new_entry.view.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // Testing data store values
    private var userName = ""
    private var userID = ""
    private var userIcNumber = ""
    private var userGender = ""
    private var userType = ""

    // Replace later with Data Store
    @Inject
    lateinit var sessionManager: SessionManager

    private val loginViewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(
        //    WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //    WindowManager.LayoutParams.FLAG_FULLSCREEN
        //)
        setContentView(R.layout.activity_login)

        //loginViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)
        sessionManager = SessionManager(this)

        // set up the authentication later
        login_btn.setOnClickListener {
            hideSoftKeyboard(this)
            val username = username_login_et.text.toString()
            val password = password_login_et.text.toString()
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
                username_login_et.text = null
                password_login_et.text = null
            }
        }

    }

    // TODO: Delete after checking the deleted DS values
    private fun readSavedUserProfileDetails() {
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
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun userLogin(params: Map<String, String>) {

        loginViewModel.loginUser(params)
        loginViewModel.loginResponse.observe(this@LoginActivity, { response ->

            when (response) {
                is NetworkResult.Success -> {
                    setCheckIcon(true)

                    // save the auth token to sharedPrefs
                    sessionManager.saveAuthToken(response.data?.accessToken.toString())
                    // loginViewModel.saveUserAccessToken(response.data?.accessToken.toString())

                    Toast.makeText(
                        this@LoginActivity,
                        response.data?.message.toString() + " " + response.data?.accessToken.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    userName = response.data?.result?.get(0)?.mName.toString()
                    userID = response.data?.result?.get(0)?.mId.toString()
                    userIcNumber = response.data?.result?.get(0)?.mIc.toString()
                    userGender = response.data?.result?.get(0)?.mGender.toString()
                    userType = response.data?.result?.get(0)?.mType.toString()

                    // save user logged in flag = true
                    loginViewModel.setUserLoggedIn(true)
                    // save user profile details to data store
                    loginViewModel.saveUserProfileDetails(
                        userName, userID, userIcNumber, userGender, userType
                    )
                    // save access token to data store
                    loginViewModel.saveUserAccessToken(response.data?.accessToken.toString())

                    // get all the user detail
                    // println("Access Token: " + sessionManager.fetchAuthToken())

                    val userString =
                        "${response.data?.result?.get(0)?.mName} = ${response.data?.result?.get(0)?.mType}"

                    Toast.makeText(
                        this@LoginActivity,
                        userString,
                        Toast.LENGTH_SHORT
                    ).show()

                    goToMain(userType)
                }
                is NetworkResult.Error -> {
                    setCheckIcon(false)
                    Toast.makeText(
                        this@LoginActivity,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
                is NetworkResult.Loading -> {
                    //TODO: Add loading fragment here
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
            username_login_et.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_check,
                0
            )
            password_login_et.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_check,
                0
            )
        } else {
            username_login_et.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_cancel,
                0
            )
            password_login_et.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_cancel,
                0
            )
        }
    }

}
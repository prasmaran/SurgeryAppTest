package com.example.surgeryapptest.ui.activity.patientActivities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.surgeryapptest.R
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.LoginActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_upload_new_entry.view.*

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //loginViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

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

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun userLogin(params: Map<String, String>) {

        loginViewModel.loginUser(params)
        loginViewModel.loginResponse.observe(this@LoginActivity, { response ->

            when (response) {
                is NetworkResult.Success -> {
                    setCheckIcon(true)
                    Toast.makeText(
                        this@LoginActivity,
                        response.data?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    // set data store variables
                    // get all the user detail

                    val userString = "${response.data?.result?.get(0)?.mName} = ${response.data?.result?.get(0)?.mType}"
                    Toast.makeText(
                        this@LoginActivity,
                        userString,
                        Toast.LENGTH_SHORT
                    ).show()
                    goToMain()
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

    private fun setCheckIcon(valid: Boolean){
        if(valid) {
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
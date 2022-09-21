package com.example.surgeryapptest.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.doctorActivities.MainActivityDoctor
import com.example.surgeryapptest.ui.activity.patientActivities.MainActivity
import com.example.surgeryapptest.ui.activity.researcherActivities.ResearcherMainActivity
import com.example.surgeryapptest.view_models.SplashAScreenActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val time: Long = 1500

    private val splashAScreenViewModel: SplashAScreenActivityViewModel by viewModels()
    private lateinit var targetActivity: Class<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        splashAScreenViewModel.readUserLoggedIn.observe(this) { userLoggedIn ->
            when (userLoggedIn) {
                true -> {
                    readSUserTypeToNavigate()
                    goToTargetActivity(targetActivity)
                    //goToMainActivity()
                }
                else -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }, time)
                }
            }
        }
    }

    private fun goToTargetActivity(navToActivity: Class<*>) {
        val intent = Intent(this@SplashScreenActivity, navToActivity)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun readSUserTypeToNavigate() {

        // TODO: Create API to update user contact details
        /** Listen to the changes and update in Ui
         * Send changes to server and return the updated response */
        lifecycleScope.launch {
            splashAScreenViewModel.readUserProfileDetail.collect { values ->

                println("RETRIEVED DATA FROM DS IN SPLASH SCREEN : ${values.userType} ")

                when (values.userType) {
                    "A" -> targetActivity = MainActivity::class.java // remove this
                    "P" -> targetActivity = MainActivity::class.java
                    "D" -> targetActivity = MainActivityDoctor::class.java
                    "R" -> targetActivity = ResearcherMainActivity::class.java // Add new nav graph etc
                    "DOP" -> targetActivity = MainActivity::class.java
                }

            }
        }
    }
}
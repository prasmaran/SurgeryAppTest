package com.example.surgeryapptest.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.patientActivities.MainActivity
import com.example.surgeryapptest.view_models.SplashAScreenActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val time: Long = 1500

    private val splashAScreenViewModel: SplashAScreenActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        splashAScreenViewModel.readUserLoggedIn.observe(this, { userLoggedIn ->
            when (userLoggedIn) {
                true -> {
                    goToMainActivity()
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
        })
    }

    private fun goToMainActivity() {
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
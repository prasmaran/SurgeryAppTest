package com.example.surgeryapptest.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.activity.patientActivities.LoginActivity

class SplashScreenActivity : AppCompatActivity() {

    private val time: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java )
            startActivity(intent)
            finish()
        }, time)
    }
}
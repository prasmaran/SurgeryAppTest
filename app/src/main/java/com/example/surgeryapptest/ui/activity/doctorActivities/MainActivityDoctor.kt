package com.example.surgeryapptest.ui.activity.doctorActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.surgeryapptest.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main_doctor.*

@AndroidEntryPoint
class MainActivityDoctor : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_doctor)

        navController = findNavController(R.id.nav_host_fragment_doctor)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.doctorHomeFragment,
                R.id.doctorPatientListFragment,
                R.id.doctorAppointmentFragment,
                R.id.doctorProfileFragment,
            )
        )

        bottomNavigationViewDoctor.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
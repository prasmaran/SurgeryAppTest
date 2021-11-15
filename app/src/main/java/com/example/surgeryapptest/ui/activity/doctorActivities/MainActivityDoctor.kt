package com.example.surgeryapptest.ui.activity.doctorActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityMainDoctorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityDoctor : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainDoctorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navController = findNavController(R.id.nav_host_fragment_doctor)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.doctorHomeFragment,
                R.id.doctorPatientListFragment,
                R.id.doctorAppointmentFragment,
                R.id.doctorProfileFragment,
            )
        )

        binding.bottomNavigationViewDoctor.setupWithNavController(navController)
        //bottomNavigationViewDoctor.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
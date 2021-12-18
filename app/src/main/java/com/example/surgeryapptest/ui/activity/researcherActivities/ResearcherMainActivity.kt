package com.example.surgeryapptest.ui.activity.researcherActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityResearcherMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResearcherMainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityResearcherMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResearcherMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navController = findNavController(R.id.nav_host_fragment_researcher)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.researcherHomeFragment,
                R.id.researcherPatientListFragment,
                R.id.researcherProfileFragment,
            )
        )

        binding.bottomNavigationViewResearcher.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
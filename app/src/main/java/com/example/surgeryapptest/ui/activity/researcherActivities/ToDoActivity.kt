package com.example.surgeryapptest.ui.activity.researcherActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityMainBinding
import com.example.surgeryapptest.databinding.ActivityToDoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToDoActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityToDoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.toDoHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
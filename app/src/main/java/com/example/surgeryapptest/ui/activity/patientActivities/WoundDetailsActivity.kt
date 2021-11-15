package com.example.surgeryapptest.ui.activity.patientActivities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityWoundDetailsBinding
import com.example.surgeryapptest.ui.fragments.patientFrags.progressEntryDetails.WoundDetailsFragment
import com.example.surgeryapptest.ui.fragments.patientFrags.progressEntryDetails.WoundDoctorFeedbackFragment
import com.example.surgeryapptest.utils.adapter.PagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WoundDetailsActivity : AppCompatActivity() {

    private val args by navArgs<WoundDetailsActivityArgs>()
    private lateinit var binding: ActivityWoundDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWoundDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.woundDetailsToolbar)
        binding.woundDetailsToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(WoundDetailsFragment())
        fragments.add(WoundDoctorFeedbackFragment())

        val titles = ArrayList<String>()
        titles.add("Wound Image Details")
        titles.add("Doctors' Feedback")

        val resultBundle = Bundle()
        resultBundle.putParcelable("progressEntryBundle", args.result)

        // Initialize Pager adapter
        val adapter = PagerAdapter(
            resultBundle,
            fragments,
            titles,
            supportFragmentManager
        )

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
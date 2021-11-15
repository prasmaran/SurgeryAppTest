package com.example.surgeryapptest.ui.activity.doctorActivities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.ActivityPatientWoundDetailsBinding
import com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails.DoctorPatientWoundDetailFragment
import com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails.DoctorPatientWoundFeedback
import com.example.surgeryapptest.utils.adapter.PagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientWoundDetailsActivity : AppCompatActivity() {

    private val args by navArgs<PatientWoundDetailsActivityArgs>()
    private lateinit var binding: ActivityPatientWoundDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientWoundDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.doctorWoundDetailsToolbar)
        binding.doctorWoundDetailsToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val woundDetailsFrags = ArrayList<Fragment>()
        woundDetailsFrags.add(DoctorPatientWoundDetailFragment())
        woundDetailsFrags.add(DoctorPatientWoundFeedback())

        val fragTitles = ArrayList<String>()
        fragTitles.add("Wound Details")
        fragTitles.add("Feedback")

        val woundImageBundle = Bundle()
        woundImageBundle.putParcelable("woundImageBundle", args.woundImage)

        val woundDetailAdapter = PagerAdapter(
            woundImageBundle,
            woundDetailsFrags,
            fragTitles,
            supportFragmentManager
        )

        binding.viewPagerDoctor.adapter = woundDetailAdapter
        binding.doctorTabLayout.setupWithViewPager(binding.viewPagerDoctor)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
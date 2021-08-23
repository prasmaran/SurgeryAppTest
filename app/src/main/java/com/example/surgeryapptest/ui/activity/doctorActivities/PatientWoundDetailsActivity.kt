package com.example.surgeryapptest.ui.activity.doctorActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails.DoctorPatientWoundDetailFragment
import com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails.DoctorPatientWoundFeedback
import com.example.surgeryapptest.utils.adapter.PagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_patient_wound_details.*

@AndroidEntryPoint
class PatientWoundDetailsActivity : AppCompatActivity() {

    private val args by navArgs<PatientWoundDetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_wound_details)

        setSupportActionBar(doctor_wound_details_toolbar)
        doctor_wound_details_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
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

        view_pager_doctor.adapter = woundDetailAdapter
        doctor_tab_layout.setupWithViewPager(view_pager_doctor)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.surgeryapptest.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.fragments.progressEntryDetails.WoundDetailsFragment
import com.example.surgeryapptest.ui.fragments.progressEntryDetails.WoundDoctorFeedbackFragment
import com.example.surgeryapptest.utils.adapter.PagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_wound_details.*

@AndroidEntryPoint
class WoundDetailsActivity : AppCompatActivity() {

    private val args by navArgs<WoundDetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wound_details)

        setSupportActionBar(wound_details_toolbar)
        wound_details_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
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

        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
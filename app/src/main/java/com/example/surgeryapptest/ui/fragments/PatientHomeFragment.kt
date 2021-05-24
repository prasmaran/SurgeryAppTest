package com.example.surgeryapptest.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import com.example.surgeryapptest.R
import kotlinx.android.synthetic.main.fragment_patient_home.*
import kotlinx.android.synthetic.main.fragment_patient_home.view.*


class PatientHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_patient_home, container, false)

        // Pain scale rating for image uploading
        // https://awesomeopensource.com/project/YuganshT79/Smiley-Rating
        view.pain_scale_rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            view.error_Text_View.text = "$rating"
        }

        return view
    }



}
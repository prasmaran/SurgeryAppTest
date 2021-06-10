package com.example.surgeryapptest.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_patient_home.*
import kotlinx.android.synthetic.main.fragment_patient_home.view.*


class PatientHomeFragment : Fragment() {

    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_patient_home, container, false)

        navigateToProgressBook()
        return mView
    }

    private fun navigateToProgressBook() {
        mView.cardViewProgressBook.setOnClickListener {
            findNavController().navigate(R.id.patientProgressBooksFragment)
        }
    }


}
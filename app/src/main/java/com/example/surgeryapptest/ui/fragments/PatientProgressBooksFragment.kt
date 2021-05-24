package com.example.surgeryapptest.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.surgeryapptest.R
import kotlinx.android.synthetic.main.fragment_patient_progress_books.view.*

class PatientProgressBooksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_patient_progress_books, container, false)

        view.recyclerView.showShimmer()

        return view
    }

}
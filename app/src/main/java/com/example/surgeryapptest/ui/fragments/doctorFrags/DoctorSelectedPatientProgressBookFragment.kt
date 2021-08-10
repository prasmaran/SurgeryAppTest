package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.utils.adapter.PatientProgressBookListAdapter
import kotlinx.android.synthetic.main.fragment_doctor_selected_patient_progress_book.view.*


class DoctorSelectedPatientProgressBookFragment : Fragment() {

    private lateinit var dView: View
    private val args by navArgs<DoctorSelectedPatientProgressBookFragmentArgs>()
    private var selectedPatientProgressBook: PatientName? = null
    private val mAdapter by lazy { PatientProgressBookListAdapter() }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dView = inflater.inflate(R.layout.fragment_doctor_selected_patient_progress_book, container, false)

        selectedPatientProgressBook = args.patientProgressBook

        setupRecyclerView()
        setListData()


        return dView
    }

    private fun setListData(){
        mAdapter.setData(selectedPatientProgressBook!!)
    }

    private fun setupRecyclerView() {
        dView.recyclerViewSelectedPatientProgressBook.adapter = mAdapter
        dView.recyclerViewSelectedPatientProgressBook.layoutManager = LinearLayoutManager(requireContext())
        //showShimmerEffect()
    }

    private fun showShimmerEffect() {
        dView.recyclerViewSelectedPatientProgressBook.showShimmer()
    }

    private fun hideShimmerEffect() {
        dView.recyclerViewSelectedPatientProgressBook.hideShimmer()
    }

}
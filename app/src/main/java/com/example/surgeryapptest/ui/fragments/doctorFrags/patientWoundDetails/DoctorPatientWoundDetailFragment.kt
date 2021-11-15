package com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentDoctorPatientWoundDetailBinding
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.utils.app.AppUtils

class DoctorPatientWoundDetailFragment : Fragment() {

    private var _binding: FragmentDoctorPatientWoundDetailBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = arguments
        val woundBundle: WoundImage? = args?.getParcelable("woundImageBundle")

        if (woundBundle != null) {
            println("BUNDLE IS NOT NULL")
        }

        // Inflate the layout for this fragment
        _binding = FragmentDoctorPatientWoundDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.patientWoundImage.load(woundBundle!!.progressImage)
        binding.patientIdTv.text = woundBundle.masterUserIdFk.toString()
        binding.patientFeverTv.text = woundBundle.quesFever.split(" ")[0]
        binding.patientFeverTv2.text = woundBundle.quesFever
        binding.patientTitleTv.text = woundBundle.progressTitle
        binding.patientDescriptionTv.text = woundBundle.progressDescription

        val painLevel: String = woundBundle.quesPain
        binding.patientPainLevelTv.text = painLevel
        setPainLevelIcon(painLevel)

        binding.patientFluidDrainTv.text = woundBundle.quesFluid
        binding.patientRednessTv.text = woundBundle.quesRedness
        binding.patientSwellingTv.text = woundBundle.quesSwelling
        binding.patientOdourTv.text = woundBundle.quesOdour
        binding.patientDateCreated.text = AppUtils.formatDateTimestampUtil(woundBundle.dateCreated)

        return view
    }

    private fun setPainLevelIcon(painLevel: String) {
        when (painLevel) {
            "TERRIBLE" -> binding.patientPainRateIcon.load(R.drawable.ic_pain_terrible)
            "BAD" -> binding.patientPainRateIcon.load(R.drawable.ic_pain_bad)
            "OKAY" -> binding.patientPainRateIcon.load(R.drawable.ic_pain_okay)
            "GOOD" -> binding.patientPainRateIcon.load(R.drawable.ic_pain_good)
            "GREAT" -> binding.patientPainRateIcon.load(R.drawable.ic_pain_great)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
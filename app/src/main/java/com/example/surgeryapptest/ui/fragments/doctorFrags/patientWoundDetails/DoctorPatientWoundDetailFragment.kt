package com.example.surgeryapptest.ui.fragments.doctorFrags.patientWoundDetails

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.utils.app.AppUtils
import kotlinx.android.synthetic.main.fragment_doctor_patient_wound_detail.view.*

class DoctorPatientWoundDetailFragment : Fragment() {

    private lateinit var dwView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dwView = inflater.inflate(R.layout.fragment_doctor_patient_wound_detail, container, false)

        val args = arguments
        val woundBundle: WoundImage? = args?.getParcelable("woundImageBundle")

        if (woundBundle != null) {
            println("BUNDLE IS NOT NULL")
        }

        dwView.patient_wound_image.load(woundBundle!!.progressImage)
        dwView.patient_id_tv.text = woundBundle.masterUserIdFk.toString()
        dwView.patient_fever_tv.text = woundBundle.quesFever.split(" ")[0]
        dwView.patient_fever_tv_2.text = woundBundle.quesFever
        dwView.patient_title_tv.text = woundBundle.progressTitle
        dwView.patient_description_tv.text = woundBundle.progressDescription

        val painLevel: String = woundBundle.quesPain
        dwView.patient_pain_level_tv.text = painLevel
        setPainLevelIcon(painLevel)

        dwView.patient_fluid_drain_tv.text = woundBundle.quesFluid
        dwView.patient_redness_tv.text = woundBundle.quesRedness
        dwView.patient_swelling_tv.text = woundBundle.quesSwelling
        dwView.patient_odour_tv.text = woundBundle.quesOdour
        dwView.patient_date_created.text = AppUtils.formatDateTimestampUtil(woundBundle.dateCreated)

        return dwView
    }

    private fun setPainLevelIcon(painLevel: String) {
        when (painLevel) {
            "TERRIBLE" -> dwView.patient_pain_rate_icon.load(R.drawable.ic_pain_terrible)
            "BAD" -> dwView.patient_pain_rate_icon.load(R.drawable.ic_pain_bad)
            "OKAY" -> dwView.patient_pain_rate_icon.load(R.drawable.ic_pain_okay)
            "GOOD" -> dwView.patient_pain_rate_icon.load(R.drawable.ic_pain_good)
            "GREAT" -> dwView.patient_pain_rate_icon.load(R.drawable.ic_pain_great)
        }
    }

}
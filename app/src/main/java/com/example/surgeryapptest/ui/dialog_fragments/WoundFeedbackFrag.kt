package com.example.surgeryapptest.ui.dialog_fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.setFragmentResult
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.ui.interfaces.SendFeedback
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.wound_feedback_dialog_fragment.*

class WoundFeedbackFrag : BaseDialogFragment(R.layout.wound_feedback_dialog_fragment) {

    private var sendFeedbackInterface: SendFeedback? = null
    private var patientDetailFeedback = PatientDetailFeedback("", "", "")

    companion object {
        fun newInstance(patientDetail: PatientDetailFeedback): WoundFeedbackFrag =
            WoundFeedbackFrag().apply {
                arguments = Bundle().apply {
                    putParcelable("patient_detail", patientDetail)
                }
//                arguments = Bundle().apply {
//                    putString("wound_title", patientDetail.woundTitle)
//                }
//                arguments = Bundle().apply {
//                    putString("pain_rate", patientDetail.painRate)
//                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sendFeedbackInterface = parentFragment as SendFeedback
        arguments?.getParcelable<PatientDetailFeedback>("patient_detail")?.let {
            patientDetailFeedback.patientName = it.patientName
            patientDetailFeedback.woundTitle = it.woundTitle
            patientDetailFeedback.painRate = it.painRate
        }
//        arguments?.getString("wound_title")?.let {
//            patientDetailFeedback.woundTitle = it
//        }
//        arguments?.getString("pain_rate")?.let {
//            patientDetailFeedback.painRate = it
//        }
    }

    override fun onDetach() {
        sendFeedbackInterface = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedback_closeBtn.setOnClickListener {
            dismiss()
        }
        submit_feedback_btn.setOnClickListener {
            // Send the feedback through the interface
            val doctorFeedback: String = feedback_et.text.toString()

            dismiss()

            if (doctorFeedback.isNotEmpty() || doctorFeedback.isNotBlank()) {
                sendFeedbackInterface?.sendData(doctorFeedback, false)
            } else {
                sendFeedbackInterface?.sendData(doctorFeedback, true)
            }
        }

        feedback_patient_name_tv.append(patientDetailFeedback.patientName)
        feedback_wound_title_tv.append(patientDetailFeedback.woundTitle)
        feedback_pain_rate_tv.append(patientDetailFeedback.painRate)

    }
}

@Parcelize
data class PatientDetailFeedback(
    var patientName: String,
    var woundTitle: String,
    var painRate: String
) : Parcelable
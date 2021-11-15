package com.example.surgeryapptest.ui.dialog_fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.surgeryapptest.databinding.WoundFeedbackDialogFragmentBinding
import com.example.surgeryapptest.ui.interfaces.SendFeedback
import kotlinx.android.parcel.Parcelize

class WoundFeedbackFrag : DialogFragment() {

    // TODO: THIS SHIT IS A BIT TRICKY TO SOLVE

    private var sendFeedbackInterface: SendFeedback? = null
    private var patientDetailFeedback = PatientDetailFeedback("", "", "")

    private var _binding: WoundFeedbackDialogFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(patientDetail: PatientDetailFeedback): WoundFeedbackFrag =
            WoundFeedbackFrag().apply {
                arguments = Bundle().apply {
                    putParcelable("patient_detail", patientDetail)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = WoundFeedbackDialogFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
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
        binding.feedbackCloseBtn.setOnClickListener {
            dismiss()
        }
        binding.submitFeedbackBtn.setOnClickListener {
            // Send the feedback through the interface
            val doctorFeedback: String = binding.feedbackEt.text.toString()

            dismiss()

            if (doctorFeedback.isNotEmpty() || doctorFeedback.isNotBlank()) {
                sendFeedbackInterface?.sendData(doctorFeedback, false)
            } else {
                sendFeedbackInterface?.sendData(doctorFeedback, true)
            }
        }

        binding.feedbackPatientNameTv.append(patientDetailFeedback.patientName)
        binding.feedbackWoundTitleTv.append(patientDetailFeedback.woundTitle)
        binding.feedbackPainRateTv.append(patientDetailFeedback.painRate)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// TODO: Why the fuck is this giving this weird errors?
// parcelize vs kotlin extensions?

@Parcelize
data class PatientDetailFeedback(
    var patientName: String,
    var woundTitle: String,
    var painRate: String
) : Parcelable
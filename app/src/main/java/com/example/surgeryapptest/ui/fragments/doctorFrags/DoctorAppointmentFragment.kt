package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.surgeryapptest.R
import com.example.surgeryapptest.ui.dialog_fragments.PatientDetailFeedback
import com.example.surgeryapptest.ui.dialog_fragments.WoundFeedbackFrag
import com.example.surgeryapptest.ui.interfaces.SendFeedback
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.constant.DialogFragConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_doctor_appointment.view.*

class DoctorAppointmentFragment : Fragment() {

    private lateinit var dView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dView = inflater.inflate(R.layout.fragment_doctor_appointment, container, false)

        dView.filledTextField.editText!!.setText(R.string.app_name)

//        dView.submit_button.setOnClickListener {
//            val input = dView.edit_text.text
//            dView.text_preview.text = input
//        }

        return dView

    }

    private fun open() {
        val textInputLayout = TextInputLayout(requireContext())
        val editText = TextInputEditText(textInputLayout.context)
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(Constants.NETWORK_ERROR_NO_INTERNET)
            .setMessage("Feedback to patient")
            .setView(editText)
            .setIcon(R.drawable.ic_chat)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            dView.text_preview.text = editText.text
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun showFeedbackDialogFrag(patientDetail: PatientDetailFeedback) {
        AppUtils.showDialogFragment(
            WoundFeedbackFrag.newInstance(patientDetail),
            childFragmentManager,
            DialogFragConstants.WOUND_IMAGE_FEEDBACK_FRAG.key
        )

    }


}
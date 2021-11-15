package com.example.surgeryapptest.ui.fragments.doctorFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentDoctorAppointmentBinding
import com.example.surgeryapptest.ui.dialog_fragments.PatientDetailFeedback
import com.example.surgeryapptest.ui.dialog_fragments.WoundFeedbackFrag
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.utils.constant.DialogFragConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DoctorAppointmentFragment : Fragment() {

    private var _binding: FragmentDoctorAppointmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorAppointmentBinding.inflate(inflater, container, false)

        return binding.root
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
            binding.textPreview.text = editText.text
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
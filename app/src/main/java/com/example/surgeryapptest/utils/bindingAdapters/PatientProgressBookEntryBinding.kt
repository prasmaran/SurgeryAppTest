package com.example.surgeryapptest.utils.bindingAdapters

import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.ui.fragments.doctorFrags.DoctorSelectedPatientProgressBookFragmentDirections
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientArchiveBookFragment
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientProgressBooksFragmentDirections
import com.example.surgeryapptest.ui.fragments.researcherFrags.ResearcherSelectedPatientProgressBookFragmentDirections
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.constant.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PatientProgressBookEntryBinding {

    companion object {

        @BindingAdapter("onProgressEntryImageClickListener")
        @JvmStatic
        fun onProgressEntryImageClickListener(
            progressEntryRowLayout: ConstraintLayout,
            result: AllProgressBookEntryItem
        ) {
            progressEntryRowLayout.setOnClickListener {
                try {
                    val action =
                        PatientProgressBooksFragmentDirections.actionPatientProgressBooksFragmentToWoundDetailsActivity(
                            result
                        )
                    progressEntryRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    println("onProgressEntryImageClickListener : $e")
                }
            }
        }

        /**
         * Researcher Selected patient progess entry to detail activity
         */
        @BindingAdapter("onResearchWoundImageClickListener")
        @JvmStatic
        fun onResearchWoundImageClickListener(
            selectedPatientWoundImageLayout: ConstraintLayout,
            patientWoundImage: WoundImage
        ) {
            selectedPatientWoundImageLayout.setOnClickListener {
                try {
                    println("GOING TO DETAIL ACTIVITY with ${patientWoundImage.progressTitle}")

                    val navigateTo = ResearcherSelectedPatientProgressBookFragmentDirections.actionResearcherSelectedPatientProgressBookFragment2ToPatientWoundDetailsActivity2(
                        patientWoundImage
                    )

                    selectedPatientWoundImageLayout.findNavController().navigate(navigateTo)
                } catch (e: Exception) {
                    println("onResearchWoundImageClickListener : $e")
                }
            }
        }


        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                placeholder(R.drawable.ic_loading_image)
                error(R.drawable.ic_mountains_2)
                crossfade(300)
            }
        }

        // Need to format the date time to DD-MM-YYYY

        @BindingAdapter("formatDateTimestamp")
        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateTimestamp(textView: TextView, dateTime: String) {
            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            val sdf = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            textView.text = sdf
            //return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }

        // Change the variables later
        @BindingAdapter("convertEntryIDToString")
        @JvmStatic
        fun convertEntryIDToString(textView: TextView, result: WoundImage) {
            val entryID = "Uploaded image ID: ${result.entryID}"
            textView.text = entryID
        }


        // Change the variables later
        @BindingAdapter("loadDrAssignedPatientProgressBook")
        @JvmStatic
        fun loadDrAssignedPatientProgressBook(textView: TextView, result: AllProgressBookEntryItem) {
            val contactNumber = "Doctor assigned: ${result.doctorAssigned}"
            textView.text = contactNumber
        }

    }
}
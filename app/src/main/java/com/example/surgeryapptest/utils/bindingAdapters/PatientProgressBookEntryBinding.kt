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
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientArchiveBookFragment
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientProgressBooksFragmentDirections
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

        /** Selected Patients
         *
         *
         *
         *
         * */

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

        /**
         * Long click listener to
         * remove the entry from the archive list
         */
        @BindingAdapter("onProgressEntryArchivedImageClickListener")
        @JvmStatic
        fun onProgressEntryArchivedImageClickListener(
            progressArchivedEntryRowLayout: ConstraintLayout,
            result: AllProgressBookEntryItem
        ) {
//            progressArchivedEntryRowLayout.setOnClickListener {
            Log.d("LONG_PRESS", "onProgressEntryArchivedImageClickListener: $result ")
//            }
        }

//        @RequiresApi(Build.VERSION_CODES.O)
//        fun parseDate(dateTime: String): String {
//            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
//            val sdf = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))
//            println("Parsed DateTime: $sdf")
//            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
//        }
    }
}
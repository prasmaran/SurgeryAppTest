package com.example.surgeryapptest.utils.bindingAdapters

import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientProgressBooksFragmentDirections
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

//        @RequiresApi(Build.VERSION_CODES.O)
//        fun parseDate(dateTime: String): String {
//            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
//            val sdf = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))
//            println("Parsed DateTime: $sdf")
//            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
//        }
    }
}
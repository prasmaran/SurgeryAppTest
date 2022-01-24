package com.example.surgeryapptest.utils.bindingAdapters

import android.app.Activity
import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.appointmentResponse.AppointmentNetworkResponse
import com.example.surgeryapptest.model.network.appointmentResponse.Result
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientProgressBooksFragmentDirections
import com.example.surgeryapptest.ui.fragments.researcherFrags.ResearcherSelectedPatientProgressBookFragmentDirections
import com.example.surgeryapptest.utils.app.AppUtils
import com.google.android.material.card.MaterialCardView
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentBinding {

    companion object {

        @BindingAdapter("onAppointmentClickListener")
        @JvmStatic
        fun onAppointmentClickListener(
            appointmentLayout: ConstraintLayout,
            result: AppointmentNetworkResponse
        ) {


        }

        @BindingAdapter("appointmentFormatDate")
        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.O)
        fun appointmentFormatDate(textView: TextView, dateTime: String) {
            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            val sdf = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            textView.text = sdf
            //return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }

        @BindingAdapter("appointmentFormatTime")
        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.O)
        fun appointmentFormatTime(textView: TextView, dateTime: String) {
            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            val sdf = parsedDate.format(DateTimeFormatter.ofPattern("hh:mm a"))
            textView.text = sdf
            //return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }

        /**
         * With doctors
         */
        @BindingAdapter("convertAppointmentUserIDToString")
        @JvmStatic
        fun convertAppointmentUserIDToString(textView: TextView, result: Result) {
            val entryID = "Dr ${result.withName} (Doctor ID : ${result.mIc})"
            textView.text = entryID
        }

        /**
         * With patients
         */
        @BindingAdapter("convertDoctorAppointmentUserIDToString")
        @JvmStatic
        fun convertDoctorAppointmentUserIDToString(textView: TextView, result: Result) {
            val entryID = "${result.withName} (Patient ID : ${result.mIc})"
            textView.text = entryID
        }

    }
}
package com.example.surgeryapptest.utils.bindingAdapters

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.WoundImage
import com.example.surgeryapptest.ui.fragments.doctorFrags.DoctorPatientListFragmentDirections
import com.example.surgeryapptest.ui.fragments.doctorFrags.DoctorSelectedPatientProgressBookFragmentDirections
import com.example.surgeryapptest.ui.fragments.researcherFrags.ResearcherPatientListFragmentDirections

class PatientListRowBinding {

    companion object {

        @BindingAdapter("onPatientNameClickListener")
        @JvmStatic
        fun onPatientNameClickListener(
            patientListRowLayout: ConstraintLayout,
            patientProgressBook: PatientName
        ) {
            patientListRowLayout.setOnClickListener {
                try {
                    val action =
                        DoctorPatientListFragmentDirections.actionDoctorPatientListFragmentToDoctorSelectedPatientProgressBookFragment(
                            patientProgressBook
                        )
                    patientListRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    println("onPatientNameClickListener : $e")
                }
            }
        }

        /**
         * Researcher Patient List to progress entry list
         */
        @BindingAdapter("onResearchPatientNameClickListener")
        @JvmStatic
        fun onResearchPatientNameClickListener(
            patientListRowLayout: ConstraintLayout,
            patientProgressBook: PatientName
        ) {
            patientListRowLayout.setOnClickListener {
                try {
                    val action =
                        ResearcherPatientListFragmentDirections.actionResearcherPatientListFragmentToResearcherSelectedPatientProgressBookFragment2(
                            patientProgressBook
                        )
                    patientListRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    println("onResearchPatientNameClickListener : $e")
                }
            }
        }

        @BindingAdapter("onWoundImageClickListener")
        @JvmStatic
        fun onWoundImageClickListener(
            selectedPatientWoundImageLayout: ConstraintLayout,
            patientWoundImage: WoundImage
        ) {
            selectedPatientWoundImageLayout.setOnClickListener {
                try {
//                    val action =
//                        DoctorSelectedPatientProgressBookFragmentDirections.actionDoctorSelectedPatientProgressBookFragmentToPatientWoundDetailsActivity(
//                            patientWoundImage
//                        )

                    val navigateTo = DoctorSelectedPatientProgressBookFragmentDirections.actionDoctorSelectedPatientProgressBookFragmentToPatientWoundDetailsActivity(
                        patientWoundImage
                    )
                    selectedPatientWoundImageLayout.findNavController().navigate(navigateTo)
                } catch (e: Exception) {
                    println("onWoundImageClickListener : $e")
                }
            }
        }

        @BindingAdapter("loadDummyPatientImage")
        @JvmStatic
        fun loadDummyPatientImage(imageView: ImageView, drawable: Int) {
            imageView.load(R.drawable.ic_patient) {
                placeholder(R.drawable.ic_loading_image)
                crossfade(300)
            }
        }

        @BindingAdapter("loadPatientName")
        @JvmStatic
        fun loadPatientName(textView: TextView, result: PatientName) {
            val name = result.patientId.split(":")[1]
            textView.text = name
        }

        @BindingAdapter("loadPatientIDNumber")
        @JvmStatic
        fun loadPatientIDNumber(textView: TextView, result: PatientName) {
            val registrationID = result.woundImages[0].mIc
            textView.text = registrationID
        }

        @BindingAdapter("loadWoundImageNumber")
        @JvmStatic
        fun loadWoundImageNumber(textView: TextView, result: PatientName) {
            val noOfImages = result.woundImages.size
            val finalText = "Uploaded $noOfImages images"
            textView.text = finalText
        }

        // Change the variables later
        @BindingAdapter("loadContactNumber")
        @JvmStatic
        fun loadContactNumber(textView: TextView, result: PatientName) {
            val contactNumber = "Doctor assigned: ${result.woundImages[0].doctorAssigned}"
            textView.text = contactNumber
        }


//        @BindingAdapter("formatDateTimestamp")
//        @JvmStatic
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun formatDateTimestamp(textView: TextView, dateTime: String) {
//            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
//            val sdf = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
//            textView.text = sdf
//            //return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
//        }
//
    }
}
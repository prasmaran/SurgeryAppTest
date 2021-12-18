package com.example.surgeryapptest.utils.bindingAdapters

import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.PatientName
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.model.network.utilsResponse.GeneralInfoList
import com.example.surgeryapptest.ui.fragments.patientFrags.GeneralInfoFragmentDirections
import com.example.surgeryapptest.ui.fragments.patientFrags.PatientProgressBooksFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.internal.Contexts.getApplication

class UtilsBinding {

    companion object {

        @BindingAdapter("generalInfoNav")
        @JvmStatic
        fun generalInfoNav(
            generalInfoRowLayout: ConstraintLayout,
            result: GeneralInfoList
        ) {
            generalInfoRowLayout.setOnClickListener {

                println("Clicked on: ${result.articleTitle}")
                try {
                    val action = GeneralInfoFragmentDirections.actionGeneralInfoFragmentToGeneralInfoWebActivity(result)
                    generalInfoRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    println("onGeneralInfoClickListener: $e")
                }
            }
        }

        @BindingAdapter("loadArticleId")
        @JvmStatic
        fun loadArticleId(textView: TextView, articleId: Int) {
            val id = articleId.toString()
            if (id.length < 2) {
                val modifiedId = "0$id"
                textView.text = modifiedId
            } else {
                textView.text = id
            }
        }

        /**
         * val colors = arrayListOf<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS) {
        colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
        colors.add(color)
        }
         *
         */

    }
}
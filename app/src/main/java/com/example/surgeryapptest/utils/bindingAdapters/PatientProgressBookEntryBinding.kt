package com.example.surgeryapptest.utils.bindingAdapters

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.ui.fragments.PatientProgressBooksFragmentDirections
import java.lang.Exception

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

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                placeholder(R.drawable.ic_loading_image)
                error(R.drawable.mountains)
                crossfade(300)
            }
        }

        // Need to format the date time to DD-MM-YYYY
        @BindingAdapter("formatDateTimestamp")
        @JvmStatic
        fun dateTimeFormatter(textView: TextView, date: String) {
        }
    }
}
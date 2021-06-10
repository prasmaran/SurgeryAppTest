package com.example.surgeryapptest.utils.bindingAdapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.example.surgeryapptest.R

class PatientProgressBookEntryBinding {

    companion object {

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
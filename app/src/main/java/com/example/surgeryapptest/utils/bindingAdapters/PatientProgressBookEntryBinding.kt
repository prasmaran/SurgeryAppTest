package com.example.surgeryapptest.utils.bindingAdapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

class PatientProgressBookEntryBinding {

    companion object {

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String){
            imageView.load(imageUrl) {
                crossfade(600)
            }
        }
    }
}
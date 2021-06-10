package com.example.surgeryapptest.utils.bindingAdapters

import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.surgeryapptest.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UploadNewEntryBinding {

    companion object {

        // Navigate to Upload New Entry Fragment
        @BindingAdapter("android:navigateAddFragment")
        @JvmStatic
        fun navigateAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    view.findNavController().navigate(R.id.uploadNewEntryFragment)
                    println("Getting clicked ....!!!!")
                }
            }
        }

    }
}
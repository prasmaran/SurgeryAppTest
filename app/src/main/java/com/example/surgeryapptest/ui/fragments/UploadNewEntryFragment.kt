package com.example.surgeryapptest.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.domain_model.UploadNewEntryRequestBody
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.AppUtils.Companion.getFileName
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.MainActivityViewModel
import com.example.surgeryapptest.view_models.UploadNewEntryFragmentViewModel
import com.hsalf.smilerating.SmileRating
import kotlinx.android.synthetic.main.fragment_upload_new_entry.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UploadNewEntryFragment () :
    Fragment(), SmileRating.OnSmileySelectionListener, SmileRating.OnRatingSelectedListener, UploadNewEntryRequestBody.UploadCallback {

    companion object {
        private const val GALLERY_PERMISSION = 1
        private const val CAMERA_PERMISSION = 2
        private const val REQUEST_CODE_IMAGE_PICKER = 1000
        private const val REQUEST_CODE_CAMERA = 2000
    }

    private lateinit var uploadNewEntryViewModel: UploadNewEntryFragmentViewModel
    private lateinit var mView: View

    // Uploaded info for new entry
    private var selectedImageUri: Uri? = null
    private var painRating : String = ""
    private var title : String = ""
    private var description : String = ""
    private var fluidDrained : String = ""
    private var redness : String = ""
    private var swelling : String = ""

    // Radio group and radio button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        uploadNewEntryViewModel = ViewModelProvider(requireActivity()).get(UploadNewEntryFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_upload_new_entry, container, false)

        // Get image from gallery and the camera
        mView.pick_from_gallery_btn.setOnClickListener {
            getWoundImage()
        }
        mView.camera_btn.setOnClickListener {
            captureNewImage()
        }

        mView.rating_bar.setOnSmileySelectionListener(this)
        mView.rating_bar.setOnRatingSelectedListener(this)

        formInputListener()
        radioBtnListeners()

        mView.submit_btn.setOnClickListener {
            val isAllFieldFilled = validateFormEntry(selectedImageUri.toString(),painRating, description, title)
            if (isAllFieldFilled) {
                println("You have entered the following details" +
                        "\n$selectedImageUri" + "\n$title" +"\n$painRating" + "\n$description" + "\n$fluidDrained")

                if (selectedImageUri != null) {
                    mView.progress_bar.visibility = View.VISIBLE
                    uploadImage()
                } else {
                    AppUtils.showToast(requireContext(),"Please upload an image!")
                }

                //AppUtils.showToast(requireContext(), "You have entered the following details" +
                //        "\n$selectedImageUri" + "\n$title" +"\n$painRating" + "\n$description" + "\n$fluidDrained" )
            }
        }

        return mView
    }

    private fun validateFormEntry(
        selectedImageUri: String,
        painRating: String,
        description: String,
        title: String
    ) : Boolean {
        when {
            (selectedImageUri.isNullOrBlank()) or painRating.isEmpty() or description.isEmpty() or title.isEmpty() -> {
                mView.uploadFragmentLayout.showSnackBar("Fill in all the fields")
                return false
            }
            else -> return true
        }
    }

    private fun formInputListener(){
        mView.new_entry_title.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    title = it.toString()
                }
            }
        }
        mView.new_entry_description.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    description = it.toString()
                }
            }
        }
    }

    private fun radioBtnListeners(){
        mView.rgFluidDrainage.setOnCheckedChangeListener { group, checkedId ->
            var rbFluid = mView.findViewById<RadioButton>(checkedId)
            fluidDrained = rbFluid.text.toString()
        }
    }

    private fun uploadImage(){
        // Get actual path of the image: App specific temp storage
        // No need an special permissions
        var contentResolver = requireActivity().contentResolver
        val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return
        val file = File(activity?.externalCacheDir, contentResolver.getFileName(selectedImageUri!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        mView.progress_bar.progress = 0
        val body = UploadNewEntryRequestBody(file, "image", this)

        uploadNewEntryViewModel.uploadNewWoundEntry(
            MultipartBody.Part.createFormData("image", file.name, body),
            RequestBody.create(MediaType.parse("multipart/form-data"), description),
            RequestBody.create(MediaType.parse("multipart/form-data"), painRating),
            RequestBody.create(MediaType.parse("multipart/form-data"), fluidDrained)
        )
        uploadNewEntryViewModel.uploadedNewEntryResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
                is NetworkResult.Success -> {
                    mView.progress_bar.progress = 100
                    mView.uploadFragmentLayout.showSnackBar("Your new entry has been uploaded ...")
//                    hideShimmerEffect()
//                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    //showShimmerEffect()
                }
            }
        })
    }

    // Capture new image
    private fun captureNewImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        } else {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION
            )
        }
    }

    // Pick from gallery
    private fun getWoundImage() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImageUri = data?.data
                    //println("SelectedImageURI: $selectedImageUri")
                    mView.new_wound_image.setImageURI(selectedImageUri)
                }
                REQUEST_CODE_CAMERA -> {
                    val thumbnail: Bitmap = data?.extras?.get("data") as Bitmap
                    mView.new_wound_image.setImageBitmap(thumbnail)
                    println("SelectedImageURI: $selectedImageUri")
                }
            }
        }
    }

    // Image converter


    // Add permission result for gallery access
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Access has been denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSmileySelected(smiley: Int, reselected: Boolean) {
        when(smiley) {
            SmileRating.TERRIBLE -> painRating = "TERRIBLE"
            SmileRating.BAD -> painRating = "BAD"
            SmileRating.OKAY -> painRating = "OKAY"
            SmileRating.GOOD -> painRating = "GOOD"
            SmileRating.GREAT -> painRating = "GREAT"
            SmileRating.NONE -> painRating = "NONE"
        }
    }

    override fun onRatingSelected(level: Int, reselected: Boolean) {
        println("Rated as $level - $reselected")
    }

    override fun onProgressUpdate(percentage: Int) {
        mView.progress_bar.progress = percentage
    }
}
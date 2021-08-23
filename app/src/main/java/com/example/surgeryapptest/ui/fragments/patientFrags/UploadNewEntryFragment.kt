 package com.example.surgeryapptest.ui.fragments.patientFrags

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.domain_model.UploadNewEntryRequestBody
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.AppUtils.Companion.getFileName
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.UploadNewEntryFragmentViewModel
import com.hsalf.smilerating.SmileRating
import kotlinx.android.synthetic.main.fragment_upload_new_entry.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UploadNewEntryFragment :
    Fragment(), SmileRating.OnSmileySelectionListener, SmileRating.OnRatingSelectedListener,
    UploadNewEntryRequestBody.UploadCallback {

    companion object {
        private const val GALLERY_PERMISSION = 1
        private const val CAMERA_PERMISSION = 2
        private const val REQUEST_CODE_IMAGE_PICKER = 1000
        private const val REQUEST_CODE_CAMERA = 2000
        private const val FILE_NAME = "woundImage"
    }

    //TODO: Check for successful upload flag
    // to prevent repetitive API calling

    private lateinit var uploadNewEntryViewModel: UploadNewEntryFragmentViewModel
    private lateinit var mView: View
    private lateinit var photoFile: File

    // Uploaded info for new entry
    private var userID: String = ""
    private var selectedImageUri: Uri? = null
    private var painRating: String = ""
    private var title: String = ""
    private var description: String = ""
    private var fluidDrained: String = ""
    private var redness: String = ""
    private var swelling: String = ""
    private var odour: String = ""
    private var fever: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        uploadNewEntryViewModel =
            ViewModelProvider(requireActivity()).get(UploadNewEntryFragmentViewModel::class.java)
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

        // Setup the pain rating slider
        mView.rating_bar.setOnSmileySelectionListener(this)
        mView.rating_bar.setOnRatingSelectedListener(this)
        //mView.rating_bar.selectedSmile = BaseRating.GREAT

        formInputListener()
        radioBtnListeners()

        mView.submit_btn.setOnClickListener {

            //closeSoftKeyboard(requireContext(), it) --> giving  weird logcat
            //closeKeyboard(mView.submit_btn)
            //hideSoftKeyboard(requireActivity())

            val isAllFieldFilled =
                validateFormEntry(
                    selectedImageUri.toString(),
                    title,
                    description,
                    fluidDrained,
                    painRating,
                    redness,
                    swelling,
                    odour,
                    fever
                )
            if (isAllFieldFilled) {
                if (selectedImageUri != null) {
                    mView.progress_bar.visibility = View.VISIBLE
                    createAlertDialog()
                    it.hideKeyboard()
                } else {
                    AppUtils.showToast(requireContext(), "Please upload an image!")
                }
            }
        }

        return mView
    }

    private fun validateFormEntry(
        selectedImageUri: String,
        title: String,
        description: String,
        fluid: String,
        painRating: String,
        redness: String,
        swelling: String,
        odour: String,
        fever: String
    ): Boolean {
        when {
            painRating.isEmpty() or description.isEmpty() or title.isEmpty() or
                    redness.isEmpty() or fluid.isEmpty() or swelling.isEmpty() or
                    odour.isEmpty() or fever.isEmpty() -> {
                mView.uploadFragmentLayout.showSnackBar("Fill in all the fields")
                return false
            }
            else -> return true
        }
    }

    // Input listeners
    private fun formInputListener() {
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

    private fun clearFocusEditText(){
        mView.new_entry_title.clearFocus()
        mView.new_entry_description.clearFocus()
    }

    // Radio button listeners
    private fun radioBtnListeners() {

        mView.rgFluidDrainage.setOnCheckedChangeListener { _, checkedId ->
            clearFocusEditText()
            val rbFluid = mView.findViewById<RadioButton>(checkedId)
            fluidDrained = rbFluid.text.toString()
        }
        mView.rgRedness.setOnCheckedChangeListener { _, checkedId ->
            clearFocusEditText()
            val rbRedness = mView.findViewById<RadioButton>(checkedId)
            redness = rbRedness.text.toString()
        }
        mView.rgSwelling.setOnCheckedChangeListener { _, checkedId ->
            clearFocusEditText()
            val rbSwelling = mView.findViewById<RadioButton>(checkedId)
            swelling = rbSwelling.text.toString()
        }
        mView.rgOdour.setOnCheckedChangeListener { _, checkedId ->
            clearFocusEditText()
            val rbOdour = mView.findViewById<RadioButton>(checkedId)
            odour = rbOdour.text.toString()
        }
        mView.rgFever.setOnCheckedChangeListener { _, checkedId ->
            clearFocusEditText()
            val rbFever = mView.findViewById<RadioButton>(checkedId)
            fever = rbFever.text.toString()
        }
    }

    // Upload image to backend
    // Get actual path of the image: App specific temp storage
    private fun uploadImage() {
        // No need an special permissions
        var contentResolver = requireActivity().contentResolver
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return
        val file = File(activity?.externalCacheDir, contentResolver.getFileName(selectedImageUri!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        // Initialize the progress bar with value 0
        mView.progress_bar.progress = 0
        val body = UploadNewEntryRequestBody(file, "image", this)

        // read user id
        viewLifecycleOwner.lifecycleScope.launch {
            uploadNewEntryViewModel.readUserProfileDetail.collect { values ->
                userID = values.userID
            }
        }
        println("USER ID: $userID")

        // Pass the values as parameter
        // to be sent to backend
        uploadNewEntryViewModel.uploadNewWoundEntry(
            userID.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            MultipartBody.Part.createFormData("image", file.name, body),
            title.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            description.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            fluidDrained.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            painRating.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            redness.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            swelling.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            odour.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            fever.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            //RequestBody.create(MediaType.parse("multipart/form-data"), fever),
        )
        uploadNewEntryViewModel.uploadedNewEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    mView.progress_bar.progress = 100
                    mView.uploadFragmentLayout.showSnackBar("Your image has been uploaded")
                    // Navigate back after 1 sec
                    findNavController().navigateUp()
                    findNavController().navigate(R.id.patientProgressBooksFragment)
                    //val action = UploadNewEntryFragmentDirections.actionUploadNewEntryFragmentToPatientProgressBooksFragment()
                    //findNavController().navigate(action)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    //TODO: Add loading fragment here
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
            // For content sharing in a more secure way
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                "com.example.surgeryapptest.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
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

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    // Testing
    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    //TODO: Move to AppUtils later on
    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm submit?")
        builder.setMessage("\nAre you sure you want to submit a new image entry?")
        builder.setIcon(R.drawable.ic_confirm)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            uploadImage()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // hide soft keyboard
    private fun closeKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // hide soft keyboard 2
    private fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus == null) {
            return
        }
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }

    // hide soft keyboard 3
    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImageUri = data?.data
                    println("Gallery SelectedImageURI: $selectedImageUri")
                    mView.new_wound_image.setImageURI(selectedImageUri)
                }
                REQUEST_CODE_CAMERA -> {
                    //val thumbnail: Bitmap = data?.extras?.get("data") as Bitmap
                    val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                    mView.new_wound_image.setImageBitmap(takenImage)
                    val bitToUri = getImageUriFromBitmap(requireContext(), takenImage)
                    selectedImageUri = bitToUri
                }
            }
        }
    }

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
                // For content sharing in a more secure way
                photoFile = getPhotoFile(FILE_NAME)
                val fileProvider = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.surgeryapptest.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
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
        when (smiley) {
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
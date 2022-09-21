package com.example.surgeryapptest.ui.fragments.patientFrags.progressEntryDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentWoundDetailsBinding
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.WoundDetailsFragmentViewModel
import com.hsalf.smilerating.BaseRating
import com.hsalf.smilerating.SmileRating
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class WoundDetailsFragment : Fragment(), SmileRating.OnSmileySelectionListener,
    SmileRating.OnRatingSelectedListener {

    private var _binding: FragmentWoundDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var updateUploadedEntryViewModel: WoundDetailsFragmentViewModel

    private var editMode: Boolean = false

    private var woundID: String = ""
    private var painRating: String = ""
    private var title: String = ""
    private var description: String = ""
    private var fluidDrained: String = ""
    private var redness: String = ""
    private var swelling: String = ""
    private var odour: String = ""
    private var fever: String = ""
    private var prevFlag: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        updateUploadedEntryViewModel =
            ViewModelProvider(requireActivity()).get(
                WoundDetailsFragmentViewModel::class.java
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWoundDetailsBinding.inflate(inflater, container, false)
        val view = binding.root


        val args = arguments
        val myBundle: AllProgressBookEntryItem? = args?.getParcelable("progressEntryBundle")

        // Setup image, title, description, entryID
        if (myBundle != null) {
            woundID = myBundle.entryID.toString()
        }
        binding.detailWoundImage.load(myBundle?.progressImage)
        binding.uploadedEntryTitle.setText(myBundle?.progressTitle)
        binding.uploadedEntryDescription.setText(myBundle?.progressDescription)

        // Setup the pain rating slider
        binding.uploadedRatingBar.setOnSmileySelectionListener(this)
        binding.uploadedRatingBar.setOnRatingSelectedListener(this)
        val painLevel: String = myBundle?.quesPain.toString()
        setUploadedPainRating(view, painLevel)

        // Set uploaded rb values
        val fluidAnswer = myBundle?.quesFluid.toString()
        val rednessAnswer = myBundle?.quesRedness.toString()
        val swellingAnswer = myBundle?.quesSwelling.toString()
        val odourAnswer = myBundle?.quesOdour.toString()
        val feverAnswer = myBundle?.quesFever.toString()
        setRbValues(view, fluidAnswer, rednessAnswer, swellingAnswer, odourAnswer, feverAnswer)

        // Enable and disable radio groups
        // Enable and disable smile rating
        enableEditing(editMode, view)

        // Set rb listeners and text listeners for editing
        formInputListener(view)
        radioBtnListeners(view)

        // Listen for edit button
        binding.updateBtn.setOnClickListener {
            editMode = true
            binding.updateBtn.visibility = View.GONE
            binding.saveBtn.visibility = View.VISIBLE
            enableEditing(editMode, view)
        }

        // Listen for save button
        binding.saveBtn.setOnClickListener {
            // save to database after validation
            val isAllFieldFilled = validateFormEntry(
                title, description, fluidDrained, painRating, redness,
                swelling, odour, fever
            )
            //createAlertDialogUpdate()
            println(
                "Updated values: $title, $description, $fluidDrained, $painRating,\n" +
                        "$redness, $swelling, $odour, $fever"
            )

            if (isAllFieldFilled) createAlertDialogUpdate()
        }

        binding.deleteBtn.setOnClickListener {
            createAlertDialogDelete()
        }

        return view
    }

    private fun validateFormEntry(
        title: String,
        description: String,
        fluid: String,
        painRating: String,
        redness: String,
        swelling: String,
        odour: String,
        fever: String
    ): Boolean {
        return when {
            painRating.isEmpty() or description.isBlank() or title.isBlank() or
                    redness.isEmpty() or fluid.isEmpty() or swelling.isEmpty() or
                    odour.isEmpty() or fever.isEmpty() -> {
                binding.woundDetailsFragmentLayout.showSnackBar("Fill in all the fields")
                false
            }
            else -> true
        }
    }

    @SuppressLint("NewApi")
    private fun updateSelectedEntry() {

        binding.updateEntryProgressBar.visibility = View.VISIBLE
        // Pass the values as parameter
        // to be sent to backend
        updateUploadedEntryViewModel.updateUploadedEntry(
            woundID.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            title.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            description.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            fluidDrained.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            painRating.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            redness.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            swelling.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            odour.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            fever.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            //RequestBody.create(MediaType.parse("multipart/form-data"), fever)
        )

        updateUploadedEntryViewModel.updatedEntryResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.updateEntryProgressBar.visibility = View.GONE
                    binding.woundDetailsFragmentLayout.showSnackBar("This entry has been updated successfully")

                    editMode = false
                    binding.updateBtn.visibility = View.VISIBLE
                    binding.saveBtn.visibility = View.GONE
                    enableEditing(editMode, requireView())

                }
                is NetworkResult.Error -> {
                    binding.updateEntryProgressBar.visibility = View.GONE
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
        }
    }

    @SuppressLint("NewApi")
    private fun deleteSelectedEntry() {

        updateUploadedEntryViewModel.deleteUploadedEntry(
            woundID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        updateUploadedEntryViewModel.deletedEntryResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    //binding.woundDetailsFragmentLayout.showSnackBar("${response.data?.message}")

                    if ((response.data?.message.toString()).contains("Successfully")) {
                        MotionToast.createColorToast(
                            requireActivity(),
                            "Done Deleting",
                            response.data?.message.toString(),
                            MotionToastStyle.DELETE,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                        )
                    } else {
                        MotionToast.createColorToast(
                            requireActivity(),
                            "Delete failed!",
                            response.data?.message.toString(),
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                        )
                    }
                }
                is NetworkResult.Error -> {
                    //response.body()!!.message
                    MotionToast.createColorToast(
                        requireActivity(),
                        "Delete failed!",
                        response.data!!.message,
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
                    )
                }
                is NetworkResult.Loading -> {
                    //TODO: Add loading fragment here
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun archiveSelectedEntry() {

        updateUploadedEntryViewModel.archiveUploadedEntry(
            woundID.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            prevFlag.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        updateUploadedEntryViewModel.archivedEntryResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.woundDetailsFragmentLayout.showSnackBar("${response.data?.message}")
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
        }
    }

    // Input listeners
    private fun formInputListener(view: View) {
        binding.uploadedEntryTitle.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    title = it.toString()
                }
            }
        }
        binding.uploadedEntryDescription.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    description = it.toString()
                }
            }
        }
    }

    // Radio button listeners
    private fun radioBtnListeners(view: View) {
        binding.uploadedRgFluidDrainage.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbFluid = view.findViewById<RadioButton>(checkedId)
            fluidDrained = updatedRbFluid.text.toString()
        }
        binding.uploadedRgRedness.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbRedness = view.findViewById<RadioButton>(checkedId)
            redness = updatedRbRedness.text.toString()
        }
        binding.uploadedRgSwelling.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbSwelling = view.findViewById<RadioButton>(checkedId)
            swelling = updatedRbSwelling.text.toString()
        }
        binding.uploadedRgOdour.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbOdour = view.findViewById<RadioButton>(checkedId)
            odour = updatedRbOdour.text.toString()
        }
        binding.uploadedRgFever.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbFever = view.findViewById<RadioButton>(checkedId)
            fever = updatedRbFever.text.toString()
        }
    }

    private fun enableEditing(editMode: Boolean, view: View) {
        // By default editMode == false
        if (!editMode) {
            binding.uploadedEntryTitle.isEnabled = false
            binding.uploadedEntryDescription.isEnabled = false
            enableDisableRadioGroup(binding.uploadedRgFluidDrainage, false)
            enableDisableRadioGroup(binding.uploadedRgRedness, false)
            enableDisableRadioGroup(binding.uploadedRgSwelling, false)
            enableDisableRadioGroup(binding.uploadedRgOdour, false)
            enableDisableRadioGroup(binding.uploadedRgFever, false)
            binding.uploadedRatingBar.isIndicator = true
        } else {
            binding.uploadedEntryTitle.isEnabled = true
            binding.uploadedEntryDescription.isEnabled = true
            enableDisableRadioGroup(binding.uploadedRgFluidDrainage, true)
            enableDisableRadioGroup(binding.uploadedRgRedness, true)
            enableDisableRadioGroup(binding.uploadedRgSwelling, true)
            enableDisableRadioGroup(binding.uploadedRgOdour, true)
            enableDisableRadioGroup(binding.uploadedRgFever, true)
            binding.uploadedRatingBar.isIndicator = false
        }
    }

    private fun setRbValues(
        view: View, fluidAnswer: String, rednessAnswer: String, swellingAnswer: String,
        odourAnswer: String, feverAnswer: String
    ) {
        when (fluidAnswer) {
            "Yes" -> {
                binding.uploadedRgFluidDrainage.check(R.id.rb_fluid_yes)
            }
            "No" -> {
                binding.uploadedRgFluidDrainage.check(R.id.rb_fluid_no)
            }
            "Not sure" -> {
                binding.uploadedRgFluidDrainage.check(R.id.rb_fluid_notSure)
            }
        }
        when (rednessAnswer) {
            "Worse" -> {
                binding.uploadedRgRedness.check(R.id.rb_redness_worse)
            }
            "Same" -> {
                binding.uploadedRgRedness.check(R.id.rb_redness_same)
            }
            "Better" -> {
                binding.uploadedRgRedness.check(R.id.rb_redness_better)
            }
            "Unsure" -> {
                binding.uploadedRgRedness.check(R.id.rb_redness_unsure)
            }
            "None" -> {
                binding.uploadedRgRedness.check(R.id.rb_redness_none)
            }
        }
        when (swellingAnswer) {
            "Worse" -> {
                binding.uploadedRgSwelling.check(R.id.rb_swelling_worse)
            }
            "Same" -> {
                binding.uploadedRgSwelling.check(R.id.rb_swelling_same)
            }
            "Better" -> {
                binding.uploadedRgSwelling.check(R.id.rb_swelling_better)
            }
            "Unsure" -> {
                binding.uploadedRgSwelling.check(R.id.rb_swelling_unsure)
            }
            "None" -> {
                binding.uploadedRgSwelling.check(R.id.rb_swelling_none)
            }
        }
        when (odourAnswer) {
            "Yes" -> {
                binding.uploadedRgOdour.check(R.id.rb_odour_yes)
            }
            "No" -> {
                binding.uploadedRgOdour.check(R.id.rb_odour_no)
            }
            "Not sure" -> {
                binding.uploadedRgOdour.check(R.id.rb_odour_not_sure)
            }
        }
        when (feverAnswer) {
            "Yes but did not take the temperature" -> {
                binding.uploadedRgFever.check(R.id.rb_fever_yes)
            }
            "No" -> {
                binding.uploadedRgFever.check(R.id.rb_fever_no)
            }
        }
    }

    private fun setUploadedPainRating(view: View, myBundle: String) {
        if (myBundle == "TERRIBLE") {
            binding.uploadedRatingBar.selectedSmile = BaseRating.TERRIBLE
        }
        if (myBundle == "BAD") {
            binding.uploadedRatingBar.selectedSmile = BaseRating.BAD
        }
        if (myBundle == "OKAY") {
            binding.uploadedRatingBar.selectedSmile = BaseRating.OKAY
        }
        if (myBundle == "GOOD") {
            binding.uploadedRatingBar.selectedSmile = BaseRating.GOOD
        }
        if (myBundle == "GREAT") {
            binding.uploadedRatingBar.selectedSmile = BaseRating.GREAT
        }
        if (myBundle == "NONE") {
            binding.uploadedRatingBar.selectedSmile = BaseRating.NONE
        }
    }

    private fun enableDisableRadioGroup(radioGroup: RadioGroup, enable_or_disable: Boolean) {
        for (i in 0 until radioGroup.childCount) {
            (radioGroup.getChildAt(i) as RadioButton).isEnabled = enable_or_disable
        }
    }

    //TODO: Move to AppUtils later on
    private fun createAlertDialogUpdate() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm edit?")
        builder.setMessage("\nAre you sure you want to make changes to this uploaded entry?")
        builder.setIcon(R.drawable.ic_confirm)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            updateSelectedEntry()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun createAlertDialogDelete() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete this entry?")
        builder.setMessage("\nAre you sure you want to delete this uploaded entry?")
        builder.setIcon(R.drawable.ic_delete)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            createAlertDialogDeleteDoubleWarning()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }

    private fun createAlertDialogDeleteDoubleWarning() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Maybe archive?")
        builder.setMessage("\nDo you want to archive this instead?")
        builder.setIcon(R.drawable.ic_delete)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            println("The delete function has been pressed ....")
            archiveSelectedEntry()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        builder.setNeutralButton("DELETE") { _, _ ->
            deleteSelectedEntry()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
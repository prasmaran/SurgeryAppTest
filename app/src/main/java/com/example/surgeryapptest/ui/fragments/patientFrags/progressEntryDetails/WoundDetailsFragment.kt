package com.example.surgeryapptest.ui.fragments.patientFrags.progressEntryDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.surgeryapptest.R
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.utils.app.AppUtils.Companion.showSnackBar
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.WoundDetailsFragmentViewModel
import com.hsalf.smilerating.BaseRating
import com.hsalf.smilerating.SmileRating
import kotlinx.android.synthetic.main.fragment_wound_details.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class WoundDetailsFragment : Fragment(), SmileRating.OnSmileySelectionListener,
    SmileRating.OnRatingSelectedListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the view models here
        updateUploadedEntryViewModel =
            ViewModelProvider(requireActivity()).get(
                "KEY1",
                WoundDetailsFragmentViewModel::class.java
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wound_details, container, false)

        val args = arguments
        val myBundle: AllProgressBookEntryItem? = args?.getParcelable("progressEntryBundle")

        // Setup image, title, description, entryID
        if (myBundle != null) {
            woundID = myBundle.entryID.toString()
        }
        view.detail_wound_image.load(myBundle?.progressImage)
        view.uploaded_entry_title.setText(myBundle?.progressTitle)
        view.uploaded_entry_description.setText(myBundle?.progressDescription)

        // Setup the pain rating slider
        view.uploaded_rating_bar.setOnSmileySelectionListener(this)
        view.uploaded_rating_bar.setOnRatingSelectedListener(this)
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
        view.update_btn.setOnClickListener {
            editMode = true
            view.update_btn.visibility = View.GONE
            view.save_btn.visibility = View.VISIBLE
            enableEditing(editMode, view)
        }

        // Listen for save button
        view.save_btn.setOnClickListener {
            // save to database after validation
            //val isAllFieldFilled = validateFormEntry(
            //    view, title, description, fluidDrained, painRating,
            //    redness, swelling, odour, fever
            //)
            createAlertDialogUpdate()
            println(
                "Updated values: $title, $description, $fluidDrained, $painRating,\n" +
                        "$redness, $swelling, $odour, $fever"
            )

//            if (isAllFieldFilled) {
//                createAlertDialogUpdate()
//            } else {
//                AppUtils.showToast(requireContext(), "Please fill all fields !")
//            }
        }

        view.delete_btn.setOnClickListener {
            createAlertDialogDelete()
            //AppUtils.showToast(requireContext(), "Need to setup the delete function")
        }

        return view
    }

    private fun validateFormEntry(
        view: View,
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
                view.woundDetailsFragmentLayout.showSnackBar("Fill in all the fields")
                return false
            }
            else -> return true
        }
    }

    private fun updateSelectedEntry() {
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

        updateUploadedEntryViewModel.updatedEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    view?.woundDetailsFragmentLayout?.showSnackBar("This entry has been updated successfully")
                    //this.findNavController().popBackStack()
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

    private fun deleteSelectedEntry() {

        updateUploadedEntryViewModel.deleteUploadedEntry(
            woundID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        updateUploadedEntryViewModel.deletedEntryResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    view?.woundDetailsFragmentLayout?.showSnackBar("${response.data?.message}")
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

    // Input listeners
    private fun formInputListener(view: View) {
        view.uploaded_entry_title.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    title = it.toString()
                }
            }
        }
        view.uploaded_entry_description.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    description = it.toString()
                }
            }
        }
    }

    // Radio button listeners
    private fun radioBtnListeners(view: View) {
        view.uploaded_rgFluidDrainage.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbFluid = view.findViewById<RadioButton>(checkedId)
            fluidDrained = updatedRbFluid.text.toString()
        }
        view.uploaded_rgRedness.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbRedness = view.findViewById<RadioButton>(checkedId)
            redness = updatedRbRedness.text.toString()
        }
        view.uploaded_rgSwelling.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbSwelling = view.findViewById<RadioButton>(checkedId)
            swelling = updatedRbSwelling.text.toString()
        }
        view.uploaded_rgOdour.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbOdour = view.findViewById<RadioButton>(checkedId)
            odour = updatedRbOdour.text.toString()
        }
        view.uploaded_rgFever.setOnCheckedChangeListener { _, checkedId ->
            val updatedRbFever = view.findViewById<RadioButton>(checkedId)
            fever = updatedRbFever.text.toString()
        }
    }

    private fun enableEditing(editMode: Boolean, view: View) {
        // By default editMode == false
        if (!editMode) {
            view.uploaded_entry_title.isEnabled = false
            view.uploaded_entry_description.isEnabled = false
            enableDisableRadioGroup(view.uploaded_rgFluidDrainage, false)
            enableDisableRadioGroup(view.uploaded_rgRedness, false)
            enableDisableRadioGroup(view.uploaded_rgSwelling, false)
            enableDisableRadioGroup(view.uploaded_rgOdour, false)
            enableDisableRadioGroup(view.uploaded_rgFever, false)
            view.uploaded_rating_bar.isIndicator = true
        } else {
            view.uploaded_entry_title.isEnabled = true
            view.uploaded_entry_description.isEnabled = true
            enableDisableRadioGroup(view.uploaded_rgFluidDrainage, true)
            enableDisableRadioGroup(view.uploaded_rgRedness, true)
            enableDisableRadioGroup(view.uploaded_rgSwelling, true)
            enableDisableRadioGroup(view.uploaded_rgOdour, true)
            enableDisableRadioGroup(view.uploaded_rgFever, true)
            view.uploaded_rating_bar.isIndicator = false
        }
    }

    private fun setRbValues(
        view: View, fluidAnswer: String, rednessAnswer: String, swellingAnswer: String,
        odourAnswer: String, feverAnswer: String
    ) {
        when (fluidAnswer) {
            "Yes" -> {
                view.uploaded_rgFluidDrainage?.check(R.id.rb_fluid_yes)
            }
            "No" -> {
                view.uploaded_rgFluidDrainage?.check(R.id.rb_fluid_no)
            }
            "Not sure" -> {
                view.uploaded_rgFluidDrainage?.check(R.id.rb_fluid_notSure)
            }
        }
        when (rednessAnswer) {
            "Worse" -> {
                view.uploaded_rgRedness?.check(R.id.rb_redness_worse)
            }
            "Same" -> {
                view.uploaded_rgRedness?.check(R.id.rb_redness_same)
            }
            "Better" -> {
                view.uploaded_rgRedness?.check(R.id.rb_redness_better)
            }
            "Unsure" -> {
                view.uploaded_rgRedness?.check(R.id.rb_redness_unsure)
            }
            "None" -> {
                view.uploaded_rgRedness?.check(R.id.rb_redness_none)
            }
        }
        when (swellingAnswer) {
            "Worse" -> {
                view.uploaded_rgSwelling?.check(R.id.rb_swelling_worse)
            }
            "Same" -> {
                view.uploaded_rgSwelling?.check(R.id.rb_swelling_same)
            }
            "Better" -> {
                view.uploaded_rgSwelling?.check(R.id.rb_swelling_better)
            }
            "Unsure" -> {
                view.uploaded_rgSwelling?.check(R.id.rb_swelling_unsure)
            }
            "None" -> {
                view.uploaded_rgSwelling?.check(R.id.rb_swelling_none)
            }
        }
        when (odourAnswer) {
            "Yes" -> {
                view.uploaded_rgOdour?.check(R.id.rb_odour_yes)
            }
            "No" -> {
                view.uploaded_rgOdour?.check(R.id.rb_odour_no)
            }
            "Not sure" -> {
                view.uploaded_rgOdour?.check(R.id.rb_odour_not_sure)
            }
        }
        when (feverAnswer) {
            "Yes but did not take the temperature" -> {
                view.uploaded_rgFever?.check(R.id.rb_fever_yes)
            }
            "No" -> {
                view.uploaded_rgFever?.check(R.id.rb_fever_no)
            }
        }
    }

    private fun setUploadedPainRating(view: View, myBundle: String) {
        if (myBundle == "TERRIBLE") {
            view.uploaded_rating_bar?.selectedSmile = BaseRating.TERRIBLE
        }
        if (myBundle == "BAD") {
            view.uploaded_rating_bar?.selectedSmile = BaseRating.BAD
        }
        if (myBundle == "OKAY") {
            view.uploaded_rating_bar?.selectedSmile = BaseRating.OKAY
        }
        if (myBundle == "GOOD") {
            view.uploaded_rating_bar?.selectedSmile = BaseRating.GOOD
        }
        if (myBundle == "GREAT") {
            view.uploaded_rating_bar?.selectedSmile = BaseRating.GREAT
        }
        if (myBundle == "NONE") {
            view.uploaded_rating_bar?.selectedSmile = BaseRating.NONE
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
        builder.setTitle("Confirm delete?")
        builder.setMessage("\nAre you sure you want to delete this uploaded entry?")
        builder.setIcon(R.drawable.ic_delete)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            println("The delete function has been pressed ....")
            deleteSelectedEntry()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
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


}
package com.example.surgeryapptest.ui.streamChat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentLoginBinding
import com.example.surgeryapptest.model.network.chatUser.ChatUser
import com.example.surgeryapptest.utils.app.AppUtils
import com.example.surgeryapptest.utils.app.SessionManager
import com.example.surgeryapptest.utils.constant.Constants
import com.example.surgeryapptest.view_models.patient.UserProfileFragmentViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var userProfileViewModel: UserProfileFragmentViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // To read saved user name and details
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileViewModel =
            ViewModelProvider(requireActivity()).get(UserProfileFragmentViewModel::class.java)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            authenticateTheUser()
        }

        readSavedUserProfileDetails()

        return binding.root
    }

    private fun authenticateTheUser() {
        val firstName = binding.firstNameEditText.text.toString()
        val username = binding.usernameEditText.text.toString()
        if (validateInput(firstName, binding.firstNameInputLayout) &&
            validateInput(username, binding.usernameInputLayout)
        ) {
            val chatUser = ChatUser(firstName, username)
            val action = LoginFragmentDirections.actionLoginFragmentToChannelFragment(chatUser)
            findNavController().navigate(action)
        }
    }

    private fun validateInput(inputText: String, textInputLayout: TextInputLayout): Boolean {
        return if (inputText.length <= 3) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = "* Minimum 4 Characters Allowed"
            false
        } else {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
            true
        }
    }

    private fun readSavedUserProfileDetails() {

        var userName = ""

        // TODO: Create API to update user contact details
        /** Listen to the changes and update in Ui
         * Send changes to server and return the updated response */
        viewLifecycleOwner.lifecycleScope.launch {
            userProfileViewModel.readUserProfileDetail.collect { values ->
                val username = "${values.userName}_${values.userID}_${values.userType}"
                userName = (username + "_${values.userGender}").lowercase()
                binding.firstNameInputLayout.editText!!.setText(userName)
                binding.usernameInputLayout.editText!!.setText(userName)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
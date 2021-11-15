package com.example.surgeryapptest.ui.fragments.patientFrags

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.surgeryapptest.R
import com.example.surgeryapptest.databinding.FragmentPatientEmergencyCallBinding
import com.example.surgeryapptest.utils.app.AppUtils

class PatientEmergencyCallFragment : Fragment() {

    private var _binding: FragmentPatientEmergencyCallBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PHONE_CALL_CODE = 42
    }

    private lateinit var mView: View

    private var prefix: String = "+6"
    private var selectedNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientEmergencyCallBinding.inflate(inflater, container, false)
        val view = binding.root

        callPPUMEmergency()
        call999Emergency()

        return view
    }

    private fun callPPUMEmergency() {
        binding.ppumEmergencyBtn.setOnClickListener {
            selectedNumber = getString(R.string.ppum_emergency_number_ori)
            checkPermission()
        }
    }

    private fun call999Emergency() {
        binding.generalEmergencyBtn.setOnClickListener {
            selectedNumber = getString(R.string.mers_number)
            AppUtils.showToast(requireContext(),"999 has been clicked")
            //checkPermission()
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PHONE_CALL_CODE
                )
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PHONE_CALL_CODE) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted
                callPhone()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Access has been denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$prefix$selectedNumber"))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.ritikprajapati.peacetravel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ritikprajapati.peacetravel.databinding.FragmentRequirementBinding
import java.util.Calendar

class RequirementFragment : Fragment() {

    private lateinit var binding: FragmentRequirementBinding

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequirementBinding.inflate(inflater, container, false)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate = "$year-$month-$day"


        val spinnerFromLocation: Spinner = binding.spinnerFromLocation
        val spinnerToLocation: Spinner = binding.spinnerToLocation
        val spinnerTimeRange: Spinner = binding.spinnerTime

        val adapterFromLocation = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        )
        val adapterToLocation = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        )
        val adapterTime= ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_options_time,
            android.R.layout.simple_spinner_item
        )

        database = FirebaseDatabase.getInstance().reference

        binding.buttonSubmitData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmation!")
                .setMessage("Do you really want to submit the data?")
                .setPositiveButton("Confirm") { _, _ ->
                    saveDataToFirebase()
                }
                .setNegativeButton("No wait") {dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.cardViewFromLocation.setOnClickListener {
            spinnerFromLocation.performClick() // Open the spinner options
        }
        binding.cardViewToLocation.setOnClickListener {
            spinnerToLocation.performClick() // Open the spinner options
        }
        binding.cardViewTime.setOnClickListener {
            spinnerTimeRange.performClick() // Open the spinner options
        }

        adapterFromLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFromLocation.adapter = adapterFromLocation

        adapterToLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerToLocation.adapter = adapterToLocation

        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeRange.adapter = adapterTime
        return binding.root
    }

    private fun saveDataToFirebase() {
        val userName = binding.editTextName.text.toString().trim()
        val phoneNumber = binding.editTextPhoneNo.text.toString().trim()
        val fromLocation = binding.spinnerFromLocation.selectedItem.toString()
        val toLocation = binding.spinnerToLocation.selectedItem.toString()
        val time = binding.spinnerTime.selectedItem.toString()

        if (userName.isEmpty()) {
            Snackbar.make(requireView(), "Please enter all details!", Snackbar.LENGTH_SHORT).show()
            return
        }
//        else if(phoneNumber.length!=10) {
//            Snackbar.make(requireView(), "Invalid phone number!", Snackbar.LENGTH_SHORT).show()
//            return
//        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate = "$year-$month-$day"

        val userId = getUidFromSharedPreferences() ?: ""
        if (userId.isBlank()) {
            Snackbar.make(requireView(), "User ID not found!", Snackbar.LENGTH_SHORT).show()
            return
        }

        val user = User(userName, phoneNumber, fromLocation, toLocation, time, currentDate, userId)

        //Save data to firebase
            database.child("users").push().setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(requireView(), "Data saved successfully!", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Snackbar.make(requireView(), "Failed to save data!", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getUidFromSharedPreferences(): String? {
        val sharedPref = context?.getSharedPreferences("MyPrefs", -Context.MODE_PRIVATE)
        return sharedPref?.getString("UID", null)
    }

}
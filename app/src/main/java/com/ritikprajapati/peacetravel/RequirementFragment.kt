package com.ritikprajapati.peacetravel

import android.content.Context
import android.os.Build
import android.os.Bundle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.ritikprajapati.peacetravel.databinding.FragmentRequirementBinding
import java.util.Calendar

class RequirementFragment : Fragment() {

    private lateinit var binding: FragmentRequirementBinding
    private lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
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

        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH")
        val formattedHour = now.format(formatter)
        println(formattedHour) // Prints the current hour formatted as two digits, e.g., "01", "14"


        val spinnerFromLocation: Spinner = binding.spinnerFromLocation
        val spinnerToLocation: Spinner = binding.spinnerToLocation
        val spinnerTimeRange: Spinner = binding.spinnerTime

        val timeRangeData: Array<String> = arrayOf(
            "07.40-08.00", "08.40-09.00", "09.40-10.00", "10.40-11.00",
            "11.40-12.00", "12.40-13.00", "13.40-14.00", "14.40-15.00",
            "15.40-16.00", "16.40-17.00", "17.40-18.00", "18.40-19.00",
            "19.40-20.00", "20.40-21.00", "21.40-22.00", "22.40-23.00",
            "23.40-24.00"
        )


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
                    checkForDuplicates { isDuplicate ->
                        if (isDuplicate) {
                            Snackbar.make(requireView(), "Duplicate entry found for this time range! To save this data, delete the previous one through the three dot option in the toolbar. But be careful, doing so will delete all your data in all other time ranges too.", Snackbar.LENGTH_SHORT)
                                .setDuration(5000)
                                .show()
                        } else {
                            saveDataToFirebase()
                        }
                    }
                }
                .setNegativeButton("No wait") { dialog, _ ->
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDataToFirebase() {
        val userName = "a"
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
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = "$year-$month-$day"

        val userId = getUidFromSharedPreferences() ?: ""
        if (userId.isBlank()) {
            Snackbar.make(requireView(), "User ID not found!", Snackbar.LENGTH_SHORT).show()
            return
        }

        fetchUserName(userId) { userName ->
            if (userName.isEmpty()) {
                Snackbar.make(requireView(), "Failed to retrieve user name!", Snackbar.LENGTH_SHORT)
                    .show()
                return@fetchUserName
            }

            val user =
                User(userName, phoneNumber, fromLocation, toLocation, time, currentDate, userId)

            if (checkTimeAvailability()) {
                //Save data to firebase
                database.child("users").push().setValue(user).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(
                            requireView(),
                            "Data saved successfully!",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Snackbar.make(requireView(), "Failed to save data!", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Snackbar.make(requireView(), "You can only save data for future not for past time!", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun fetchUserName(userId: String, callback: (String) -> Unit) {
        database.child("newUsers").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                val lastName = snapshot.child("lastName").getValue(String::class.java) ?: ""
                val userName = "$firstName $lastName".trim()
                callback(userName)
            }

            override fun onCancelled(error: DatabaseError) {
                callback("")
            }
        })
    }

    private fun checkForDuplicates(callback: (Boolean) -> Unit) {
        val time = binding.spinnerTime.selectedItem.toString()
        val userId = getUidFromSharedPreferences() ?: ""
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = "$year-$month-$day"

        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var isDuplicate = false
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.time == time && user.id == userId && user.date == currentDate) {
                        isDuplicate = true
                        break
                    }
                }
                callback(isDuplicate)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
                callback(false)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkTimeAvailability(): Boolean {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH")
        val formattedHour = now.format(formatter).toInt()

        val selectedTimeRange = binding.spinnerTime.selectedItem.toString()
        val endHourString = selectedTimeRange.substring(6, 8) // Extracting the end hour part
        val endHour = endHourString.toInt() + 1

        return formattedHour < endHour
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getUidFromSharedPreferences(): String? {
        val sharedPref = context?.getSharedPreferences("MyPrefs", -Context.MODE_PRIVATE)
        return sharedPref?.getString("UID", null)
    }

}
package com.ritikprajapati.peacetravel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.ritikprajapati.peacetravel.databinding.ActivityGiveInRentBinding

class GiveInRentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiveInRentBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var RentList : MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiveInRentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val name:TextInputEditText = binding.editTextName
        val phone:TextInputEditText = binding.editTextPhoneNo
        val hall:TextInputEditText = binding.editTextHall
        val question:TextInputEditText = binding.textGiveTake
        val uid = getUidFromSharedPreferences()


        binding.buttonSubmitGiveRent.setOnClickListener {
            val nameText = name.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val hallText = hall.text.toString().trim()
            val takeGive = question.text.toString().trim()
            AlertDialog.Builder(this)
                .setTitle("Confirmation!")
                .setMessage("Do you really want to submit the data?")
                .setPositiveButton("Confirm") { _, _ ->
                    if (nameText.isNotEmpty() && phoneText.isNotEmpty() && hallText.isNotEmpty()) {
                        if (uid != null) {
                            saveDataToFirebase(nameText, phoneText, hallText, takeGive,uid)
                        }
                    } else {
                        Snackbar.make(binding.root, "Data incomplete", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No wait") {dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.buttonDeleteRent.setOnClickListener {
            val currentUser = auth.currentUser
            AlertDialog.Builder(this)
                .setTitle("Confirmation!")
                .setMessage("Do you really want to delete the data?")
                .setPositiveButton("Confirm") { _, _ ->
                    if (currentUser != null) {
                        if (uid != null) {
                            deleteDataFromFirebase(uid)
                        }
                    } else {
                        Snackbar.make(binding.root, "User not logged in", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No wait") {dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        }
    }
    private fun getUidFromSharedPreferences(): String? {
        val sharedPref = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("UID", null)
    }

    private fun deleteDataFromFirebase(phoneNumber: String) {
        val query: Query = database.child("rent").orderByChild("id").equalTo(phoneNumber)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(binding.root, "Data deleted successfully", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(binding.root, "Failed to delete data", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Snackbar.make(binding.root, "Error: ${databaseError.message}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun saveDataToFirebase(name: String, phone: String, hall: String, giveOrTake: String, uid : String) {
        val userId = database.push().key ?: return
        val rent = Rent(name, phone, hall, giveOrTake,uid)
        database.child("rent").child(userId).setValue(rent)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "Data saved successfully", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Data saving failed", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

}
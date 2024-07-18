package com.ritikprajapati.peacetravel

import UserAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ritikprajapati.peacetravel.databinding.ActivityPeopleThisTimeBinding
import java.util.Calendar

class PeopleThisTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPeopleThisTimeBinding
    private lateinit var database: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private var userList : MutableList<User> = mutableListOf()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleThisTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("users")

        userAdapter = UserAdapter(userList, this)

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@PeopleThisTimeActivity)
            adapter = userAdapter
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate2 = "$year-$month-$day"

//        val selectedTime = intent.getStringExtra("Distance")
//        if(selectedTime != null) {
//            fetchUsersByTimeRange(selectedTime)
//        }
        Log.d("PeopleThisTimeActivity", "Fetching data from Firebase")

        val selectedTime = intent.getStringExtra("TimeRange")
        selectedTime?.let {
            Log.d("PeopleThisTimeActivity", "Selected time: $selectedTime")
            fetchUsersByTimeRange(selectedTime, currentDate2)
        } ?: Log.e("PeopleThisTimeActivity", "No time selected")
    }

    private fun fetchUsersByTimeRange(timeRange: String, dateToday: String) {

        Log.d("PeopleThisTimeActivity", "Fetching users for time range: $timeRange")

        database.orderByChild("time").equalTo(timeRange)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot : DataSnapshot) {
                    userList.clear()

                    Log.d("PeopleThisTimeActivity", "Data snapshot received: ${snapshot.childrenCount} children")

                    for(userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let {
                            if(it.date==dateToday && currentUserId!=it.id)
                            userList.add(it)
                            else if(it.date==dateToday && currentUserId==it.id){
                                it.userName = it.userName+"(You)"
                                userList.add(it)
                            } else {

                            }
                        }
                    }
                    userAdapter.notifyDataSetChanged()

                    if (userList.isEmpty()) {
                        showEmptyScreenMessage()
                    }

                    Log.d("PeopleThisTimeActivity", "User list updated: ${userList.size} users")

                }
                override fun onCancelled(error: DatabaseError) {

                    Log.e("PeopleThisTimeActivity", "Database error: ${error.message}")

                }
            })
    }

    private fun showEmptyScreenMessage() {
        Snackbar.make(
            binding.root,
            "No users found for the selected time range",
            Snackbar.LENGTH_SHORT
        ).show()
    }

}
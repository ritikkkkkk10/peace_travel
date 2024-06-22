package com.ritikprajapati.peacetravel

import RentAdapter
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ViewRentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rentAdapter: RentAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_rent)

        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        rentAdapter = RentAdapter(ArrayList()) // Initialize the adapter with an empty list
        recyclerView.adapter = rentAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("rent")

        // Add a ValueEventListener to fetch data from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rentList = ArrayList<Rent>()
                for (rentSnapshot in snapshot.children) {
                    val rent = rentSnapshot.getValue(Rent::class.java)
                    rent?.let { rentList.add(it) }
                }
                rentAdapter.updateList(rentList) // Update the adapter with the fetched data
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
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
}

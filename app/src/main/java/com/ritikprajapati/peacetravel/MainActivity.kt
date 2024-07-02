package com.ritikprajapati.peacetravel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.ritikprajapati.peacetravel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AlertDialog.Builder(this)
            .setTitle("Note!")
            .setMessage("Before texting anyone, enter your data as well, else the other user won't be able to see your text!")
            .setNegativeButton("Okay") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        true

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        binding.pager.adapter = ScreenSlidePagerAdapter(this)

//        if (savedInstanceState == null) {
//            val fragment = RequirementFragment()
//            supportFragmentManager.beginTransaction()
//                .add(R.id.pager, fragment)
//                .commit()
//        }


        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Requirements"
                1 -> tab.text = "Availability"
            }
        }.attach()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuClickHandled = when (item.itemId) {
            R.id.logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Warning!")
                    .setMessage("Confirm logout")
                    .setPositiveButton("Yes") { _, _ ->
                        FirebaseAuth.getInstance().signOut();
                        val view = this@MainActivity.currentFocus ?: binding.root
                        Snackbar.make(view, "Logged out", Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(this, StartActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }

            R.id.feedback -> {
                AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("You will be directed to Gmail")
                    .setPositiveButton("Confirm") { _, _ ->
                        feedbackGmail()
                    }
                    .setNegativeButton("Stay") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }

            R.id.delete -> {
                AlertDialog.Builder(this)
                    .setTitle("Confirm deletion?")
                    .setMessage("All of your saved details for all the time ranges will be deleted")
                    .setPositiveButton("Confirm") { _, _ ->
                        val uid = getUidFromSharedPreferences()
                        if (uid != null) {
                            deleteData(uid)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
            R.id.email -> {
                val userId = getUidFromSharedPreferences() ?: ""
                fetchUserEmail(userId) { email ->
                    AlertDialog.Builder(this)
                        .setTitle("You are logged in as:")
                        .setMessage(email)
                        .setNegativeButton("Okay") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                true
            }
            R.id.privacy_policy -> {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                true
            }
            R.id.delete_account -> {
                AlertDialog.Builder(this)
                    .setTitle("Warning!")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes") { _, _ ->
                        val uid = getUidFromSharedPreferences()
                        if (uid != null) {
                            deleteData(uid)
                            deleteUserAccount()
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return menuClickHandled
    }

    private fun fetchUserEmail(userId: String, callback: (String) -> Unit) {
        database.child("newUsers").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                callback(email)
            }

            override fun onCancelled(error: DatabaseError) {
                callback("")
            }
        })
    }

    private fun deleteUserAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(binding.root, "Account deleted successfully", Snackbar.LENGTH_SHORT).show()
                startActivity(Intent(this, StartActivity::class.java))
                finish()
            } else {
                Snackbar.make(binding.root, "Failed to delete account: ${task.exception?.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun deleteData(uid: String) {
        val query: Query = database.child("users").orderByChild("id").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(
                                binding.root,
                                "Data deleted successfully",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Failed to delete data",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Snackbar.make(
                    binding.root,
                    "Error: ${databaseError.message}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun feedbackGmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("peacetravel001@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback for Peace Travel")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Snackbar.make(binding.root, "No email app found", Snackbar.LENGTH_SHORT).show()
        }
    }


    private class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RequirementFragment()
                else -> AvailabilityFragment()
            }
        }
    }

    private fun getUidFromSharedPreferences(): String? {
        val sharedPref = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("UID", null)
    }
}
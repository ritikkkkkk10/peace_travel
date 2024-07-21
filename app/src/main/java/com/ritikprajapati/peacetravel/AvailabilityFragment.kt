package com.ritikprajapati.peacetravel

import UserAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ritikprajapati.peacetravel.databinding.FragmentAvailabilityBinding
import com.ritikprajapati.peacetravel.databinding.FragmentRequirementBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AvailabilityFragment : Fragment() {

    private lateinit var binding: FragmentAvailabilityBinding
    private lateinit var database: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private var userList: MutableList<User> = mutableListOf()
    private lateinit var chatRooms: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAvailabilityBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance().reference.child("users")
        chatRooms = FirebaseDatabase.getInstance().reference.child("chatRooms")

        binding.cardViewBuy.setOnClickListener { startActivity(Intent(context, ViewRentActivity::class.java)) }

        binding.cardViewTimeFilter.setOnClickListener { startActivity(Intent(context, TimeFilterActivity::class.java)) }
        binding.cardViewRentSell.setOnClickListener { startActivity(Intent(context, GiveInRentActivity::class.java)) }
        binding.cardViewPlaceFilter.setOnClickListener { startActivity(Intent(context, PeopleTextedListActivity::class.java)) }
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate2 = "$year-$month-$day"

        // Prints the current hour formatted as two digits, e.g., "01", "14"

        fetchUsers(currentDate2)
    }

    private fun openLinkedIn() {
        val linkedinUrl = "https://www.linkedin.com/in/ritik-prajapati-b65b9424b/"

        // Try to open the LinkedIn app
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://in/your-profile-id"))
            startActivity(intent)
        } catch (e: Exception) {
            // If the LinkedIn app is not installed, open the URL in a web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl))
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUsers(dateToday: String) {
        Log.d("PeopleTextedListActivity", "Fetching users for today's date: $dateToday")
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH")
        val formattedHour = now.format(formatter)
        println(formattedHour)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        var newChats=0

        if (currentUserId == null) {
            Log.e("PeopleTextedListActivity", "Current user is not authenticated.")
            return
        }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                Log.d("PeopleTextedListActivity", "Data snapshot received: ${snapshot.childrenCount} children")

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    //change1
                    user?.let {
                        if (it.date == dateToday && it.time.substring(6,8) > formattedHour) {
                            val chatRoomId = generateChatRoomId(currentUserId, it.id)

                            chatRooms.child(chatRoomId).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                @SuppressLint("SuspiciousIndentation")
                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun onDataChange(chatSnapshot: DataSnapshot) {
                                    if (chatSnapshot.exists()) {
                                        var unreadCount = 0

                                        for (chat in chatSnapshot.children) {
                                            val seenByMe = chat.child("seenByMe").getValue(Boolean::class.java) ?: true
                                            val senderUid = chat.child("senderUid").getValue(String::class.java)

                                            if (!seenByMe && senderUid != currentUserId) {
                                                unreadCount++
                                            }
                                        }
                                        if(unreadCount > 0) {
                                            newChats++
                                        }
                                        Log.e("AvailabilityFragment", "no of new chats: ${newChats}")
                                        binding.unreadChatsNotif.text = newChats.toString()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("PeopleTextedListActivity", "ChatRoom database error: ${error.message}")
                                }
                            })
                        }
                    }
                }


                Log.d("PeopleTextedListActivity", "User list updated: ${userList.size} users")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PeopleTextedListActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun generateChatRoomId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            userId1 + userId2
        } else {
            userId2 + userId1
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("AvailabilityFragment", "in the am availability")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate2 = "$year-$month-$day"
        fetchUsers(currentDate2)
    }

}
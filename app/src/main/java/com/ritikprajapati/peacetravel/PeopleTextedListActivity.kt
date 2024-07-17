package com.ritikprajapati.peacetravel

import UserAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ritikprajapati.peacetravel.databinding.ActivityPeopleTextedListBinding
import java.util.Calendar

class PeopleTextedListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPeopleTextedListBinding
    private lateinit var database: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private var userList : MutableList<User> = mutableListOf()
    private lateinit var chatRooms: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleTextedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("users")
        chatRooms = FirebaseDatabase.getInstance().reference.child("chatRooms")

        userAdapter = UserAdapter(userList, this)

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@PeopleTextedListActivity)
            adapter = userAdapter
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate2 = "$year-$month-$day"

        Log.d("PeopleTextedListActivity", "Fetching data from Firebase")
    }

    private fun fetchUsersByTimeRange(dateToday: String) {
        Log.d("PeopleTextedListActivity", "Fetching users for today's date: $dateToday")

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Log.e("PeopleTextedListActivity", "Current user is not authenticated.")
            showEmptyScreenMessage("User not authenticated.")
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
                        if (it.date == dateToday) {
                            val chatRoomId = generateChatRoomId(currentUserId, it.id)

                            chatRooms.child(chatRoomId).addListenerForSingleValueEvent(object : ValueEventListener {
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

                                        // Update user with unread message count
                                        it.unreadMessagesCount = unreadCount
                                        Log.d("PeopleTextedListActivity", "User ${it.id} has $unreadCount unread messages")

                                        userList.add(it)
                                        userAdapter.notifyDataSetChanged()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("PeopleTextedListActivity", "ChatRoom database error: ${error.message}")
                                }
                            })
                        }
                    }
                }

                if (userList.isEmpty()) {
                    showEmptyScreenMessage("No users found for today's date.")
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
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate = "$year-$month-$day"

        Log.d("PeopleTextedListActivity", "Refreshing data for current date: $currentDate")
        fetchUsersByTimeRange(currentDate)
    }


    private fun showEmptyScreenMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

}

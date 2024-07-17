package com.ritikprajapati.peacetravel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.properties.Delegates

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<Message>()
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatRoomId: String
    private lateinit var userName: String
    private lateinit var databaseReference: DatabaseReference
    private var onScreen by Delegates.notNull<Boolean>()

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val currentDateChat = "$year-$month-$day"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Get extras from intent
        chatRoomId = intent.getStringExtra("CHAT_ROOM_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: "Unknown"

        // Set the user name in the header
        findViewById<TextView>(R.id.user_name).text = userName

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("chatRooms").child(chatRoomId)

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_chat)
        messageInput = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.button_send)

        // Set up RecyclerView
        chatAdapter = ChatAdapter(messageList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Load messages from Firebase
        loadMessages()

        // Send button click listener
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }
    }

    private fun loadMessages() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                if (message != null && currentUserUid != null && currentDateChat == message.date) {
                    // Set isSent based on the senderUid
                    message.isSent = message.senderUid == currentUserUid

                    // Update seenByMe if the message is not sent by the current user
                    if (message.senderUid != currentUserUid && onScreen) {
                        message.SeenByMe = true
                        // Update the message in the database
                        snapshot.ref.child("seenByMe").setValue(true)

                    }
                    messageList.add(message)
                    chatAdapter.notifyItemInserted(messageList.size - 1)
                    recyclerView.scrollToPosition(messageList.size - 1)
                } else {
                    Log.e("ChatActivity", "Received null message from Firebase or user UID is null")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatActivity", "Failed to load messages: ${error.message}")
            }
        })
    }

    private fun sendMessage(messageText: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid == null) {
            Log.e("ChatActivity", "Current user UID is null")
            return
        }

        val messageId = databaseReference.push().key ?: return

        val message = Message(content = messageText, senderUid = currentUserUid, date = currentDateChat, SeenByMe = false)

        databaseReference.child(messageId).setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                messageInput.text.clear()
            } else {
                Log.e("ChatActivity", "Failed to send message: ${task.exception?.message}")
            }
        }
    }

    private fun getUidFromSharedPreferences(): String? {
        val sharedPref = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("UID", null)
    }

    override fun onPause() {
        super.onPause()
        onScreen = false
        Log.e("ChatActivity", "Failed to send message: i am in pause}")
    }

    override fun onStart() {
        super.onStart()
        onScreen = true
        Log.e("ChatActivity", "Failed to send message: i am in start")
    }
}

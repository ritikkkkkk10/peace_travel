import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ritikprajapati.peacetravel.ChatActivity
import com.ritikprajapati.peacetravel.R
import com.ritikprajapati.peacetravel.User

class UserAdapter (
    private val userList: List<User>,
    private val context: Context
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val userName: TextView = itemView.findViewById(R.id.text_view_user_name)
        val contact: TextView = itemView.findViewById(R.id.text_view_contact)
        val fromLocation: TextView = itemView.findViewById(R.id.text_view_from_location)
        val toLocation: TextView = itemView.findViewById(R.id.text_view_to_location)
        val time: TextView = itemView.findViewById(R.id.text_view_time)
        val unreadMessages: TextView = itemView.findViewById(R.id.UnReadsCount)
        var userIdphone = getUidFromSharedPreferences() ?: ""



        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User) {
            userName.text = user.userName
            contact.text = user.phoneNumber
            fromLocation.text = user.fromLocation
            toLocation.text = user.toLocation
            time.text = user.time

            // Disable click listener
//            itemView.isClickable = false
//            itemView.isFocusable = false

            // Set unread messages count
            val cardNotif = itemView.findViewById<CardView>(R.id.cardNotif)

            // Set unread messages count
            if (user.unreadMessagesCount > 0) {
                unreadMessages.visibility = View.VISIBLE
                unreadMessages.text = user.unreadMessagesCount.toString()
                cardNotif.visibility = View.VISIBLE
            } else {
                unreadMessages.visibility = View.GONE
                cardNotif.visibility = View.GONE
            }
        }



        override fun onClick(v: View?) {
            if (isCheckVariableTrue()) {
                val user = userList[adapterPosition]
                val chatRoomId = generateChatRoomId(user.id, userIdphone)
                fetchUserNameAndStartChatActivity(user.id, chatRoomId)
            } else {
                Snackbar.make(v ?: return, "Please enter your data first!", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        private fun fetchUserNameAndStartChatActivity(userId: String, chatRoomId: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("newUsers").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
                    val userName = "$firstName $lastName"

                    if (userName.isNotBlank()) {
                        Log.d("UserAdapter", "Fetched userName=$userName for userId=$userId")

                        val intent = Intent(context, ChatActivity::class.java).apply {
                            putExtra("CHAT_ROOM_ID", chatRoomId)
                            putExtra("USER_NAME", userName)
                        }
                        context.startActivity(intent)
                    } else {
                        Log.e("UserAdapter", "Failed to fetch valid userName for userId=$userId")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle potential errors here
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user , parent, false)
        return UserViewHolder(itemView)
    }

    private fun isCheckVariableTrue(): Boolean {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val f = sharedPref.getString("check_variable","false")
        Log.e("MMMMMMMMMM","$f")
        return sharedPref.getString("check_variable", "false") == "true"
    }
    private fun getUidFromSharedPreferences(): String? {
        val sharedPref = context?.getSharedPreferences("MyPrefs", -Context.MODE_PRIVATE)
        return sharedPref?.getString("UID", null)
    }

    private fun generateChatRoomId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            userId1 + userId2
        } else {
            userId2 + userId1
        }
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size
}



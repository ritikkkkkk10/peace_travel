package com.ritikprajapati.peacetravel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isSent) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sentMessageTextView: TextView = itemView.findViewById(R.id.text_view_sent_message)
        private val readTickImageView: ImageView = itemView.findViewById(R.id.readTick)

        fun bind(message: Message) {
            sentMessageTextView.text = message.content
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            if (message.senderUid == currentUserUid && message.SeenByMe) {
                readTickImageView.setImageResource(R.drawable.tick_icon) // Set your tick icon here
                readTickImageView.visibility = View.VISIBLE
            } else {
                readTickImageView.visibility = View.GONE
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receivedMessageTextView: TextView = itemView.findViewById(R.id.text_view_received_message)

        fun bind(message: Message) {
            receivedMessageTextView.text = message.content
        }
    }


}
package com.example.kt_messenger

import User
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textView_latestMessage.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latestmessage.text = chatPartnerUser?.username
                val targetImageView = viewHolder.itemView.image_view_latest_message
                Picasso.get().load(chatPartnerUser?.profielImageUrl).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("User", "Database error: $p0")
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}
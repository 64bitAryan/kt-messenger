package com.example.kt_messenger

import User
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity: AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
    }
        val adapter = GroupAdapter<GroupieViewHolder>()
        var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        //val userName = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        listenForMessages()

        /* on Send Button click Listener */
        send_button_chat_log.setOnClickListener {
            /* Sending text to firebase */
            performToSendMessage()
            edittext_chat_log.setText("")
        }
    }

    private fun  listenForMessages()
    {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage!=null) {
                    if(chatMessage.fromId==FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage?.text, currentUser!!))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage?.text, toUser!!))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun performToSendMessage()
    {
        /* Send Messages to Firebase */
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if(fromId == null) return

//        val refrence = FirebaseDatabase.getInstance().getReference("/messages").push()
        val refrence = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toRefrence = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessageActivity = ChatMessage(refrence.key!!, text,fromId, toId,System.currentTimeMillis() / 1000)
        refrence.setValue(chatMessageActivity).addOnSuccessListener {
            Log.d(TAG,"Saved our Message: ${refrence.key}")
            edittext_chat_log.text.clear()
            recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
        }
        toRefrence.setValue(chatMessageActivity)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessageActivity)

        val latestMessageRef2 = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageRef2.setValue(chatMessageActivity)
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        //Loading the image
        val uri = user.profielImageUrl
        val tergetImageView = viewHolder.itemView.imageview_from_row
        Picasso.get().load(uri).into(tergetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        //Loader User Image
        val uri = user.profielImageUrl
        val tergetImageView = viewHolder.itemView.imageview_to_row
        Picasso.get().load(uri).into(tergetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}
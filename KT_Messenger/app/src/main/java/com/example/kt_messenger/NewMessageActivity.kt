package com.example.kt_messenger

import User
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row.view.*

class NewMessageActivity: AppCompatActivity (){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers()
    {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        /* Adding Listener */
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            /*  Data snapshot instance contain data from a firebase Database location.
                Any time you read Database data you receive the data as a dataSnapshot  */
            override fun onDataChange(p0: DataSnapshot) {
                /*  Setting adapter
                    To provide custom class and view holder
                    To exclude tedious work of setting up this adapter use groupie library */
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    Log.d("MainActivity",it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null)
                        adapter.add(UserItem(user))
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recycler_view_newmessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"An error occurred Fetching your data",Toast.LENGTH_SHORT).show()
                Log.d("DataBase error",error.toString())
            }

        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>(){
    /* Bind your data hear */
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // will be called in our list for each user object later on......
        viewHolder.itemView.usernametextvire_newmessage.text = user.username

        Picasso.get().load(user.profielImageUrl).into((viewHolder.itemView.imageView_newmessage))
    }
    /* Use Layout */
    override fun getLayout(): Int {
        return R.layout.user_row
    }

}


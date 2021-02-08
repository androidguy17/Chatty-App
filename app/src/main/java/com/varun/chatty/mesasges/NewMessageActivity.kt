package com.varun.chatty.mesasges

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.varun.chatty.R
import com.varun.chatty.model.Users
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*


import kotlinx.android.synthetic.main.users_ticket.*
import kotlinx.android.synthetic.main.users_ticket.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title="Select User"

        val adapter = GroupAdapter<GroupieViewHolder>()

        recyclerView_newMessage.adapter= adapter

//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())

        fetchUsers()
    }

    fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()


               p0.children.forEach(){
                   Log.d("NewMessage",it.toString())


                   var user = it.getValue(Users::class.java)//????? why Users
                   if(user!=null) {
                       adapter.add(
                           UserItem(
                               user
                           )
                       )
                   }

               }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    var intent = Intent(applicationContext,ChatLog::class.java )
                    intent.putExtra("USER_KEY",userItem.user)
                    startActivity(intent)

                    finish()


                }

                recyclerView_newMessage.adapter=adapter

            }


        })



    }


    class UserItem(val user: Users):com.xwray.groupie.Item<GroupieViewHolder>(){


        override fun getLayout(): Int {
            return R.layout.users_ticket
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView2.text= user.Username

            Picasso.get().load(user.ProfileImageurl).into(viewHolder.itemView.imageView2)

        }


    }



}

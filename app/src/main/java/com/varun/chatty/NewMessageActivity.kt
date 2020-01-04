package com.varun.chatty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title="Select User"

        val adapter = GroupAdapter<GroupieViewHolder>()

        recyclerView_newMessage.adapter= adapter

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

    }

    class UserItem:Item(){
        override fun bind(
            viewHolder: com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder,
            position: Int
        ) {

        }

        override fun getLayout(): Int {
            return R.layout.users_ticket
        }


    }



}

package com.varun.chatty.mesasges

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.varun.chatty.R
import com.varun.chatty.model.ChatMessage
import com.varun.chatty.model.Users
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_ticket.view.*
import kotlinx.android.synthetic.main.chat_to_ticket.view.*

class ChatLog : AppCompatActivity() {

        var touser:Users?=null
    val adapter = GroupAdapter<GroupieViewHolder>()

    val TAG = "ChatLog"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter= adapter


         touser = intent.getParcelableExtra<Users>("USER_KEY")



        supportActionBar?.title = touser?.Username
       // setUpDummyData()

        listenForMessage()

        busend_chatlog.setOnClickListener {

            Log.d(TAG, "Attemting to send msg")
            PerformSendMessage()
        }


    }


    private fun listenForMessage(){

        val fromId= FirebaseAuth.getInstance().uid
        val toId = touser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-messages/$fromId/$toId/")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

               val chatMessage = p0.getValue(ChatMessage::class.java)

               if(chatMessage!=null) {
                   Log.d(TAG, chatMessage.text.toString())

                   if(chatMessage.fromId==FirebaseAuth.getInstance().uid) {   // message incoming should be shown on

                       var currentuser = LatestMessagesActivity.currentUser

                       adapter.add(ChatItemto(chatMessage.text,currentuser))
                   } else {

                       adapter.add(ChatItemfrom(chatMessage.text,touser!!))


                   }

               }


            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }


        })


    }





    fun PerformSendMessage(){


       val text =  etchatlog.text.toString()
       val fromId = FirebaseAuth.getInstance().currentUser?.uid
       val toId = touser?.uid

            if(fromId==null) return
       // val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/users-messages/$fromId/$toId").push()

        val toreference = FirebaseDatabase.getInstance().getReference("/users-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId!!,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"saved our message")
                etchatlog.text.clear()
                recyclerview_chatlog.scrollToPosition(adapter.itemCount -1 )

            }

        toreference.setValue(chatMessage)


    }




}




class ChatItemfrom(val text:String,val users:Users):Item<GroupieViewHolder>(){ // msg on left


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.etchatfromtext.text = text
        Picasso.get().load(users.ProfileImageurl).into(viewHolder.itemView.ivchatfrom)
    }

    override fun getLayout(): Int {

        return R.layout.chat_from_ticket

    }


}




class ChatItemto(val text:String,val currentuser:Users?):Item<GroupieViewHolder>(){ // msg to be shown on right


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.etchatto.text = text

        var url = currentuser?.ProfileImageurl
        Picasso.get().load(url).into(viewHolder.itemView.ivchat_to)


    }

    override fun getLayout(): Int {

        return R.layout.chat_to_ticket

    }


}


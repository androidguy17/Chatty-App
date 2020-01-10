package com.varun.chatty.mesasges

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.varun.chatty.Login.MainActivity
import com.varun.chatty.R
import com.varun.chatty.model.ChatMessage
import com.varun.chatty.model.Users
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latestmessages_resource.view.*

class LatestMessagesActivity : AppCompatActivity() {


    companion object{

        var currentUser : Users ?= null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        fetchCurrentUser()
        recyclerview_latestmessages.adapter=adapter
        //setUpDummyRows()

        //Listen for latest Messages
        listenForlatestMessages()
    }

    private fun refreshRecycler()
    {
        adapter.clear()
        latestMessagesMap.values.forEach{

            adapter.add(LatestMessageRow(it))
        }



    }
    var adapter = GroupAdapter<com.xwray.groupie.GroupieViewHolder>()

    var latestMessagesMap = HashMap<String,ChatMessage>()

    private fun listenForlatestMessages(){
    val fromId = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
    ref.addChildEventListener(object: ChildEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            val chatMessage = p0.getValue(ChatMessage::class.java) ?:return

            latestMessagesMap[p0.key!!]= chatMessage
            refreshRecycler()

        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val chatMessage = p0.getValue(ChatMessage::class.java) ?:return

            latestMessagesMap[p0.key!!]= chatMessage
            refreshRecycler()



        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }


    })


    }



    private fun setUpDummyRows(){


//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
        recyclerview_latestmessages.adapter=adapter

    }

    private fun fetchCurrentUser(){

        var uid = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
      ref.addListenerForSingleValueEvent(object : ValueEventListener{
          override fun onCancelled(p0: DatabaseError) {

          }

          override fun onDataChange(p0: DataSnapshot) {
             currentUser= p0.getValue(Users::class.java)
              Log.d("uid", currentUser?.Username.toString())
          }


      })



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.top_main_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.menu_logout ->{
                FirebaseAuth.getInstance().signOut()
                var intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }


            R.id.menu_new_msg ->{
                val intent = Intent(this, NewMessageActivity::class.java)
               // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)





            }


        }


        return super.onOptionsItemSelected(item)
    }

    class LatestMessageRow(val chatMessage: ChatMessage):Item<com.xwray.groupie.GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.latestmessages_resource
        }



        override fun bind(viewHolder: com.xwray.groupie.GroupieViewHolder, position: Int) {
            viewHolder.itemView.etLatestMessage.text = chatMessage.text
        }


    }
}

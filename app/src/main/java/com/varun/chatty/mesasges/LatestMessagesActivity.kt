package com.varun.chatty.mesasges

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varun.chatty.Login.MainActivity
import com.varun.chatty.R
import com.varun.chatty.model.Users

class LatestMessagesActivity : AppCompatActivity() {


    companion object{

        var currentUser : Users ?= null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        fetchCurrentUser()
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
}

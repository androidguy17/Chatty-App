package com.varun.chatty.Login

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.varun.chatty.mesasges.LatestMessagesActivity
import com.varun.chatty.R
import com.varun.chatty.model.Users
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private  var auth: FirebaseAuth?=null
    private var mStorageRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        etAlreadyhaveAccount.setOnClickListener{

                    val intent =Intent(this,
                        LoginActivity::class.java)
                    startActivity(intent)

        }

    }

    val REQ_CODE = 256
    val PERMISIONCODE=144
    var uri:Uri?=null
    fun buImage(view: View){


       CheckPermission()




    }


    fun getImage(){



        val intent =Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent,REQ_CODE )
    }


    override fun onStart() {

        if (auth!!.currentUser!=null){

            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        super.onStart()
    }

    fun CheckPermission(){

        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISIONCODE
                )


            }else{

                getImage()
            }

        }else{

            getImage()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){
            PERMISIONCODE -> {

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                    getImage()
                    return
                }

                else if(grantResults[0]==PackageManager.PERMISSION_DENIED)
                {
                    Toast.makeText(this,"Can't access storage",Toast.LENGTH_LONG).show()
                }


                }

            }










        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==REQ_CODE && resultCode== Activity.RESULT_OK && data!=null){
       uri=data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)


            circleImageView2.setImageBitmap(bitmap)
            //circleImageView2.alpha=0f
           buImage.setVisibility(View.GONE)
           // circleImageView2.alpha=
            // val drawable = BitmapDrawable(bitmap)
           // buImage.setText(" ")
            //buImage.setBackgroundDrawable(drawable)
          //  val baos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos)
//            val picture = baos.toByteArray()





        }


    }






    val TAG = "MainActivity"

    fun buRegister(view:View){


        val email = etemail.text.toString()
        val password = etPassword.text.toString()
        Log.d(TAG,"$email, and $password")
        Register(email,password)


    }

    fun Register(email:String,password:String){

        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   // Log.d(TAG, "createUserWithEmail:success")
                    val user = auth!!.currentUser

                    UploadImageToFirebaseStorage()
                    Toast.makeText(baseContext, "Success",
                        Toast.LENGTH_LONG).show()
                } else {
                    // If sign in fails, display a message to the user.
                   // Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }


            }


    }

    var Imageurl:String="No Image"

    fun UploadImageToFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/$filename")


        val bitmap2 = MediaStore.Images.Media.getBitmap(contentResolver,uri)

        val baos = ByteArrayOutputStream()
        bitmap2.compress(Bitmap.CompressFormat.JPEG,0,baos)
        val picture = baos.toByteArray()





        val UploadTask = ref.putBytes(picture)

        UploadTask.addOnFailureListener{

            Toast.makeText(this,"Failed to upload",Toast.LENGTH_LONG).show()


        }.addOnSuccessListener {

            Toast.makeText(this," uploaded",Toast.LENGTH_LONG).show()




        }.continueWithTask{task->

            ref.downloadUrl

        }.addOnCompleteListener{task->

             Imageurl = task.result.toString()
            Toast.makeText(this,Imageurl,Toast.LENGTH_LONG).show()
            FireBaseDatabase(Imageurl)

        }

    }


    fun FireBaseDatabase(ProfileUrl:String){


       val uid = auth!!.currentUser!!.uid
       var ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        var users =
            Users(
                uid!!,
                etusername.text.toString(),
                ProfileUrl
            )

        ref.setValue(users)
            .addOnCompleteListener{

                val intent = Intent(this,
                    LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }

    }


}

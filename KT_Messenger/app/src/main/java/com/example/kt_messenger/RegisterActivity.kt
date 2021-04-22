package com.example.kt_messenger

import User
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //register button onclick listener (lambda Expression)
        register_button_registor.setOnClickListener {
            performRegister()
        }

        // Already have an account link
        alreadyhaveaccount_textview.setOnClickListener {
            // Launch tha login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Transitions slide_in_left/right.xml
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
        }

        //Photo Selector Button
        selectPhoto_button_rigister.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            //starting activity for result
            startActivityForResult(intent, 0)
        }
    }
    var selectedPhotoUri: Uri? = null
    /* Capturing Result */
    /* Processing Intent(Image) of the selectPhoto_button_rigister */
    /* getting URI of the Image and displaying image */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null){
            //Procead and check what is the selected image was
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            //selectPhoto_button_rigister.setBackgroundDrawable(bitmapDrawable)
            select_photo_imageview_register.setBackgroundDrawable(bitmapDrawable)
            selectPhoto_button_rigister.alpha = 0f
        }
    }

    private fun performRegister()
    {
        val email = email_editext_register.text.toString()
        val password = password_editext_register.text.toString()

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this,"Please Enter your password/email",Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                // If not successful
                if(!it.isSuccessful) return@addOnCompleteListener

                // If successful
                Log.d("Main","Successfully Created user with uid: ${it.result?.user?.uid}")
                uploadImageToFirebaseStorage()
                Toast.makeText(this,"Registered Successfully :)", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
            }

    }

    /* uploading Image to FireBase */
    private fun uploadImageToFirebaseStorage() {
        /* Checking for null */
        if(selectedPhotoUri == null) return
        /* UUID for Location in database */
        val filename = UUID.randomUUID().toString()
        /* Creating refrence */
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        /* Putting Image uri in storage */
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener { it ->
                    Log.d("RegisterActivity","successfully uploaded image: ${it.metadata?.path}")

                    /* Accessing Download Location in database*/
                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        Log.d("RegisterActivity","File location: $it")
                        saveUserToFirebase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity","Image addition Failed :(")
                }
    }

    /* Saving  users username and image to database */
    private fun saveUserToFirebase(profielImageUrl: String) {
        Log.d("User","invoking saveUserToFirebase()")
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_edittext_register.text.toString(), profielImageUrl)
        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("MainActivity","Finally we save the user to database")
                    email_editext_register.setText("")
                    password_editext_register.setText("")
                    selectPhoto_button_rigister.setBackgroundResource(R.drawable.circular_button)
                    username_edittext_register.setText("")

                    val intent = Intent(this,LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("MainActivity","Unable to save user")
                }
    }
}

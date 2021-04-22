package com.example.kt_messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginbutton_login.setOnClickListener {
            val email = email_editext_login.text.toString()
            val password = password_edittext_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Log.d("Main", "signInwithEmail: successful")
                        val intent = Intent(this,LatestMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Log.d("Main", "signInwithEmail: Unsuccessful")
                        Toast.makeText(this, "Log in Fail",Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Log in Fail",Toast.LENGTH_SHORT).show()
                }
        }

        backtoregistreation_textview.setOnClickListener {


            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            // Transitions slide_in_left/right.xml
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
        }
    }

    override fun finish() {
        super.finish()
        // Transitions slide_in_left/right.xml
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
    }

}
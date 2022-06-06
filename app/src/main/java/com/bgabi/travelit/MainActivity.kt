package com.bgabi.travelit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bgabi.travelit.auth.SignInActivity
import com.bgabi.travelit.auth.SignUpActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser : FirebaseUser? = null

//    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
//        val user = firebaseAuth.currentUser?.uid
//        user?.let {
////            val intent = Intent(this, MainActivity::class.java)
////            startActivity(intent)
////            finish()
//            //this.recreate()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = Firebase.auth
        firebaseUser = firebaseAuth.currentUser

        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)

        val button_sign_in: Button = findViewById(R.id.button_sign_up)
        button_sign_in.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
        }

        val button_sign_up: Button = findViewById(R.id.button_sign_in)
        button_sign_up.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        }
    }

//    override fun onStart() {
//        super.onStart()
//        firebaseAuth!!.addAuthStateListener(this.firebaseAuthListener!!)
//    }


}
package com.bgabi.travelit

import android.content.ClipData.newIntent
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bgabi.travelit.R
import com.bgabi.travelit.auth.SignInActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
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
    }

//    override fun onStart() {
//        super.onStart()
//        firebaseAuth!!.addAuthStateListener(this.firebaseAuthListener!!)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu,menu)
        if (menu != null) {
            checkCurrentUser(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if ( id == R.id.redirect_sign_in) {
            val intent : Intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        else if (id == R.id.log_out_button) {
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut();
            val refresh = Intent(this, MainActivity::class.java)
            startActivity(refresh)
            //finish()
        }
        return super.onOptionsItemSelected(item)
    }

    public fun checkCurrentUser(menu: Menu) {
        FirebaseAuth.getInstance().currentUser?.reload()
        val user = Firebase.auth.currentUser

        if (user != null) {
            menu.getItem(0).setVisible(false)
            menu.getItem(1).setVisible(true)
        } else {
            menu.getItem(0).setVisible(true)
            menu.getItem(1).setVisible(false)
        }
    }
}
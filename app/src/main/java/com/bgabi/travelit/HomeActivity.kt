package com.bgabi.travelit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bgabi.travelit.auth.SignInActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

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
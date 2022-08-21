package com.bgabi.travelit.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bgabi.travelit.MainActivity
import com.bgabi.travelit.R
import com.bgabi.travelit.auth.SignInActivity
import com.bgabi.travelit.databinding.ActivityHomeBinding
import com.bgabi.travelit.fragments.CommentFragment
import com.bgabi.travelit.fragments.HomeFragment
import com.bgabi.travelit.fragments.NewPostFragment
import com.bgabi.travelit.fragments.ProfileFragment
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var commentFragment: CommentFragment
    private lateinit var newPostFragment: NewPostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val profileFragment = ProfileFragment()
        val homeFragment = HomeFragment()
        commentFragment = CommentFragment()
        setCurrentFragment(profileFragment)
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.home -> setCurrentFragment(homeFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        if (supportFragmentManager.backStackEntryCount >= 1) {
            for (i: Int in 1 until supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment)
                commit()
            }
        } else if (supportFragmentManager.backStackEntryCount < 1) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment)
                    .addToBackStack(null)
                commit()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        if (menu != null) {
            checkCurrentUser(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        if (id == R.id.log_out_button) {
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
            menu.getItem(0).setVisible(true)
        } else {
            menu.getItem(0).setVisible(false)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, which -> exitProcess(2) })
                .setNegativeButton("No", null)
                .show()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
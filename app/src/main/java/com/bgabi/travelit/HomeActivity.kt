package com.bgabi.travelit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bgabi.travelit.auth.SignInActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.fragment.app.Fragment
import com.bgabi.travelit.databinding.ActivityHomeBinding
import com.bgabi.travelit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val profileFragment = ProfileFragment()
        val homeFragment = HomeFragment()
        setCurrentFragment(profileFragment)
        val bottomNavigationView :BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.home -> setCurrentFragment(homeFragment)

            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
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

        if (id == R.id.redirect_sign_in) {
            val intent: Intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.log_out_button) {
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
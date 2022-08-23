package com.bgabi.travelit.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bgabi.travelit.MainActivity
import com.bgabi.travelit.R
import com.bgabi.travelit.auth.SignInActivity
import com.bgabi.travelit.databinding.ActivityHomeBinding
import com.bgabi.travelit.fragments.*
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.viewmodels.UsersViewModel
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
    private var defaultUser = UtilsObj.defaultUser
    private var currentUser: User = UtilsObj.defaultUser
    private lateinit var usersViewModel: UsersViewModel
    lateinit var mainHandler: Handler
    private val profileFragment = ProfileFragment()

    private val updateTask = object : Runnable {
        override fun run() {
            if (!checkDataLoaded()) {
                mainHandler.postDelayed(this, 1000)
            } else {
                kill()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        usersViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        getCurrentUserDetails()
        mainHandler = Handler(Looper.getMainLooper())
        val profileFragment = ProfileFragment()
        val homeFragment = HomeFragment()
        commentFragment = CommentFragment()
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
        var newFragment = Fragment()
        if (fragment.javaClass == ProfileFragment::class.java) {
            newFragment = ProfileFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (fragment.javaClass == HomeFragment::class.java) {
            newFragment = HomeFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (fragment.javaClass == FollowersFragment::class.java) {
            newFragment = FollowersFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (fragment.javaClass == FollowingFragment::class.java) {
            newFragment = FollowingFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (fragment.javaClass == EditProfileFragment::class.java) {
            newFragment = EditProfileFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (fragment.javaClass == NewPostFragment::class.java) {
            newFragment = NewPostFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (fragment.javaClass == CommentFragment::class.java) {
            newFragment = CommentFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            newFragment.arguments = mBundle
        }
        if (supportFragmentManager.backStackEntryCount >= 1) {
            for (i: Int in 1 until supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, newFragment)
                commit()
            }
        } else if (supportFragmentManager.backStackEntryCount < 1) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, newFragment)
                    .addToBackStack(null)
                commit()
            }
        }
    }


    fun kill() {
        mainHandler.removeCallbacks(updateTask)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTask)
    }

    private fun checkDataLoaded(): Boolean {
        if (currentUser != defaultUser) {
            setCurrentFragment(profileFragment)
            return true
        }
        return false
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

    private fun getCurrentUserDetails() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            usersViewModel.getCurrentUser(uid).observe(this) {
                currentUser = User(
                    it.uid,
                    it.email,
                    it.userName,
                    it.phone,
                    it.description,
                    it.followers,
                    it.following,
                    it.travelHistory,
                    it.favorites,
                    it.futureTravel,
                    it.userPosts,
                    it.isAdmin
                )
            }
        }
    }
}
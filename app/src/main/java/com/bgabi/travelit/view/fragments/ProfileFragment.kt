package com.bgabi.travelit.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bgabi.travelit.R
import com.bgabi.travelit.view.activities.HomeActivity
import com.bgabi.travelit.databinding.FragmentProfileBinding
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.TravelHistoryActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var postsFragment: LinearLayout
    private lateinit var followersFragment: LinearLayout
    private lateinit var followingFragment: LinearLayout
    private lateinit var edit_profile: Button
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private lateinit var usersList: ArrayList<User>
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var profile_pic: CircleImageView
    private val REQUEST_CODE = 100
    private var imageUri: Uri? = null
    private lateinit var emailText: TextView
    private lateinit var nameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var followers: TextView
    private lateinit var following: TextView
    private lateinit var posts: TextView
    private lateinit var descriptionLayout: LinearLayout
    var username: String = ""
    var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile, container, false
        )
        val bundle = arguments
        currentUser = bundle!!.getSerializable("mUser") as User
        usersList = bundle.getSerializable("usersList") as ArrayList<User>
        followersFragment = binding.followers
        postsFragment = binding.posts
        postsFragment.setOnClickListener {
            val fragment: Fragment = UserProfileFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("userProfile", currentUser)
            fragment.arguments = mBundle
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        followersFragment.setOnClickListener {
            val fragment: Fragment = FollowersFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("usersList", usersList)
            fragment.arguments = mBundle
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
        followingFragment = binding.following
        followingFragment.setOnClickListener {
            val fragment: Fragment = FollowingFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("usersList", usersList)
            fragment.arguments = mBundle
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
        val travelHistory = binding.travelHistory
        travelHistory.setOnClickListener {
            val intent = Intent(context, TravelHistoryActivity::class.java)
            intent.putExtra("currentUser", currentUser)
            context?.startActivity(intent)
        }
        profile_pic = binding.profilePic
        emailText = binding.emailProfile
        nameText = binding.userNameProfile
        descriptionText = binding.descriptionProfile
        followers = binding.followersNumber
        following = binding.followingNumber
        posts = binding.postsNumber
        descriptionLayout = binding.descriptionProfileLayout
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        firebaseAuth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        //if (user != null) {
        //    currentUser = user
        checkUserDetails(currentUser)
        currentUser.uid?.let { loadPhotoFromFirebase(it) }
        //}


        edit_profile = binding.editProfile
        edit_profile.setOnClickListener {
            val fragment: Fragment = EditProfileFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("usersList", usersList)
            mBundle.putSerializable("username",username)
            mBundle.putSerializable("description",description)
            fragment.arguments = mBundle
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
        return binding.root
    }

    private fun checkUserDetails(user: User) {
        if (user.email != "") {
            emailText.setText(user.email)
        }
        if (user.userName != "") {
            nameText.setText(user.userName)
        }
        if (user.description != "") {
            descriptionText.setText(user.description)
        } else {
            descriptionLayout.visibility = View.GONE
        }
        username = user.userName.toString()
        description = user.description.toString()
        val s1 = user.followers.size.toString()
        val s2 = user.following.size.toString()
        val s3 = user.userPosts.size.toString()
        followers.setText(s1)
        following.setText(s2)
        posts.setText(s3)
    }

    private fun loadPhotoFromFirebase(uid: String) {
        val profilePhotosRef = storage.reference.child("profile_images/${uid}")
        profilePhotosRef.downloadUrl.addOnSuccessListener { it ->
            context?.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(profile_pic)
            }
        }.addOnFailureListener { it ->
            context?.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(profile_pic)
            }
        }
    }
}
package com.bgabi.travelit.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var followersFragment: LinearLayout
    private lateinit var followingFragment: LinearLayout
    private lateinit var edit_profile: Button
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
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

        followersFragment = binding.followers
        followersFragment.setOnClickListener {
            val fragment: Fragment = FollowersFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
        followingFragment = binding.following
        followingFragment.setOnClickListener {
            val fragment: Fragment = FollowingFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }

        profile_pic = binding.profilePic
        emailText = binding.emailProfile
        nameText = binding.userNameProfile
        descriptionText = binding.descriptionProfile
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        firebaseAuth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        val bundle = arguments
        currentUser = bundle!!.getSerializable("mUser") as User
        //if (user != null) {
        //    currentUser = user
        checkUserDetails(currentUser)
        currentUser.uid?.let { loadPhotoFromFirebase(it) }
        //}

        edit_profile =binding.editProfile
        edit_profile.setOnClickListener {
            val fragment: Fragment = EditProfileFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
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
        }
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
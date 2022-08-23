package com.bgabi.travelit.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bgabi.travelit.R
import com.bgabi.travelit.activities.HomeActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentProfileBinding>(
            inflater,
            R.layout.fragment_profile, container, false
        )
        val edit_profile: Button = binding.editProfile
        edit_profile.setOnClickListener {
            val fragment: Fragment = EditProfileFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
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
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        firebaseAuth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        val bundle = arguments
        currentUser = bundle!!.getSerializable("mUser") as User
        //if (user != null) {
        //    currentUser = user
        checkUserDetails(currentUser)
        //}


        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
    private fun checkUserDetails(user: User) {
        if (user.email != "") {
            emailText.setText(user.email)
        }
        if (user.userName != "") {
            nameText.setText(user.userName)
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
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profiile_images%2Fuser.png?alt=media&token=7f916443-a0b2-467d-b740-cd2f0835bfb1")
                    .into(profile_pic)
            }
        }
    }
}
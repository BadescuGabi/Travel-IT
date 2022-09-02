package com.bgabi.travelit.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentHomeBinding
import com.bgabi.travelit.databinding.FragmentProfileBinding
import com.bgabi.travelit.databinding.FragmentUserProfileBinding
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.HomeActivity
import com.bgabi.travelit.view.adapter.PostAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private lateinit var userProfile: User
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private val REQUEST_CODE = 100
    private var imageUri: Uri? = null
    private lateinit var emailText: TextView
    private lateinit var nameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var followers: TextView
    private lateinit var following: TextView
    private lateinit var userProfilePic: CircleImageView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        userProfile = bundle!!.getSerializable("userProfile") as User
        currentUser = bundle!!.getSerializable("mUser") as User
        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        storage = Firebase.storage
        binding.userNameProfileOther.setText(userProfile.userName)
        binding.emailProfileOther.setText(userProfile.email)
        binding.descriptionProfileOther.setText(userProfile.description)
        userProfilePic = binding.profilePicOther
        userProfile.uid?.let { loadPhotoFromFirebase(it) }
        recyclerView = binding.feedRecyclerviewOther
        recyclerView.layoutManager =  LinearLayoutManager(context)
        var userList = ArrayList<User>()
        userList.add(userProfile)
        recyclerView.adapter = PostAdapter(userProfile.userPosts,userList)
                // Inflate the layout for this fragment
            return binding.root
    }
    private fun loadPhotoFromFirebase(uid: String) {
        val profilePhotosRef = storage.reference.child("profile_images/${uid}")
        profilePhotosRef.downloadUrl.addOnSuccessListener { it ->
            context?.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(userProfilePic)
            }
        }.addOnFailureListener { it ->
            context?.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(userProfilePic)
            }
        }
    }

}
package com.bgabi.travelit.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bgabi.travelit.R
import com.bgabi.travelit.activities.HomeActivity
import com.bgabi.travelit.databinding.FragmentEditProfileBinding
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
import java.io.ByteArrayOutputStream


class EditProfileFragment() : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var profilePicEdit: CircleImageView
    private lateinit var profilePic: CircleImageView
    private val REQUEST_CODE = 1000
    private var imageUri: Uri? = null
    private lateinit var userNameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        profilePic = binding.editProfilePic
        profilePicEdit = binding.addProfilePic
        userNameText = binding.etUpdateUsername
        descriptionText = binding.etUpdateDescription
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        firebaseAuth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        val bundle = arguments
        if (container != null) {
            mContext = container.context
        }
        currentUser = bundle!!.getSerializable("mUser") as User
        //if (user != null) {
        //    currentUser = user
        checkUserDetails(currentUser)
        currentUser.uid?.let { loadPhotoFromFirebase(it) }
        profilePicEdit.setOnClickListener {
            openGalleryForImage()
        }
        val uploadButton = binding.buttonUpdateProfile
        uploadButton.setOnClickListener() {
            val userName = userNameText.text.toString().trim { it <= ' ' }
            val description = descriptionText.text.toString().trim { it <= ' ' }

            when {
                TextUtils.isEmpty(userName) -> {
                    Toast.makeText(context, "Please enter your new username.", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    firebaseUser = firebaseAuth.currentUser
                    firebaseUser?.let { it1 -> saveDataToFirebase(userName, description, it1.uid) }
                    Toast.makeText(context, "Data saved.", Toast.LENGTH_SHORT).show()
                    currentUser = bundle!!.getSerializable("mUser") as User
                    //if (user != null) {
                    //    currentUser = user
                    currentUser.userName = userName
                    currentUser.description = description
                    checkUserDetails(currentUser)
                    val fragment: Fragment = ProfileFragment()
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val mBundle = Bundle()
                    mBundle.putSerializable("mUser", currentUser)
                    fragment.arguments = mBundle
                    val fragmentTransaction: FragmentTransaction =
                        fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.flFragment, fragment)
                    fragmentTransaction.commit()
                }
            }
        }
        return binding.root
    }


    private fun openGalleryForImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, 1000)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data?.data
            profilePic.setImageURI(imageUri)
            savePhotoToFirebase(imageUri)
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
    }

    fun savePhotoToFirebase(imageUri: Uri?) {
        val userUid = firebaseAuth.currentUser?.uid
        //val imagesRef = storageRef.child("profile_photos/${userUid}-${imageUri}")
        val imagesRef = storage.reference.child("profile_images/${userUid}")

        profilePic.isDrawingCacheEnabled = true
        profilePic.buildDrawingCache()
        val bitmap = (profilePic.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
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
    }

//    private fun refreshFragment(fragment: Fragment) {
//        fragmentManager?.beginTransaction()?.
//        detach(fragment)?.
//        attach(fragment)?.
//        commit()
//    }

    private fun saveDataToFirebase(
        userName: String,
        description: String,
        uid: String
    ) {
        database.child(uid).child("userName").setValue(userName)
        database.child(uid).child("description").setValue(description)
    }

    private fun checkUserDetails(user: User) {
        if (user.userName != "") {
            userNameText.setText(user.userName)
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
                    .into(profilePic)
            }
        }.addOnFailureListener { it ->
            context?.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(profilePic)
            }
        }
    }
}
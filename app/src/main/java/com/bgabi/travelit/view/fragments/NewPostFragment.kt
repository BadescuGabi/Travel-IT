package com.bgabi.travelit.view.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentNewPostBinding
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.HomeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private lateinit var postDescrition: TextView
    private lateinit var postLocation: TextView
    private lateinit var postImage: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null
    private val REQUEST_CODE = 1000
    private lateinit var postId: String
    private lateinit var postDate: String
    private lateinit var usersList: ArrayList<User>
    private var pstLocation = ""
    private var pstDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val bundle = arguments
        currentUser = bundle!!.getSerializable("mUser") as User
        val photoId = bundle!!.getSerializable("photoId") as String
        usersList = bundle.getSerializable("usersList") as ArrayList<User>
        pstDescription = bundle!!.getSerializable("postDescription") as String
        pstLocation = bundle!!.getSerializable("postLocation") as String
        usersList.add(currentUser)
        binding = FragmentNewPostBinding.inflate(layoutInflater)
        postDescrition = binding.postDescription
        postLocation = binding.postLocation
        postDescrition.setText(pstDescription)
        postLocation.setText(pstLocation)
        postImage = binding.postPhoto
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        storage = Firebase.storage
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        postDate = current.format(formatter)
        if (photoId != "") {
            postId = photoId
        } else {
            postId = "${currentUser.userName}+${postDate}"
        }
        loadPhotoFromFirebase(postId)
        postImage.setOnClickListener {
            openGalleryForImage()
        }
        val addPostButton = binding.postButton1
        addPostButton.setOnClickListener {
            savePostToFirebase(
                currentUser,
                postId,
                currentUser.uid.toString(),
                postLocation.text.toString(),
                postDate,
                postDescrition.text.toString()
            )
//            val fragment: Fragment = ProfileFragment()
//            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
//            val mBundle = Bundle()
//            mBundle.putSerializable("mUser", currentUser)
//            mBundle.putSerializable("usersList",usersList)
//            fragment.arguments = mBundle
//            val fragmentTransaction: FragmentTransaction =
//                fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.flFragment, fragment)
//            fragmentTransaction.commit()
//        }
            val intent = Intent(context, HomeActivity::class.java)
            Toast.makeText(context, "Post added", Toast.LENGTH_SHORT).show()
            context?.startActivity(intent)
        }
        return binding.root
    }


    private fun savePostToFirebase(
        currentUsr: User,
        postId: String,
        postUser: String,
        postLocation: String,
        postDate: String,
        postDescription: String,
    ) {

        val newPost =
            Post(postId, postUser, postLocation, postDate, postDescription)
        currentUsr.userPosts.add(newPost)
        currentUsr.uid?.let { database.child(it).child("userPosts").setValue(currentUsr.userPosts) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data?.data
            postImage.setImageURI(imageUri)
            pstDescription = postDescrition.text.toString()
            pstLocation = postLocation.text.toString()
            savePhotoToFirebase(imageUri, postId)
            val fragment: Fragment = NewPostFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("photoId", postId)
            mBundle.putSerializable("usersList",usersList)
            mBundle.putSerializable("postDescription",pstDescription)
            mBundle.putSerializable("postLocation",pstLocation)
            fragment.arguments = mBundle
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, 1000)

    }

    private fun loadPhotoFromFirebase(postId: String) {
        val postPhotsRef = storage.reference.child("post_images/${postId}")
        postPhotsRef.downloadUrl.addOnSuccessListener { it ->
            context?.let { con ->
                Glide.with(con)
                    .load(it)
                    .override(600, 200)
                    .into(postImage)
            }
        }
    }

    private fun savePhotoToFirebase(imageUri: Uri?, photoUid: String) {
        //val imagesRef = storageRef.child("profile_photos/${userUid}-${imageUri}")
        val imagesRef = storage.reference.child("post_images/${photoUid}")

        postImage.isDrawingCacheEnabled = true
        postImage.buildDrawingCache()
        pstDescription = postDescrition.text.toString()
        pstLocation = postLocation.text.toString()
        val bitmap = (postImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener {
            val fragment: Fragment = NewPostFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("photoId", photoUid)
            mBundle.putSerializable("usersList",usersList)
            mBundle.putSerializable("postDescription",pstDescription)
            mBundle.putSerializable("postLocation",pstLocation)
            fragment.arguments = mBundle
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flFragment, fragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}
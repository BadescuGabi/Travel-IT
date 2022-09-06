package com.bgabi.travelit.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.models.User
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class LikedUsersAdapter(
    private val uids: ArrayList<String>,
    private val usersList: ArrayList<User>,
    private val currentUser: User
) :
    RecyclerView.Adapter<LikedUsersAdapter.ViewHolder>() {
    private lateinit var mContext: Context
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("data")
    private val userRef: DatabaseReference = rootRef.child("users")
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_user_liked_design, parent, false)
        mContext = parent.context
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userUid = uids[position]
        val user = usersList.first { it.uid == userUid }
        // sets the image to the imageview from our itemHolder class
        // sets the text to the textview from our itemHolder class
        holder.textView.setText(user.userName)
        storage = Firebase.storage
        // setting data to each view of recyclerview item.
        val profilePhotoRef = storage.reference.child("profile_images/${userUid}")
        profilePhotoRef.downloadUrl.addOnSuccessListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(holder.imageView)
            }
        }.addOnFailureListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(holder.imageView)
            }
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return uids.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_user_liked)
        val textView: TextView = itemView.findViewById(R.id.textView_like)
    }
}
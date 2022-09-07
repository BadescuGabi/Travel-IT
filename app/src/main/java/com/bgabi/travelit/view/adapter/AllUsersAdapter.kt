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
import com.bgabi.travelit.models.Comment
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AllUsersAdapter(
    private val uids: ArrayList<String>,
    private val usersList: ArrayList<User>,
    private val currentUser: User
) :
    RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {
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
            .inflate(R.layout.card_view_all_users, parent, false)
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

        holder.removeButton.setOnClickListener() {
            uids.remove(userUid)
            deleteUserFromFirebase(user)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, uids.size)
            usersList.remove(user)
            usersList.forEach {
                if (it.following.contains(userUid)) {
                    it.following.remove(userUid)
                    it.uid?.let { it3 -> saveUserFollowingToFirebase(it3, it.following) }
                }
                if (it.followers.contains(userUid)) {
                    it.followers.remove(userUid)
                    it.uid?.let { it4 -> saveUserFollowersToFirebase(it4, it.following) }
                }
                it.userPosts.forEach { it1 ->
                    if (it1.postLikes.contains(userUid)) {
                        it1.postLikes.remove(userUid)
                        updateLikeToFirebase(it1, it)
                    }
                }
            }
            usersList.forEach {
                var notifForDelete = ArrayList<String>()
                var newNotif = ArrayList<String>()
                it.notifications.forEach{notif->
                    if(user.userName?.let { it5 -> notif.contains(it5) } == true){
                        notifForDelete.add(notif)
                    }
                }
                newNotif.addAll(it.notifications)
                newNotif.removeAll(notifForDelete)
                it.uid?.let { it1 -> saveNotificationToFirebase(it1,newNotif) }
                it.userPosts.forEach { it1 ->
                    var comms = ArrayList<Comment>()
                    comms.addAll(it1.comments.filterNotNull())

                    it1.comments.forEach { it2 ->
                        if (it2.commentUser == userUid) {
//                            it1.comments.remove(it2)
                                comms.remove(it2)
                        }
                    }
                    updateCommentsFirebase(it.uid, comms, it, it1)
                }
            }
        }
    }

    private fun deleteUserFromFirebase(usr: User) {
        usr.uid?.let { database.child(it).removeValue() }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return uids.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_all_users)
        val textView: TextView = itemView.findViewById(R.id.username_all_users)
        val removeButton: Button = itemView.findViewById(R.id.delete_user_button)
    }

    private fun getCurrentUserDetails(uid: String): User {
        var user = FirebaseHelper.defaultUser
        userRef.child(uid).get().addOnSuccessListener {
            user = it.getValue(User::class.java)!!
        }
        return user
    }

    private fun saveNotificationToFirebase(
        uid: String,
        notification: ArrayList<String>
    ) {
        database.child(uid).child("notifications").setValue(notification)
    }

    private fun saveUserFollowersToFirebase(currentUid: String, followers: ArrayList<String>) {
        database.child(currentUid).child("followers").setValue(followers)

    }


    private fun saveUserFollowingToFirebase(currentUid: String, following: ArrayList<String>) {
        database.child(currentUid).child("following").setValue(following)
    }

    private fun updateLikeToFirebase(posts: Post, author: User) {
        val ind = author.userPosts.indexOf(posts)
        if (posts != null) {
            posts.postUser.let {
                if (it != null) {
                    database.child(it).child("userPosts").child(ind.toString()).setValue(posts)
                }
            }
        }
    }

    private fun updateCommentsFirebase(
        postUser: String?,
        com: ArrayList<Comment>,
        pUser: User,
        currentPost: Post

    ) {
        val ind = pUser.userPosts.indexOfFirst {
            it.postId == currentPost.postId
        }
        if (postUser != null) {
            database.child(postUser).child("userPosts").child(ind.toString()).child("comments")
                .setValue(com)
        }
    }
}


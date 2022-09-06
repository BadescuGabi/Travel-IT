package com.bgabi.travelit.view.adapter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.fragments.LikeUsersFragment
import com.bgabi.travelit.view.fragments.PostForNotificationFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlin.collections.ArrayList

class NotificationAdapter(
    private var mList: ArrayList<String>,
    private var usersList: ArrayList<User>,
    private var currentUser: User
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private lateinit var mContext: Context
    private lateinit var storage: FirebaseStorage
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_notification_design, parent, false)
        mContext = parent.context
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        storage = Firebase.storage
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification = mList[position]
        holder.notificationText.text = notification
        usersList.forEach() {
            if (it.userName?.let { it1 -> notification.contains(it1) } == true) {
                val imageRef = storage.reference.child("profile_images/${it.uid}")
                imageRef.downloadUrl.addOnSuccessListener { it2 ->
                    mContext.let { con ->
                        Glide.with(con)
                            .load(it2)
                            .into(holder.userImage)
                    }
                }.addOnFailureListener { it2 ->
                    mContext.let { con ->
                        Glide.with(con)
                            .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                            .into(holder.userImage)
                    }
                }
            }

        }
        var id = ""
        if (notification.contains("post")) {
            id = notification.substringAfter("post ")
        }
        if (id != "") {
            var redirectPost: ArrayList<Post> = ArrayList()
            currentUser.userPosts.forEach {
                if (it.postId == id) {
                    redirectPost.add(it)
                }
            }
            holder.notificationRedirect.setOnClickListener {
                val fragment: Fragment = PostForNotificationFragment()
                val mBundle = Bundle()
                mBundle.putSerializable("mUser", currentUser)
                mBundle.putSerializable("usersList", usersList)
                mBundle.putSerializable("userPost", redirectPost)
                fragment.arguments = mBundle
                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        holder.deleteButton.setOnClickListener {
            currentUser.notifications.remove(notification)
//            mList.remove(notification)
            updateNotificationsFirebase(position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var notificationText: TextView = itemView.findViewById(R.id.textView_notification)
        var userImage: ImageView = itemView.findViewById(R.id.image_notification)
        var notificationRedirect: LinearLayout =
            itemView.findViewById(R.id.notification_post_redirect)
        var deleteButton: ImageView = itemView.findViewById(R.id.delete_notification)
    }

    private fun updateNotificationsFirebase(pos:Int) {
        currentUser.uid?.let {
            database.child(it).child("notifications").setValue(currentUser.notifications)
        }
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, mList.size)
    }
}
package com.bgabi.travelit.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.models.Comment
import com.bgabi.travelit.models.User
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class CommentAdapter(private val mList: ArrayList<Comment>, private val usersList :ArrayList<User>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private lateinit var mContext: Context
    private lateinit var storage: FirebaseStorage

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_view, parent, false)
        mContext = parent.context
        storage = Firebase.storage
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val comment = mList[position]
        var user = usersList.firstOrNull(){it.uid == comment.commentUser}
        // sets the text to the textview from our itemHolder class
        if (user != null) {
            holder.userName.setText(user.userName ?: "")
        }
        holder.userComment.setText(comment.comment)
        holder.commentDate.setText(comment.commentDate)
        val profilePhotoRef = storage.reference.child("profile_images/${user?.uid}")
        profilePhotoRef.downloadUrl.addOnSuccessListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(holder.profileImage)
            }
        }.addOnFailureListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(holder.profileImage)
            }
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.comment_profile_image)
        val userName: TextView = itemView.findViewById(R.id.comment_username)
        val userComment: TextView = itemView.findViewById(R.id.comment)
        val commentDate: TextView = itemView.findViewById(R.id.comment_time_posted)
    }

}
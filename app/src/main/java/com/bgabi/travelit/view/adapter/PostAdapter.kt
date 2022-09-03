package com.bgabi.travelit.view.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.view.fragments.CommentFragment
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class PostAdapter(
    private var postList: ArrayList<Post>,
    private var usersList: ArrayList<User>,
    private var currentUser: User
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    // arraylist for our facebook feeds.
    private lateinit var mContext: Context
    private lateinit var storage: FirebaseStorage
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout for item of recycler view item.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        mContext = parent.context
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // getting data from array list and setting it to our modal class.
        val post: Post = postList[position]
        storage = Firebase.storage

        // setting data to each view of recyclerview item.
        val profilePhotoRef = storage.reference.child("profile_images/${post.postUser}")
        profilePhotoRef.downloadUrl.addOnSuccessListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(holder.postUserProfileImage)
            }
        }.addOnFailureListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(holder.postUserProfileImage)
            }
        }

        val postPhotoRef = storage.reference.child("post_images/${post.postId}")
        postPhotoRef.downloadUrl.addOnSuccessListener { it ->
            holder.loading.visibility = View.GONE
            mContext.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(holder.postImage)
            }
        }

        val user = usersList.first { it.uid == post.postUser }
        holder.postUserName.setText(user.userName)
        holder.postDate.setText(post.postDate)
        holder.postLocation.setText(post.postLocation)
        holder.postDescirption.setText(post.postDescription)

        val commentButton: LinearLayout = holder.commentButton
        commentButton.setOnClickListener {
            val fragment: Fragment = CommentFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("userPost", post)
            mBundle.putSerializable("usersPost", postList)
            fragment.arguments = mBundle
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        holder.reportButton.setOnClickListener {
            val admin = usersList.first { it.admin == "true" }
            admin.notifications.add("${currentUser.userName} reported a post ") //${post.postId}
            admin.uid?.let { it1 -> savaNotificationToFirebase(it1,admin.notifications) }
        }
    }

    override fun getItemCount(): Int {
        // returning the size of our array list.
        return postList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our views
        // of recycler view items.
        val postUserProfileImage: CircleImageView
        val postUserName: TextView
        val postDate: TextView
        val postLocation: TextView
        val postDescirption: TextView
        val postImage: ImageView
        val likeButton: LinearLayout
        val commentButton: LinearLayout
        val reportButton: LinearLayout
        val likeNumber: TextView
        val commentNumber: TextView
        val loading: ProgressBar

        init {
            // initializing our variables
            postUserProfileImage = itemView.findViewById(R.id.post_user_image)
            postUserName = itemView.findViewById(R.id.post_username)
            postDate = itemView.findViewById(R.id.post_user_date)
            postLocation = itemView.findViewById(R.id.post_user_location)
            postDescirption = itemView.findViewById(R.id.post_content)
            postImage = itemView.findViewById(R.id.post_image)
            likeButton = itemView.findViewById(R.id.like_button)
            commentButton = itemView.findViewById(R.id.commentButton)
            reportButton = itemView.findViewById(R.id.reportButton)
            likeNumber = itemView.findViewById(R.id.like_number)
            commentNumber = itemView.findViewById(R.id.comment_number)
            loading = itemView.findViewById(R.id.idLoadingPB)
        }
    }

    private fun savaNotificationToFirebase(userUid: String, notifications: ArrayList<String>) {
        database.child(userUid).child("notifications").setValue(notifications)
    }
    // creating a constructor for our adapter class.
//    init {
//        this.postList = postList
//        postFilterList = postList
//    }


//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                postFilterList = results?.values as ArrayList<Post>
//                notifyDataSetChanged()
//            }
}



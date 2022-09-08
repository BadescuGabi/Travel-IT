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
import com.bgabi.travelit.view.fragments.PostForNotificationFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList

class ReportAdapter(
    private var mList: ArrayList<String>,
    private var usersList: ArrayList<User>,
    private var currentUser: User
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {
    private lateinit var mContext: Context
    private lateinit var storage: FirebaseStorage
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    var redirectPost: ArrayList<Post> = ArrayList()
    private lateinit var reportedUser: User

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.car_view_report_design, parent, false)
        mContext = parent.context
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        storage = Firebase.storage
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val report = mList[position]
        var copyRap = ""
        if (report.contains("post")) {
            copyRap = report
            copyRap=copyRap.replaceAfter("post","")
            holder.reportText.text = copyRap
        } else {
            holder.reportText.text = report
        }
        usersList.forEach() {
            if (it.userName?.let { it1 -> report.contains(it1) } == true) {
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
        holder.itemView.setOnClickListener {
            Toast.makeText(mContext, "Notification clicked", Toast.LENGTH_SHORT).show()
        }
        var id = ""
        if (report.contains("post")) {
            id = report.substringAfter("post ")
        }

        usersList.forEach {
        it.userPosts.forEach {it1->
            if (it1.postId == id) {
                redirectPost.add(it1)
                reportedUser = it
            }

        }
            holder.postRedirect.setOnClickListener {
                if(redirectPost.size ==0){
                    Toast.makeText(mContext, "This post was deleted", Toast.LENGTH_SHORT).show()
                }
                else {
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
            holder.reject.setOnClickListener {
                currentUser.notifications.remove(report)
//            mList.remove(notification)
                updateReportsFirebase(position)
            }
            holder.accept.setOnClickListener {
                currentUser.notifications.remove(report)
                updateReportsFirebase(position)
                reportedUser.userPosts.remove(redirectPost[0])
                updatePostsFirebase(reportedUser.uid)
            }
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var reportText: TextView = itemView.findViewById(R.id.textView_report)
        var userImage : ImageView = itemView.findViewById(R.id.image_report)
        var accept: ImageView = itemView.findViewById(R.id.accept_report)
        var reject: ImageView = itemView.findViewById(R.id.reject_report)
        var postRedirect: LinearLayout = itemView.findViewById(R.id.report_post_redirect)
    }
    private fun updateReportsFirebase(pos:Int) {
        currentUser.uid?.let {
            database.child(it).child("notifications").setValue(currentUser.notifications)
        }
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, mList.size)
    }
    private fun updatePostsFirebase(postUser: String?) {
        if (postUser != null) {
            reportedUser.uid?.let {
                database.child(it).child("userPosts").setValue(reportedUser.userPosts)
            }
        }
    }
}
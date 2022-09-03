package com.bgabi.travelit.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.models.User
import com.bgabi.travelit.viewmodels.UsersViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.car_view_report_design, parent, false)
        mContext = parent.context
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val itemsViewModel = mList[position]
        storage = Firebase.storage
        holder.reportText.setText(itemsViewModel)
        usersList.forEach(){
            if (it.userName?.let { it1 -> itemsViewModel.contains(it1) } == true) {
                val imageRef = storage.reference.child("profile_images/${it.uid}")
                imageRef.downloadUrl.addOnSuccessListener { it2 ->
                    mContext.let { con ->
                        Glide.with(con)
                            .load(it2)
                            .into(holder.userImage)
                    }
                }.
                addOnFailureListener { it2 ->
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
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var reportText: TextView = itemView.findViewById(R.id.textView_report)
        var userImage : ImageView = itemView.findViewById(R.id.image_report)
        var accept: ImageView = itemView.findViewById(R.id.accept_report)
        var reject: ImageView = itemView.findViewById(R.id.reject_report)
    }
}
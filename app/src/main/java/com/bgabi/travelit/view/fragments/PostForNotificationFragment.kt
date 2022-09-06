package com.bgabi.travelit.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentPostForNotificationBinding
import com.bgabi.travelit.databinding.FragmentUserProfileBinding
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.Post
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


class PostForNotificationFragment : Fragment() {

    private lateinit var binding: FragmentPostForNotificationBinding
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private lateinit var storage: FirebaseStorage
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersList: ArrayList<User>
    private lateinit var postList: ArrayList<Post> //just one
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        currentUser = bundle!!.getSerializable("mUser") as User
        usersList = bundle.getSerializable("usersList") as ArrayList<User>
        postList = bundle.getSerializable("userPost") as ArrayList<Post>
        binding = FragmentPostForNotificationBinding.inflate(layoutInflater)
        storage = Firebase.storage
        recyclerView = binding.notificationPostRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
//        val feedPosts = getPostsForAdapter(currentUser.uid)
//        val postSorted = currentUser.userPosts.sortedWith(compareByDescending { it.postDate })
        recyclerView.adapter = PostAdapter(postList, usersList, currentUser)
        // Inflate the layout for this fragment
        return binding.root
    }
}
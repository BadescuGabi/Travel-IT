package com.bgabi.travelit.view.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentCommentBinding
import com.bgabi.travelit.helpers.FirebaseHelper
import com.bgabi.travelit.models.Comment
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.adapter.CommentAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentFragment : Fragment(R.layout.fragment_comment) {
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: FragmentCommentBinding
    private lateinit var currentUser: User
    private lateinit var currentPost: Post
    private lateinit var recyclerView: RecyclerView
    private lateinit var newComment: TextView
    private lateinit var commentButton: ImageView
    private lateinit var commentDate: String
    private lateinit var database: DatabaseReference
    private lateinit var postsList: ArrayList<Post>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        currentPost = bundle!!.getSerializable("userPost") as Post
        currentUser = bundle!!.getSerializable("mUser") as User
        postsList = bundle!!.getSerializable("usersPost") as ArrayList<Post>
        binding = FragmentCommentBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance(FirebaseHelper.dbUrl).getReference("data/users")
        storage = Firebase.storage
        recyclerView = binding.recyclerviewComents
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CommentAdapter(currentPost.comments)
        newComment = binding.comment
        commentButton = binding.addComment
        commentButton.setOnClickListener {
            if (newComment.text != "") {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                commentDate = current.format(formatter)
                val newCom = Comment(currentUser, newComment.text.toString(),commentDate)
                currentPost.comments.add(newCom)
                addComment(currentPost,currentUser,currentPost.comments,postsList)
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addComment(post: Post, user: User, comment: List<Comment>,posts: ArrayList<Post>) {
            val ind=posts.indexOf(post)
            user.uid?.let { post.postUser?.let { it1 -> database.child(it1).child("userPosts").child(ind.toString()).child("comments").setValue(post.comments) } }

    }

}

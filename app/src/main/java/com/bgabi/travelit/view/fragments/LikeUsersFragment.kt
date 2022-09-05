package com.bgabi.travelit.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentFollowingBinding
import com.bgabi.travelit.databinding.FragmentLikeUsersBinding
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.HomeActivity
import com.bgabi.travelit.view.adapter.FollowingAdapter
import com.bgabi.travelit.view.adapter.LikedUsersAdapter


class LikeUsersFragment : Fragment() {


    private lateinit var binding: FragmentLikeUsersBinding
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private lateinit var usersList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        currentUser = bundle!!.getSerializable("mUser") as User
        usersList = bundle!!.getSerializable("usersList") as ArrayList<User>
        binding = FragmentLikeUsersBinding.inflate(layoutInflater)

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerviewLike

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)

        // This will pass the ArrayList to our Adapter
//        val adapter = FollowingAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = LikedUsersAdapter(currentUser.following,usersList,currentUser)

        return binding.root
    }
}
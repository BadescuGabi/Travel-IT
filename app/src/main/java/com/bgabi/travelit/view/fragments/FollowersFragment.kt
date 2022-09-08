package com.bgabi.travelit.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bgabi.travelit.R
import com.bgabi.travelit.viewmodels.UserCardVIewModel
import com.bgabi.travelit.view.adapter.FollowersAdapter
import com.bgabi.travelit.databinding.FragmentFollowersBinding
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.HomeActivity


class FollowersFragment : Fragment(R.layout.fragment_followers) {

    private lateinit var binding: FragmentFollowersBinding
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
        binding = FragmentFollowersBinding.inflate(layoutInflater)

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerviewFollowers

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)


        // This will pass the ArrayList to our Adapter
        val adapter = FollowersAdapter(currentUser.followers,usersList,currentUser)
        recyclerview.adapter = adapter
        // Setting the Adapter with the recyclerview
//


        return binding.root
    }
}
package com.bgabi.travelit.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentAllUsersBinding
import com.bgabi.travelit.databinding.FragmentFollowingBinding
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.HomeActivity
import com.bgabi.travelit.view.adapter.AllUsersAdapter
import com.bgabi.travelit.view.adapter.FollowingAdapter


class AllUsersFragment : Fragment() {
    private lateinit var binding: FragmentAllUsersBinding
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
        binding = FragmentAllUsersBinding.inflate(layoutInflater)
        var uidList = ArrayList<String>()
        usersList.forEach {
            it.uid?.let { it1 -> uidList.add(it1) }
        }
        // getting the recyclerview by its id
        val recyclerview = binding.recyclerviewAllUsers

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)

        // This will pass the ArrayList to our Adapter
//        val adapter = FollowingAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = AllUsersAdapter(uidList,usersList,currentUser)

        return binding.root
    }

}
package com.bgabi.travelit.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.databinding.FragmentNotificationBinding
import com.bgabi.travelit.helpers.UtilsObj
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.activities.HomeActivity
import com.bgabi.travelit.view.adapter.NotificationAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    lateinit var homeActivity: HomeActivity
    private var currentUser: User = UtilsObj.defaultUser
    private var firebaseUser: FirebaseUser? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var profile_pic: CircleImageView
    private val REQUEST_CODE = 100
    private var imageUri: Uri? = null
    private lateinit var notifRecyclerView: RecyclerView
    private lateinit var usersList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments


        usersList = bundle!!.getSerializable("usersList") as ArrayList<User>
        currentUser = bundle!!.getSerializable("mUser") as User
        binding = FragmentNotificationBinding.inflate(layoutInflater)
        notifRecyclerView = binding.recyclerviewNotifications
        notifRecyclerView.layoutManager = LinearLayoutManager(context)

        notifRecyclerView.adapter = NotificationAdapter(currentUser.notifications,usersList,currentUser
        )
        return binding.root
    }


}
package com.bgabi.travelit.view.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
//import com.bgabi.travelit.view.adapter.PostAdapter
import com.bgabi.travelit.view.adapter.UserSearchAdapter
import com.bgabi.travelit.databinding.FeedRvItemBinding
import com.bgabi.travelit.databinding.FragmentHomeBinding
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bgabi.travelit.view.adapter.PostAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding2: FeedRvItemBinding
    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
    private lateinit var progressBar: ProgressBar
    private lateinit var postAdapter: PostAdapter
    private lateinit var usersSearchAdapter: UserSearchAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("data")
    private val userRef: DatabaseReference = rootRef.child("users")
    private var usersList: ArrayList<User> = ArrayList<User>()
    private lateinit var newPostButton: LinearLayout
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val v = inflater.inflate(R.layout.feed_rv_item, container, false)

        val bundle = arguments
        usersList = bundle!!.getSerializable("usersList") as ArrayList<User>
        currentUser = bundle!!.getSerializable("mUser") as User
        usersList=ArrayList(usersList.filter { it.admin!="true" })
        usersList.remove(currentUser)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding2 = FeedRvItemBinding.inflate(layoutInflater)
        newPostButton = binding.newPostButton

        newPostButton.setOnClickListener {
            val fragment: Fragment = NewPostFragment()
            val mBundle = Bundle()
            mBundle.putSerializable("mUser", currentUser)
            mBundle.putSerializable("photoId", "")
            mBundle.putSerializable("usersList", usersList)
            fragment.arguments = mBundle
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        // getting the recyclerview by its id
        val feedPosts = getPostsForAdapter(currentUser.following)
        val postSorted = ArrayList(feedPosts.sortedWith(compareByDescending { it.postDate }))
        recyclerview = binding.feedRecyclerview

        recyclerView2 = binding.peopleRecyclerview
        recyclerView2.visibility = View.GONE
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerView2.layoutManager = LinearLayoutManager(context)
        usersSearchAdapter = UserSearchAdapter(usersList, currentUser)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = PostAdapter(feedPosts,usersList,currentUser)
        recyclerView2.adapter = usersSearchAdapter


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search)

        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                recyclerView2.visibility = View.VISIBLE
                newPostButton.visibility = View.GONE
                recyclerview.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                recyclerview.visibility = View.VISIBLE
                newPostButton.visibility = View.VISIBLE
                recyclerView2.visibility = View.GONE
                return true
            }
        })

        queryTextListener = object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("onQueryTextChange", newText)
                usersSearchAdapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i("onQueryTextSubmit", query)
                return false
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun getPostsForAdapter(followingUids: ArrayList<String>): ArrayList<Post> {
        var posts = ArrayList<Post>()
        usersList.forEach() { usr ->
            followingUids.forEach() { uid ->
                if (usr.uid == uid.toString()) {
                    usr.userPosts.forEach(){ post ->
                        posts.add(post)
                    }
                }
            }
        }

        if (posts.isNotEmpty()) {
            val postSorted = posts.sortedWith(compareByDescending { it.postDate })
            return ArrayList(postSorted)
        }
            return posts
    }
}
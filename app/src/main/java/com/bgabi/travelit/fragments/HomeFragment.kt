package com.bgabi.travelit.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bgabi.travelit.R
import com.bgabi.travelit.adapter.PostAdapter
import com.bgabi.travelit.adapter.UserSearchAdapter
import com.bgabi.travelit.databinding.FeedRvItemBinding
import com.bgabi.travelit.databinding.FragmentHomeBinding
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.bgabi.travelit.viewmodels.UsersViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding2: FeedRvItemBinding
    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var searchManager: SearchManager
    private lateinit var instaModalArrayList: ArrayList<Post>
    private lateinit var facebookFeedModalArrayList: ArrayList<Post>
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
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var newPostButton: LinearLayout

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

        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding2 = FeedRvItemBinding.inflate(layoutInflater)
        progressBar = binding.idLoadingPB
        newPostButton = binding.newPostButton
        newPostButton.setOnClickListener {
            val fragment: Fragment = NewPostFragment()
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        // getting the recyclerview by its id
        recyclerview = binding.feedRecyclerview

        recyclerView2 = binding.peopleRecyclerview
        recyclerView2.visibility = View.GONE
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerView2.layoutManager = LinearLayoutManager(context)
        usersSearchAdapter = UserSearchAdapter(usersList)
        // Setting the Adapter with the recyclerview
        recyclerView2.adapter = usersSearchAdapter

        //usersAdapter = UserAdapter(getUsers())
        getPostsFeed()

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

    private fun getPostsFeed() {
        facebookFeedModalArrayList = ArrayList()

        // below line is use to initialize the variable for our request queue.
        mRequestQueue = Volley.newRequestQueue(context)

        // below line is use to clear
        // cache this will be use when
        // our data is being updated.
        mRequestQueue.cache.clear()

        // below is the url stored in
        // string for our sample data
        val url = "https://jsonkeeper.com/b/OB3B"
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    progressBar.visibility = View.GONE
                    try {
                        // in below line we are extracting the data from json object.
                        val authorName = response.getString("authorName")
                        val authorImage = response.getString("authorImage")

                        // below line is to get json array from our json object.
                        val feedsArray = response.getJSONArray("feeds")

                        // running a for loop to add data to our array list
                        for (i in 0 until feedsArray.length()) {
                            // getting json object of our json array.
                            val feedsObj = feedsArray.getJSONObject(i)

                            // extracting data from our json object.
                            val postDate = feedsObj.getString("postDate")
                            val postDescription = feedsObj.getString("postDescription")
                            val postIV = feedsObj.getString("postIV")
                            val postLikes = feedsObj.getString("postLikes")
                            val postComments = feedsObj.getString("postComments")

                            // adding data to our modal class.
                            val feedModal = Post(
                                authorImage,
                                authorName,
                                postDate,
                                postDescription,
                                postIV,
                                postLikes,
                                postComments
                            )
                            facebookFeedModalArrayList.add(feedModal)

                            // below line we are creating an postAdapter class and adding our array list in it.
                            postAdapter = PostAdapter(facebookFeedModalArrayList)
                            recyclerview = binding.feedRecyclerview

                            // below line is for setting linear layout manager to our recycler view.
                            val linearLayoutManager =
                                LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                            // below line is to set layout
                            // manager to our recycler view.
                            recyclerview.layoutManager = linearLayoutManager

                            // below line is to set postAdapter
                            // to our recycler view.
                            recyclerview.adapter = postAdapter
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(
                            context,
                            "Fail to get data with error $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        mRequestQueue.add(jsonObjectRequest)
    }

//
//    suspend fun getUsers(): DbResponse {
//        val response = DbResponse()
//        try {
//            response.users = userRef.get().await().children.map { snapShot ->
//                snapShot.getValue(User::class.java)!!
//            }
//        } catch (exception: Exception) {
//            response.exception = exception
//        }
//        return response
//    }
}
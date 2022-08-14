package com.bgabi.travelit.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
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
import com.bgabi.travelit.databinding.FragmentHomeBinding
import com.bgabi.travelit.models.Post
import org.json.JSONException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var searchManager: SearchManager
    private lateinit var instaModalArrayList: ArrayList<Post>
    private lateinit var facebookFeedModalArrayList: ArrayList<Post>
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        progressBar = binding.idLoadingPB

        // getting the recyclerview by its id
        recyclerview = binding.feedRecyclerview

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)

        // ArrayList of class ItemsViewModel
        //val data = ArrayList<PostViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..2) {
//            data.add(PostViewModel("a", "b ","c","d","e","f","g"))
//        }

        // This will pass the ArrayList to our Adapter
        //adapter = PostAdapter(data)

        // Setting the Adapter with the recyclerview
        //recyclerview.adapter = adapter
        getPostsFeed()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("onQueryTextChange", newText)
                adapter.filter.filter(newText)
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

                            // below line we are creating an adapter class and adding our array list in it.
                            adapter = PostAdapter(facebookFeedModalArrayList)
                            recyclerview = binding.feedRecyclerview

                            // below line is for setting linear layout manager to our recycler view.
                            val linearLayoutManager =
                                LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                            // below line is to set layout
                            // manager to our recycler view.
                            recyclerview.layoutManager = linearLayoutManager

                            // below line is to set adapter
                            // to our recycler view.
                            recyclerview.adapter = adapter
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
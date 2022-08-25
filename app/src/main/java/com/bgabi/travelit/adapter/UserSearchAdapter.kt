package com.bgabi.travelit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.models.User
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class UserSearchAdapter(private var usersList: ArrayList<User>): RecyclerView.Adapter<UserSearchAdapter.ViewHolder>(),
    Filterable {
    // arraylist for our facebook feeds.
    var usersFilterList = ArrayList<User>()
    private lateinit var mContext: Context
    private lateinit var storage: FirebaseStorage

    init {
        usersFilterList = usersList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout for item of recycler view item.
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_user_search_design, parent, false)
        mContext = parent.context

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // getting data from array list and setting it to our modal class.
        val modal: User = usersFilterList[position]
        val uid = modal.uid
        storage = Firebase.storage
        // setting data to each view of recyclerview item.
        val profilePhotosRef = storage.reference.child("profile_images/${uid}")
        profilePhotosRef.downloadUrl.addOnSuccessListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load(it)
                    .into(holder.image)
            }
        }.
        addOnFailureListener { it ->
            mContext.let { con ->
                Glide.with(con)
                    .load("https://firebasestorage.googleapis.com/v0/b/travel-it-d162e.appspot.com/o/profile_images%2Fuser.png?alt=media&token=1569df88-2e93-41d1-baa8-73d747c77c83")
                    .into(holder.image)
            }
        }
        holder.userName.setText(modal.userName)
    }

    override fun getItemCount(): Int {
        // returning the size of our array list.
        return usersFilterList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our views
        // of recycler view items.
        val image: CircleImageView
        val userName: TextView
        val addButton: Button

        init {
            // initializing our variables
            userName = itemView.findViewById(R.id.textView_search)
            image = itemView.findViewById(R.id.image_search)
            addButton = itemView.findViewById(R.id.follow__button_search_view)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    usersFilterList = usersList
                } else {
                    val resultList = ArrayList<User>()
                    for (user in usersList) {
                        if (user.userName!!.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(user)
                        }
                    }
                    usersFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = usersFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                usersFilterList = results?.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }
}
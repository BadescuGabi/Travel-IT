package com.bgabi.travelit.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bgabi.travelit.R
import com.bgabi.travelit.view.fragments.CommentFragment
import com.bgabi.travelit.models.Post
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class PostAdapter(private var postList: ArrayList<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>(), Filterable {
    // arraylist for our facebook feeds.
    var postFilterList = ArrayList<Post>()
    private lateinit var mContext: Context

    init {
        postFilterList = postList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout for item of recycler view item.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        mContext = parent.context

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // getting data from array list and setting it to our modal class.
        val modal: Post = postFilterList[position]

        // setting data to each view of recyclerview item.
        Picasso.get().load(modal.authorImage).placeholder(R.drawable.me).into(holder.authorIV)
        holder.authorNameTV.setText(modal.authorName)
        holder.timeTV.setText(modal.postDate)
        holder.descTV.setText(modal.postDescription)
        Picasso.get().load(modal.postIV).placeholder(R.drawable.me).into(holder.postIV)
        holder.likesTV.setText(modal.postLikes)
        holder.commentsTV.setText(modal.postComments)

        val commentButton: LinearLayout = holder.button
        commentButton.setOnClickListener {
            val fragment: Fragment = CommentFragment()
            val activity=it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment,fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        // returning the size of our array list.
        return postFilterList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our views
        // of recycler view items.
        val authorIV: CircleImageView
        val authorNameTV: TextView
        val timeTV: TextView
        val descTV: TextView
        val postIV: ImageView
        val likesTV: TextView
        val commentsTV: TextView
        val shareLL: LinearLayout
        val button: LinearLayout

        init {
            // initializing our variables
            shareLL = itemView.findViewById(R.id.idLLShare)
            authorIV = itemView.findViewById(R.id.idCVAuthor)
            authorNameTV = itemView.findViewById(R.id.idTVAuthorName)
            timeTV = itemView.findViewById(R.id.idTVTime)
            descTV = itemView.findViewById(R.id.idTVDescription)
            postIV = itemView.findViewById(R.id.idIVPost)
            likesTV = itemView.findViewById(R.id.idTVLikes)
            commentsTV = itemView.findViewById(R.id.idTVComments)
            button = itemView.findViewById(R.id.commentButton)
        }
    }

    // creating a constructor for our adapter class.
//    init {
//        this.postList = postList
//        postFilterList = postList
//    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    postFilterList = postList
                } else {
                    val resultList = ArrayList<Post>()
                    for (post in postList) {
                        if (post.postDescription!!.lowercase(Locale.ROOT)
                            .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(post)
                        }
                    }
                    postFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = postFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                postFilterList = results?.values as ArrayList<Post>
                notifyDataSetChanged()
            }
        }
    }
}



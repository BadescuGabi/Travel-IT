package com.bgabi.travelit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class PostAdapter(
    postList: ArrayList<PostViewModel>,
) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    // arraylist for our facebook feeds.
    private lateinit var postList: ArrayList<PostViewModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout for item of recycler view item.
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // getting data from array list and setting it to our modal class.
        val modal: PostViewModel = postList[position]

        // setting data to each view of recyclerview item.
        Picasso.get().load(modal.authorImage).placeholder(R.drawable.me).into(holder.authorIV)
        holder.authorNameTV.setText(modal.authorName)
        holder.timeTV.setText(modal.postDate)
        holder.descTV.setText(modal.postDescription)
        Picasso.get().load(modal.postIV).placeholder(R.drawable.me).into(holder.postIV)
        holder.likesTV.setText(modal.postLikes)
        holder.commentsTV.setText(modal.postComments)
    }

    override fun getItemCount(): Int {
        // returning the size of our array list.
        return postList.size
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
        }
    }

    // creating a constructor for our adapter class.
    init {
        this.postList = postList
    }
}

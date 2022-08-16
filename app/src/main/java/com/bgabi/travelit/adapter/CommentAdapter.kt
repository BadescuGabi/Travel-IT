//package com.bgabi.travelit.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bgabi.travelit.R
//import com.bgabi.travelit.UserCardVIewModel
//import com.bgabi.travelit.models.Comment
//
//class CommentAdapter(private val mList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
//
//    // create new views
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        // inflates the card_view_design view
//        // that is used to hold list item
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.comment_view, parent, false)
//
//        return ViewHolder(view)
//    }
//
//    // binds the list items to a view
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val comment = mList[position]
//
//        // sets the image to the imageview from our itemHolder class
//        holder.profileImage.setImageResource()
//
//        // sets the text to the textview from our itemHolder class
//        holder.textView.text = UserCardVIewModel.name
//
//    }
//
//    // return the number of the items in the list
//    override fun getItemCount(): Int {
//        return mList.size
//    }
//
//    // Holds the views for adding it to image and text
//    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        val profileImage: ImageView = itemView.findViewById(R.id.comment_profile_image)
//        val userName: TextView = itemView.findViewById(R.id.comment_username)
//        val userComment: TextView = itemView.findViewById(R.id.comment)
//    }
//}
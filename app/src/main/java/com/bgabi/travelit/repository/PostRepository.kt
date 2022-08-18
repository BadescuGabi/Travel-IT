package com.bgabi.travelit.repository

import androidx.lifecycle.MutableLiveData
import com.bgabi.travelit.models.DbResponse
import com.bgabi.travelit.models.Post
import com.bgabi.travelit.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class PostRepository(private val rootRef: DatabaseReference = FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app//").getReference("data"),
                     private val postRef: DatabaseReference = rootRef.child("Posts")) {

    suspend fun getResponseFromDbCoroutine(): DbResponse {
        val response = DbResponse()
        try {
            response.posts = postRef.get().await().children.map { snapShot ->
                snapShot.getValue(Post::class.java)!!
            }
        } catch (exception: Exception) {
            response.exception = exception
        }
        return response
    }
}
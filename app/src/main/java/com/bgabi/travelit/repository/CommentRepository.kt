package com.bgabi.travelit.repository

import androidx.lifecycle.MutableLiveData
import com.bgabi.travelit.models.Comment
import com.bgabi.travelit.models.DbResponse
import com.bgabi.travelit.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CommentRepository(private val rootRef: DatabaseReference = FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app/").getReference("data"),
                     private val commentRef: DatabaseReference = rootRef.child("Comments")) {

    fun getResponseLiveData() : MutableLiveData<DbResponse> {
        val mutableLiveData = MutableLiveData<DbResponse>()
        commentRef.get().addOnCompleteListener { task ->
            val response = DbResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.comments = result.children.map { snapShot ->
                        snapShot.getValue(Comment::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
    }

    suspend fun getResponseFromDbCoroutine(): DbResponse {
        val response = DbResponse()
        try {
            response.comments = commentRef.get().await().children.map { snapShot ->
                snapShot.getValue(Comment::class.java)!!
            }
        } catch (exception: Exception) {
            response.exception = exception
        }
        return response
    }
}
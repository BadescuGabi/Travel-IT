package com.bgabi.travelit.repository

import androidx.lifecycle.MutableLiveData
import com.bgabi.travelit.models.DbResponse
import com.bgabi.travelit.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository(private val rootRef: DatabaseReference = FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app//").getReference("data"),
                             private val userRef: DatabaseReference = rootRef.child("Users")) {

    fun getResponseLiveData() : MutableLiveData<DbResponse> {
        val mutableLiveData = MutableLiveData<DbResponse>()
        userRef.get().addOnCompleteListener { task ->
            val response = DbResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.users = result.children.map { snapShot ->
                        snapShot.getValue(User::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
    }

    suspend fun getResponse(): DbResponse {
        val response = DbResponse()
        try {
            response.users = userRef.get().await().children.map { snapShot ->
                snapShot.getValue(User::class.java)!!
            }
        } catch (exception: Exception) {
            response.exception = exception
        }
        return response
    }
}
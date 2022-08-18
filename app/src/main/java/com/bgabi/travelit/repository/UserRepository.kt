package com.bgabi.travelit.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bgabi.travelit.models.DbResponse
import com.bgabi.travelit.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app//")
        .getReference("data"),
    private val userRef: DatabaseReference = rootRef.child("Users")
) {

    suspend fun getResponseFromDbCoroutine(): DbResponse {
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

    suspend fun getCurrentUser(uid: String): User {
        var user: User = User("default", "", "", "", null, null, null)
        try {
            val data = userRef.get().await().child(uid)
            user = data.getValue(User::class.java)!!

        } catch (exception: Exception) {
            exception.message?.let { Log.e(TAG, it) }
        }
        return user
    }
}
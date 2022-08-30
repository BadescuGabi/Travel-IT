package com.bgabi.travelit.helpers

import androidx.lifecycle.LifecycleOwner
import com.bgabi.travelit.models.User
import com.bgabi.travelit.viewmodels.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    private lateinit var database: DatabaseReference
    val defaultUser = User("default", "", "", "", ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList(),"")
    public final var dbUrl: String = "https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app/"

    fun addUserToFirebase(uid: String, email: String = "", userName: String = "") {
        database = FirebaseDatabase.getInstance(dbUrl).getReference("data/users")
        database.child(uid).setValue(defaultUser)
    }


}
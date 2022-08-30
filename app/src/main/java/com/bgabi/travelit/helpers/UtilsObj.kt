package com.bgabi.travelit.helpers

import android.content.ContentValues
import android.location.Location
import android.os.Build
import android.util.Log
import com.bgabi.travelit.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object UtilsObj {
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance("https://travel-it-d162e-default-rtdb.europe-west1.firebasedatabase.app/").getReference("data")
    val userRef: DatabaseReference = rootRef.child("users")
    private val postRef: DatabaseReference = rootRef.child("posts")
    private val commentRef: DatabaseReference = rootRef.child("comments")
    public var defaultUser: User = User("default", "", "", "", ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList(),"")
    fun parseStringForJson(str: String): String {
        val builder = StringBuilder("{")
        val str1 = if (str.substring(0, 4) == "User") str.drop(5).dropLast(1) else str.drop(1).dropLast(1)
        val listValues: List<String> = str1.split(",").map { it -> it.trim() }
        listValues.forEach { it ->
            val keyValue: List<String> = it.split("=").map { x -> x.trim() }
            builder.append("\"").append(keyValue[0] + "\"=\"" + keyValue[1] + "\",")
        }
        builder.deleteCharAt(builder.length-1)
        builder.append("}")
        return builder.toString()
    }

    fun userFromJsonString(str: String): User {
        return Gson().fromJson(str, User::class.java)
    }

    fun parseNotificationsString(all: String): ArrayList<String> {
        return ArrayList(all.split(";"))
    }
}
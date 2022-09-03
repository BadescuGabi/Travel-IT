package com.bgabi.travelit.models

import java.io.Serializable

data class Comment (
    val commentUser: User? = null,
    val comment: String? = null,
    val commentDate: String? = null):Serializable{
}
package com.bgabi.travelit.models

data class Comment (
    val commentUser: User? = null,
    val comment: String? = null,
    val commentDate: String? = null){
}
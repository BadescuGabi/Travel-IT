package com.bgabi.travelit.models

data class Comment (
    val commentPost: Post? = null,
    val commentUser: User? = null,
    val comment: String? = null){

}
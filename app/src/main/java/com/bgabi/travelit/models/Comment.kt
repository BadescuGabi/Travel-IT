package com.bgabi.travelit.models

data class Comment (
    val post: Post? = null,
    val author: User? = null,
    val comm: String? = null){

}
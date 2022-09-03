package com.bgabi.travelit.models

import java.io.Serializable

class Notification(
    val user: User? = null,
    val message: String? = null
):Serializable{
}
package com.bgabi.travelit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bgabi.travelit.models.User
import com.bgabi.travelit.repository.CommentRepository
import com.bgabi.travelit.repository.UserRepository
import kotlinx.coroutines.Dispatchers

class CommentViewModel (private val repository: CommentRepository = CommentRepository()): ViewModel() {
    val responseLiveData = liveData(Dispatchers.IO) {
        emit(repository.getResponseFromDbCoroutine())
    }
}
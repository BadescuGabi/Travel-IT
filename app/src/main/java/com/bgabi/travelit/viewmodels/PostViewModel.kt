package com.bgabi.travelit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bgabi.travelit.repository.PostRepository
import com.bgabi.travelit.repository.UserRepository
import kotlinx.coroutines.Dispatchers

class PostViewModel(private val repository: PostRepository = PostRepository()) : ViewModel() {
    val responseLiveData = liveData(Dispatchers.IO) {
        emit(repository.getResponseFromDbCoroutine())
    }
}

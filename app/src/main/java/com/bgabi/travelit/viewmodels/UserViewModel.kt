package com.bgabi.travelit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bgabi.travelit.models.User
import com.bgabi.travelit.repository.UserRepository
import kotlinx.coroutines.Dispatchers

class UsersViewModel (private val repository: UserRepository = UserRepository()): ViewModel() {
    val responseLiveData = liveData(Dispatchers.IO) {
        emit(repository.getResponseFromDbCoroutine())
    }
    fun getCurrentUser(uid: String): LiveData<User> {
        val data = liveData(Dispatchers.IO) {
            emit(repository.getCurrentUser(uid))
        }
        return data
    }
}
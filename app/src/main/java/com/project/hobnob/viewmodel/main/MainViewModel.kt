package com.project.hobnob.viewmodel.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.hobnob.model.Message
import com.project.hobnob.model.Page
import com.project.hobnob.model.ResponseData
import com.project.hobnob.model.UserSchema
import com.project.hobnob.network.KtorService
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val service: KtorService) :
    AndroidViewModel(application) {
    val getRoomMessagesRes: MutableLiveData<List<Message>> = MutableLiveData()

    fun getRoomMessages(page: Page) {
        viewModelScope.launch {
            val messages: List<Message>? = service.getRoomMessages(page)
            getRoomMessagesRes.value = messages
        }
    }
}
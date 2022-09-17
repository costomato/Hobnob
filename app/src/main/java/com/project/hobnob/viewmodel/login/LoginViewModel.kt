package com.project.hobnob.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.hobnob.model.ResponseData
import com.project.hobnob.model.UserSchema
import com.project.hobnob.network.KtorService
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val service: KtorService) :
    AndroidViewModel(application) {
    val createUserResponse: MutableLiveData<ResponseData> = MutableLiveData()
    val authenticateResponse: MutableLiveData<ResponseData> = MutableLiveData()
    val updateResponse: MutableLiveData<ResponseData> = MutableLiveData()
    val resetPasswordResponse: MutableLiveData<ResponseData> = MutableLiveData()
    val deleteUserResponse: MutableLiveData<ResponseData> = MutableLiveData()
    val sendOtpResponse: MutableLiveData<ResponseData> = MutableLiveData()
    val verifyOtpResponse: MutableLiveData<ResponseData> = MutableLiveData()

    fun createUser(userSchema: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.createUser(userSchema)
            createUserResponse.value = responseData
        }
    }

    fun authenticateUser(userSchema: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.authenticateUser(userSchema)
            authenticateResponse.value = responseData
        }
    }

    fun updateUser(newUser: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.updateUser(newUser)
            updateResponse.value = responseData
        }
    }

    fun sendResetLink(email: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.sendResetLink(email)
            resetPasswordResponse.value = responseData
        }
    }

    fun deleteUser(userSchema: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.deleteUser(userSchema)
            deleteUserResponse.value = responseData
        }
    }

    fun sendOtp(userSchema: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.sendOtp(userSchema)
            sendOtpResponse.value = responseData
        }
    }

    fun verifyOtp(userSchema: UserSchema) {
        viewModelScope.launch {
            val responseData: ResponseData? = service.verifyOtp(userSchema)
            verifyOtpResponse.value = responseData
        }
    }
}
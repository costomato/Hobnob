package com.project.hobnob.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.hobnob.network.KtorService
import com.project.hobnob.viewmodel.login.LoginViewModel

class ViewModelFactory(
    private val application: Application,
    private val service: KtorService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(application, service) as T
    }
}
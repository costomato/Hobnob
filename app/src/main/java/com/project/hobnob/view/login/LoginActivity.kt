package com.project.hobnob.view.login

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.project.hobnob.R
import com.project.hobnob.network.KtorService
import com.project.hobnob.viewmodel.ViewModelFactory
import com.project.hobnob.viewmodel.login.LoginViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setStatusBarGradient()
        setContentView(R.layout.activity_login)

        val viewModelFactory = ViewModelFactory(application, KtorService.create())
        loginViewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    private fun setStatusBarGradient() {
        val drawable = AppCompatResources.getDrawable(this, R.drawable.bg_gradient_primary)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.almostTransparent)
        window.setBackgroundDrawable(drawable)
    }
}
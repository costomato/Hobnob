package com.project.hobnob.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.project.hobnob.databinding.ActivitySplashBinding
import com.project.hobnob.utils.Constants
import com.project.hobnob.view.login.LoginActivity
import com.project.hobnob.view.main.MainActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        val sharedPreferences =
            getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("modeNight", false))
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)


        Constants.API_KEY = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName, PackageManager.GET_META_DATA
        ).metaData["apiKey"].toString()

        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPreferences.getBoolean(Constants.IS_VERIFIED, false))
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, 400)
    }
}

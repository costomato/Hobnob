package com.project.hobnob.view.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.project.hobnob.R
import com.project.hobnob.adapter.ChatRecyclerAdapter
import com.project.hobnob.databinding.ActivityMainBinding
import com.project.hobnob.dialogs.ProgressDialog
import com.project.hobnob.model.Message
import com.project.hobnob.model.Page
import com.project.hobnob.model.UserSchema
import com.project.hobnob.network.KtorService
import com.project.hobnob.network.NetworkChecker
import com.project.hobnob.utils.Constants
import com.project.hobnob.view.login.LoginActivity
import com.project.hobnob.viewmodel.ViewModelFactory
import com.project.hobnob.viewmodel.login.LoginViewModel
import com.project.hobnob.viewmodel.main.MainViewModel
import com.project.hobnob.viewmodel.main.MainViewModelFactory
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class MainActivity : AppCompatActivity(), ChatRecyclerAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefEditor: SharedPreferences.Editor

    private lateinit var socket: Socket
    private lateinit var messageListener: Emitter.Listener
    private lateinit var recyclerMsg: RecyclerView
    private lateinit var messageList: MutableList<Message>
    private lateinit var msgAdapter: ChatRecyclerAdapter
    private lateinit var senderEmail: String
    private lateinit var senderName: String
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkChecker: NetworkChecker

    interface LoadingProgress {
        fun onLoading()
        fun onComplete()
    }

    private val loadingProgress: LoadingProgress = object : LoadingProgress {
        override fun onLoading() {
            if (networkChecker.hasNetwork()) {
                msgAdapter.setData(emptyList())
                binding.msgProgress.visibility = View.VISIBLE
                mainViewModel.getRoomMessages(Page(1, 20))
            } else {
                Snackbar.make(binding.root, "Couldn't connect to network", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onComplete() {
            binding.msgProgress.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarGradient()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =
            getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()

        val modeNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO
        binding.uiMode.setImageResource(if (modeNight) R.drawable.ic_baseline_dark_mode_24 else R.drawable.ic_light_mode_black_24dp)

        binding.uiMode.setOnClickListener {
            if (modeNight) {
                prefEditor.putBoolean("modeNight", true)
                prefEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                prefEditor.putBoolean("modeNight", false)
                prefEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

//      Basic works
        var name = sharedPreferences.getString(Constants.USER_NAME, "null")
        if (name?.indexOf(" ") != -1)
            name =
                name?.substring(
                    0,
                    name.lastIndexOf(" ") + 1
                ) + name?.substring(name.lastIndexOf(" "))
                    ?.get(1)
                    .toString().uppercase() + "."
        binding.tvUsername.text = name

        val progressDialog = ProgressDialog(this@MainActivity)
        binding.tvLogOut.setOnClickListener {
            logout()
        }

        val viewModelFactory = ViewModelFactory(application, KtorService.create())
        val loginViewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        var observeIntended = false
        loginViewModel.deleteUserResponse.observe(this, { response ->
            if (observeIntended) {
                if (response?.statusOk == true) {
                    Toast.makeText(
                        this@MainActivity,
                        response.statusString,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    logout()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        response?.statusString ?: "Some error occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                progressDialog.dismiss()
                observeIntended = false
            }
        })
        binding.tvDeleteAc.setOnClickListener {
            progressDialog.show()
            loginViewModel.deleteUser(
                UserSchema(
                    _id = sharedPreferences.getString(Constants.USER_ID, null) ?: "null12345678",
                    email = sharedPreferences.getString(Constants.USER_EMAIL, "nullEmail"),
                    password = sharedPreferences.getString(Constants.USER_PASS, "null")
                )
            )
            observeIntended = true
        }

        binding.ivMenu.setOnClickListener {
            binding.root.openDrawer(GravityCompat.END)
        }
        networkChecker = NetworkChecker(this)
//      up to here

//        Chat implementation

        val mainVmFactory = MainViewModelFactory(application, KtorService.create())
        mainViewModel =
            ViewModelProvider(this, mainVmFactory).get(MainViewModel::class.java)
        recyclerMsg = binding.recyclerMsg
        msgAdapter = ChatRecyclerAdapter(
            this,
            sharedPreferences.getString(Constants.USER_EMAIL, "nullEmail") ?: "null"
        )
        messageList = mutableListOf()
        recyclerMsg.adapter = msgAdapter

        senderEmail = sharedPreferences.getString(Constants.USER_EMAIL, "nullEmail") ?: "null"
        senderName = sharedPreferences.getString(Constants.USER_NAME, "nullName") ?: "nul"
        mainViewModel.getRoomMessagesRes.observe(this, {
            if (it != null) {
                messageList = it.toMutableList()
                msgAdapter.setData(messageList)
//                msgAdapter.notifyItemInserted(0)
                loadingProgress.onComplete()
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            networkListener()
        else
            loadingProgress.onLoading()


        binding.retry.setOnClickListener {
            loadingProgress.onLoading()
        }
        setChatImpl()
//        up to here
    }

    private fun setStatusBarGradient() {
        val drawable = AppCompatResources.getDrawable(this, R.drawable.bg_gradient_secondary)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.almostTransparent)
        window.setBackgroundDrawable(drawable)
    }

    private fun logout() {
        prefEditor.remove(Constants.USER_NAME)
        prefEditor.remove(Constants.USER_EMAIL)
        prefEditor.remove(Constants.USER_ID)
        prefEditor.remove(Constants.ABOUT_VERIFIED)
        prefEditor.remove(Constants.USER_PASS)
        prefEditor.remove(Constants.IS_VERIFIED)
        prefEditor.apply()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    private fun setChatImpl() {
        try {
            socket = IO.socket(Constants.SOCKET_URL)
            Log.e("Connection", "Connected to socket")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val gson = Gson()
        binding.ivSend.setOnClickListener {
            val msg = binding.etMessage.text.trim().toString()
            if (msg.isNotEmpty()) {
                binding.etMessage.setText("")
                socket.emit(
                    "room-message",
                    gson.toJson(Message(sender = senderEmail, senderName = senderName, body = msg))
                )
            }
        }
        messageListener = Emitter.Listener { args ->
            this@MainActivity.runOnUiThread {
                val msg = gson.fromJson(args[0].toString(), Message::class.java)
                Log.e("Emit...", "${msg.body}")
                messageList.add(msg)
                msgAdapter.notifyItemInserted(messageList.size)
                recyclerMsg.smoothScrollToPosition(messageList.size - 1)
            }
        }
        socket.on("room-message", messageListener)

        val deleteListener = Emitter.Listener { args ->
            this@MainActivity.runOnUiThread {
                val id = args[0].toString()
                for (position in 0 until messageList.size)
                    if (messageList[position]._id == id) {
                        messageList.removeAt(position)
                        msgAdapter.notifyItemRemoved(position)
                        break
                    }
            }
        }
        socket.on("room-message-delete", deleteListener)

        socket.connect()
    }

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    @RequiresApi(Build.VERSION_CODES.N)
    private fun networkListener() {
        var firstTime = true
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (firstTime) {
                    runOnUiThread {
                        loadingProgress.onLoading()
                        firstTime = false
                    }
                }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        socket.off("room-message", messageListener)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onItemClick(position: Int) {
        AlertDialog.Builder(this).setTitle("Delete message")
            .setMessage("Do you really want to delete that message?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                socket.emit("room-message-delete", messageList[position]._id)
                Log.e("Delete...", "${messageList[position]._id}")
            }
            .setNegativeButton("No", null)
            .show()
    }
}
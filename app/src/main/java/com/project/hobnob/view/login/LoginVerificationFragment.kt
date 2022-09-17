package com.project.hobnob.view.login

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.project.hobnob.R
import com.project.hobnob.databinding.FragmentLoginVerificationBinding
import com.project.hobnob.dialogs.ProgressDialog
import com.project.hobnob.model.UserSchema
import com.project.hobnob.utils.Constants
import com.project.hobnob.utils.GoEditText
import com.project.hobnob.viewmodel.login.LoginViewModel
import java.util.*


class LoginVerificationFragment : Fragment() {
    private var _binding: FragmentLoginVerificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var etOtp1: GoEditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentLoginVerificationBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = (activity as LoginActivity).loginViewModel
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        val prefEditor = sharedPreferences.edit()
        val navigation = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)

        val progressDialog = ProgressDialog(requireContext())
        var verifyIntended = false
        var sendOtpIntended = false

        var timeLeft = 0L
        val countDownTimer = object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeftFormatted =
                    String.format(Locale.getDefault(), "Try again in 00m:%02ds", timeLeft)
                binding.tvResend.text = timeLeftFormatted
                timeLeft--
            }

            override fun onFinish() {
                binding.tvResend.text = getString(R.string.resend_otp)
            }
        }
        loginViewModel.verifyOtpResponse.observe(viewLifecycleOwner, { response ->
            if (verifyIntended) {
                if (response?.statusOk == true) {
                    countDownTimer.cancel()
                    prefEditor.putBoolean(Constants.IS_VERIFIED, true)
                    prefEditor.apply()
                    navigation.navigate(R.id.action_loginVerificationFragment_to_loginSignupAboutFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        response?.statusString ?: "Some error occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                progressDialog.dismiss()
                verifyIntended = false
            }
        })

        loginViewModel.sendOtpResponse.observe(viewLifecycleOwner, { response ->
            if (sendOtpIntended) {
                if (response?.statusOk == true) {
                    timeLeft = 59L
                    countDownTimer.start()

                    Toast.makeText(
                        requireContext(),
                        "An OTP was sent on your email ID: ${
                            sharedPreferences.getString(
                                Constants.USER_EMAIL,
                                "nullId"
                            )
                        }",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        response?.statusString ?: "Some error occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                progressDialog.dismiss()
                sendOtpIntended = false
            }
        })

        etOtp1 = binding.etOtp1
        etOtp2 = binding.etOtp2
        etOtp3 = binding.etOtp3
        etOtp4 = binding.etOtp4
        etOtp5 = binding.etOtp5
        etOtp6 = binding.etOtp6
        var otp = ""
        etOtp1.doAfterTextChanged {
            if (etOtp1.text?.isNotEmpty() == true) {
                etOtp2.requestFocus()
            }
        }
        etOtp2.doAfterTextChanged {
            if (etOtp2.text.isNotEmpty()) {
                etOtp3.requestFocus()
            }
        }
        etOtp3.doAfterTextChanged {
            if (etOtp3.text.isNotEmpty()) {
                etOtp4.requestFocus()
            }
        }
        etOtp4.doAfterTextChanged {
            if (etOtp4.text.isNotEmpty()) {
                etOtp5.requestFocus()
            }
        }
        etOtp5.doAfterTextChanged {
            if (etOtp5.text.isNotEmpty()) {
                etOtp6.requestFocus()
            }
        }
        etOtp6.doAfterTextChanged {
            if (etOtp6.text.isNotEmpty()) {
                otp =
                    "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"
                if (otp.length == 6)
                    binding.btnVerify.performClick()
            }
        }

        editTextKeyListeners()

        binding.btnVerify.setOnClickListener {
            if (otp.isEmpty())
                otp =
                    "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"

            if (otp.length != 6)
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT)
                    .show()
            else {
                verifyIntended = true
                progressDialog.show()
                Log.e(
                    "Email",
                    "onViewCreated: ${
                        sharedPreferences.getString(
                            Constants.USER_EMAIL,
                            "nullEmail"
                        )
                    }"
                )
                Log.e("Email", "onViewCreated: $otp")
                loginViewModel.verifyOtp(
                    UserSchema(
                        _id = sharedPreferences.getString(Constants.USER_ID, "nullEmail"),
                        email = sharedPreferences.getString(Constants.USER_EMAIL, "nullEmail"),
                        _otp = otp
                    )
                )
            }
        }

        binding.tvResend.setOnClickListener {
            if (timeLeft == 0L) {
                sendOtpIntended = true
                progressDialog.show()
                loginViewModel.sendOtp(
                    UserSchema(
                        _id = sharedPreferences.getString(Constants.USER_ID, "nullId"),
                        email = sharedPreferences.getString(Constants.USER_EMAIL, "nullEmail")
                    )
                )
            }
        }


//        Paste event listener
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener {
            val text = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            if (text.length == 6 && text.matches(Regex("[0-9]+"))) {
                etOtp1.setText(text[0].toString())
                etOtp2.setText(text[1].toString())
                etOtp3.setText(text[2].toString())
                etOtp4.setText(text[3].toString())
                etOtp5.setText(text[4].toString())
                etOtp6.setText(text[5].toString())
            }
        }

        etOtp1.addListener(object : GoEditText.GoEditTextListener {
            override fun onUpdate() {
                val text = clipboard.primaryClip?.getItemAt(0)?.text.toString()
                if (text.length == 6 && text.matches(Regex("[0-9]+"))) {
                    etOtp1.setText(text[0].toString())
                    etOtp2.setText(text[1].toString())
                    etOtp3.setText(text[2].toString())
                    etOtp4.setText(text[3].toString())
                    etOtp5.setText(text[4].toString())
                    etOtp6.setText(text[5].toString())
                }
            }
        })
    }

    private fun editTextKeyListeners() {
        etOtp1.setOnKeyListener { _, keyCode, _ ->
            if (etOtp1.text?.isNotEmpty() == true && etOtp2.text.isEmpty() && keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) { // checking if number was pressed
                etOtp2.requestFocus()
                etOtp2.setText("${keyCode - KeyEvent.KEYCODE_0}")
            }
            return@setOnKeyListener false
        }
        etOtp2.setOnKeyListener { _, keyCode, _ ->
            if (etOtp2.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL) {
                etOtp1.requestFocus()
                etOtp1.setSelection(etOtp1.text?.length ?: 0)
                return@setOnKeyListener true
            } else if (etOtp2.text.isNotEmpty() && etOtp3.text.isEmpty() && keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) { // checking if number was pressed
                etOtp3.requestFocus()
                etOtp3.setText("${keyCode - KeyEvent.KEYCODE_0}")
            }
            return@setOnKeyListener false
        }
        etOtp3.setOnKeyListener { _, keyCode, _ ->
            if (etOtp3.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL) {
                etOtp2.requestFocus()
                etOtp2.setSelection(etOtp2.text.length)
                return@setOnKeyListener true
            } else if (etOtp3.text.isNotEmpty() && etOtp4.text.isEmpty() && keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) { // checking if number was pressed
                etOtp4.requestFocus()
                etOtp4.setText("${keyCode - KeyEvent.KEYCODE_0}")
            }
            return@setOnKeyListener false
        }
        etOtp4.setOnKeyListener { _, keyCode, _ ->
            if (etOtp4.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL) {
                etOtp3.requestFocus()
                etOtp3.setSelection(etOtp3.text.length)
                return@setOnKeyListener true
            } else if (etOtp4.text.isNotEmpty() && etOtp5.text.isEmpty() && keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) { // checking if number was pressed
                etOtp5.requestFocus()
                etOtp5.setText("${keyCode - KeyEvent.KEYCODE_0}")
            }
            return@setOnKeyListener false
        }
        etOtp5.setOnKeyListener { _, keyCode, _ ->
            if (etOtp5.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL) {
                etOtp4.requestFocus()
                etOtp4.setSelection(etOtp4.text.length)
                return@setOnKeyListener true
            } else if (etOtp5.text.isNotEmpty() && etOtp6.text.isEmpty() && keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) { // checking if number was pressed
                etOtp6.requestFocus()
                etOtp6.setText("${keyCode - KeyEvent.KEYCODE_0}")
            }
            return@setOnKeyListener false
        }
        etOtp6.setOnKeyListener { _, keyCode, _ ->
            if (etOtp6.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL) {
                etOtp5.requestFocus()
                etOtp5.setSelection(etOtp5.text.length)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
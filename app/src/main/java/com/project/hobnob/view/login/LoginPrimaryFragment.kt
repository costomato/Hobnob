package com.project.hobnob.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.project.hobnob.R
import com.project.hobnob.databinding.FragmentLoginPrimaryBinding
import com.project.hobnob.dialogs.ProgressDialog
import com.project.hobnob.model.UserSchema
import com.project.hobnob.utils.Constants
import com.project.hobnob.view.main.MainActivity
import com.project.hobnob.viewmodel.login.LoginViewModel


class LoginPrimaryFragment : Fragment() {
    private var _binding: FragmentLoginPrimaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentLoginPrimaryBinding.inflate(inflater, container, false)
            // to do... initialise() function
//            initialise(savedInstanceState)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navigation = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
        binding.tvForgotPass.setOnClickListener {
            navigation.navigate(R.id.action_loginPrimaryFragment_to_loginResetPassFragment)
        }
        binding.tvSignup.setOnClickListener {
            navigation.navigate(R.id.action_loginPrimaryFragment_to_loginSignupFragment)
        }

        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        val prefEditor = sharedPreferences.edit()
        val progressDialog = ProgressDialog(requireContext())

        Log.e(
            "USer", "onViewCreated: ${
                sharedPreferences.getString(
                    Constants.USER_ID,
                    null
                )
            }"
        )
        Log.e(
            "ABoutVerified",
            "onViewCreated: ${sharedPreferences.getBoolean(Constants.ABOUT_VERIFIED, false)}"
        )

        /*if (sharedPreferences.getString(Constants.USER_ID, null) != null &&
            !sharedPreferences.getBoolean(Constants.ABOUT_VERIFIED, false)
            && sharedPreferences.getBoolean(Constants.IS_VERIFIED, false)
        ) navigation.navigate(R.id.loginSignupAboutFragment)*/


        loginViewModel = (activity as LoginActivity).loginViewModel
        loginViewModel.authenticateResponse.observe(viewLifecycleOwner, { response ->
            Log.e("AUTH VAL", "onViewCreated: ${loginViewModel.authenticateResponse.value}")
            if (loginViewModel.authenticateResponse.value != null) {
                if (response?.statusOk == true) {
                    prefEditor.putString(
                        Constants.USER_NAME,
                        response._name
                    )
                    prefEditor.putString(
                        Constants.USER_EMAIL,
                        binding.etEmail.editText?.text.toString()
                    )
                    prefEditor.putString(Constants.USER_ID, response._id)
                    prefEditor.putString(
                        Constants.USER_PASS,
                        binding.etPassword.editText?.text.toString()
                    )
                    prefEditor.putBoolean(
                        Constants.IS_VERIFIED,
                        response._isVerified ?: false
                    )
                    prefEditor.putBoolean(
                        Constants.IS_TEACHER,
                        response._isTeacher ?: false
                    )
                    prefEditor.apply()

                    if (response._isVerified == true) {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    } else
                        navigation.navigate(R.id.action_loginPrimaryFragment_to_loginVerificationFragment)

                } else {
                    Toast.makeText(
                        requireContext(),
                        response?.statusString ?: "Some error occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                progressDialog.dismiss()
                loginViewModel.authenticateResponse.value = null
            }
        })
        binding.btnLogin.setOnClickListener {
            if (isCredentialValid()) {
                progressDialog.show()
                loginViewModel.authenticateUser(
                    UserSchema(
                        email = binding.etEmail.editText?.text.toString(),
                        password = binding.etPassword.editText?.text.toString()
                    )
                )
            }
        }
    }

    private fun initialise(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            binding.etEmail.editText?.text = Editable.Factory().newEditable(
                savedInstanceState.getCharSequence("email")
            )
            binding.etPassword.editText?.text = Editable.Factory().newEditable(
                savedInstanceState.getCharSequence("password")
            )
        }
    }

    private fun isCredentialValid(): Boolean {
        val email = binding.etEmail.editText?.text?.toString()
        val password = binding.etPassword.editText?.text?.toString()

        val isEmailValid = !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid =
            !password.isNullOrEmpty() && password.length in 8..24

        binding.etEmail.isErrorEnabled = false
        binding.etPassword.isErrorEnabled = false

        return when {
            !isEmailValid -> {
                binding.etEmail.error = "Email is invalid"
                false
            }
            !isPasswordValid -> {
                binding.etPassword.error = "Password must have 8 to 24 characters"
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.project.hobnob.view.login

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.project.hobnob.R
import com.project.hobnob.databinding.FragmentLoginSignupBinding
import com.project.hobnob.dialogs.ProgressDialog
import com.project.hobnob.model.UserSchema
import com.project.hobnob.utils.Constants
import com.project.hobnob.viewmodel.login.LoginViewModel

class LoginSignupFragment : Fragment() {
    private var _binding: FragmentLoginSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentLoginSignupBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navigation = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)

        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        val prefEditor = sharedPreferences.edit()
        val progressDialog = ProgressDialog(requireContext())

        loginViewModel = (activity as LoginActivity).loginViewModel
        loginViewModel.createUserResponse.observe(viewLifecycleOwner, {
            if (it?.statusOk == true) {
                prefEditor.putString(
                    Constants.USER_NAME,
                    binding.etName.editText?.text.toString()
                )
                prefEditor.putString(
                    Constants.USER_EMAIL,
                    binding.etEmail.editText?.text.toString()
                )
                prefEditor.putString(
                    Constants.USER_PASS,
                    binding.etPassword.editText?.text.toString()
                )
                prefEditor.putString(Constants.USER_ID, it._id)
                prefEditor.apply()
                navigation.navigate(R.id.action_loginSignupFragment_to_loginVerificationFragment)
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    it?.statusString ?: "Some error occurred",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            progressDialog.dismiss()
        })
        binding.btnSignup.setOnClickListener {
            if (isCredentialValid()) {
                progressDialog.show()
                loginViewModel.createUser(
                    UserSchema(
                        name = binding.etName.editText?.text.toString(),
                        email = binding.etEmail.editText?.text.toString(),
                        password = binding.etPassword.editText?.text.toString()
                    )
                )
            }
        }

        binding.tvLogin.setOnClickListener {
            // to do
            navigation.popBackStack()
        }
    }

    private fun isCredentialValid(): Boolean {
        val email = binding.etEmail.editText?.text?.toString()
        val name = binding.etName.editText?.text?.toString()
        val password = binding.etPassword.editText?.text?.toString()
        val confirmPassword = binding.etConfirmPassword.editText?.text?.toString()

        val isNameValid = !name.isNullOrEmpty() && name.length in 2..45
        val isEmailValid = !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid =
            !password.isNullOrEmpty() && password.length in 8..24

        binding.etName.isErrorEnabled = false
        binding.etEmail.isErrorEnabled = false
        binding.etPassword.isErrorEnabled = false
        binding.etConfirmPassword.isErrorEnabled = false

        return when {
            !isNameValid -> {
                binding.etName.error = "Name must have 2 to 45 characters"
                false
            }
            !isEmailValid -> {
                binding.etEmail.error = "Email is invalid"
                false
            }
            !isPasswordValid -> {
                binding.etPassword.error = "Password must have 8 to 24 characters"
                false
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Passwords do not match"
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
package com.project.hobnob.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.project.hobnob.R
import com.project.hobnob.databinding.FragmentLoginSignupAboutBinding
import com.project.hobnob.dialogs.ProgressDialog
import com.project.hobnob.model.UserSchema
import com.project.hobnob.utils.Constants
import com.project.hobnob.view.main.MainActivity
import com.project.hobnob.viewmodel.login.LoginViewModel

class LoginSignupAboutFragment : Fragment() {
    private var _binding: FragmentLoginSignupAboutBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentLoginSignupAboutBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOpenDashboard.text = getString(R.string.go_to_dashboard, "main")

        var isTeacher = false
        binding.btnStudent.setOnClickListener {
            toggleButton(false)
            isTeacher = false
        }
        binding.btnTeacher.setOnClickListener {
            toggleButton(true)
            isTeacher = true
        }

        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        val prefEditor = sharedPreferences.edit()

        val progressDialog = ProgressDialog(requireContext())

        loginViewModel = (activity as LoginActivity).loginViewModel
        loginViewModel.updateResponse.observe(viewLifecycleOwner, {
            if (it?.statusOk == true) {
                prefEditor.putBoolean(Constants.ABOUT_VERIFIED, true)
                prefEditor.apply()
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(
                    requireContext(),
                    it?.statusString ?: "Some error occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }
            progressDialog.dismiss()
        })
        binding.btnOpenDashboard.setOnClickListener {
            progressDialog.show()
            loginViewModel.updateUser(
                UserSchema(
                    _id = sharedPreferences.getString(
                        Constants.USER_ID,
                        null
                    ), isTeacher = isTeacher
                )
            )
        }

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK && !sharedPreferences.getBoolean(
                    Constants.ABOUT_VERIFIED,
                    false
                )
            ) {
                requireActivity().finish()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun toggleButton(position: Boolean) {
        if (position) {
            binding.btnStudent.backgroundTintList = null
            binding.btnTeacher.backgroundTintList =
                AppCompatResources.getColorStateList(requireContext(), R.color.btnBgBlue)
            binding.btnOpenDashboard.text = getString(R.string.go_to_dashboard, "admin")
            return
        }
        binding.btnTeacher.backgroundTintList = null
        binding.btnStudent.backgroundTintList =
            AppCompatResources.getColorStateList(requireContext(), R.color.btnBgBlue)
        binding.btnOpenDashboard.text = getString(R.string.go_to_dashboard, "main")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
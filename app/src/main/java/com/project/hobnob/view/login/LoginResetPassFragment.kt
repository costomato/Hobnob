package com.project.hobnob.view.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.project.hobnob.R
import com.project.hobnob.databinding.FragmentLoginResetPassBinding
import com.project.hobnob.dialogs.ProgressDialog
import com.project.hobnob.model.UserSchema
import com.project.hobnob.viewmodel.login.LoginViewModel

class LoginResetPassFragment : Fragment() {
    private var _binding: FragmentLoginResetPassBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentLoginResetPassBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navigation = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
        val progressDialog = ProgressDialog(requireContext())

        loginViewModel = (activity as LoginActivity).loginViewModel
        loginViewModel.resetPasswordResponse.observe(viewLifecycleOwner, {
            if (it?.statusOk == true) {
                changeButtonStyle()
                binding.tvPleaseCheck.text = getString(R.string.please_check_mail)
                binding.btnReset.setOnClickListener(null)
            } else
                binding.tvPleaseCheck.text = it?.statusString ?: "Some error occurred"
            progressDialog.dismiss()
        })
        binding.btnReset.setOnClickListener {
            if (isCredentialValid()) {
                progressDialog.show()
                loginViewModel.sendResetLink(UserSchema(email = binding.etEmail.editText?.text.toString()))
            }
        }

        binding.tvLogin.setOnClickListener {
            navigation.popBackStack()
        }
    }

    private fun changeButtonStyle() {
        binding.btnReset.backgroundTintList =
            AppCompatResources.getColorStateList(requireContext(), R.color.btnBgBlue)
        binding.tvResetPass.text = getString(R.string.confirmation_sent)
        binding.ivConfirmed.visibility = View.VISIBLE
    }

    private fun isCredentialValid(): Boolean {
        val email = binding.etEmail.editText?.text?.toString()
        val isEmailValid = !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

        return when {
            !isEmailValid -> {
                binding.etEmail.error = "Email is invalid"
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
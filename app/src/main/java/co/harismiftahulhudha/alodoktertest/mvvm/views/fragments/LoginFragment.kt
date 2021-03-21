package co.harismiftahulhudha.alodoktertest.mvvm.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.FragmentLoginBinding
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.UserViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.AuthenticationActivity
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        subscribeListeners()
        subscribeObservers()
    }

    private fun initComponents(view: View) {
        binding = FragmentLoginBinding.bind(view)
        binding.apply {
            inputEmail.setText(viewModel.emailLogin)
            inputPassword.setText(viewModel.passwordLogin)
        }
    }

    private fun subscribeListeners() {
        binding.apply {
            inputEmail.addTextChangedListener {
                viewModel.emailLogin = inputEmail.text.toString()
                if (inputEmail.text.toString().isNotBlank()) {
                    txtEmailError.visibility = View.GONE
                }
            }
            inputPassword.addTextChangedListener {
                viewModel.passwordLogin = inputPassword.text.toString()
                if (inputPassword.text.toString().isNotBlank()) {
                    txtPasswordError.visibility = View.GONE
                }
            }
            btnLogin.setOnClickListener {
                viewModel.onClickLogin()
            }
            btnRegister.setOnClickListener {
                viewModel.onClickRegisterPage()
            }
        }
    }

    private fun subscribeObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userEvent.collect { event ->
                when (event) {
                    is UserViewModel.UserEvent.NavigateToRegister -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                        findNavController().navigate(action)
                    }
                    is UserViewModel.UserEvent.NavigateToLogin -> {
                        (activity as AuthenticationActivity).onBackPressed()
                    }
                    is UserViewModel.UserEvent.NavigateToMain -> {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    is UserViewModel.UserEvent.ShowInputLoginError -> {
                        when (event.key) {
                            UserViewModel.EMAIL_LOGIN -> {
                                binding.txtEmailError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtEmailError.visibility = View.VISIBLE
                                } else {
                                    binding.txtEmailError.visibility = View.GONE
                                }
                            }
                            UserViewModel.PASSWORD_LOGIN -> {
                                binding.txtPasswordError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtPasswordError.visibility = View.VISIBLE
                                } else {
                                    binding.txtPasswordError.visibility = View.GONE
                                }
                            }
                            else -> {
                                //
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUsers().observe(viewLifecycleOwner, {
                // hanya untuk mentrigger onCreate databaseRoom
                it.forEach {
                    Log.d(TAG, "subscribeObservers: getUsers $it")
                }
            })
            viewModel.preferencesFlow.collect {
                if (it.id != -1) {
                    viewModel.getUser(it.id).observe(viewLifecycleOwner, {
                        if (it != null) {
                            viewModel.onNavigateToMainPage()
                        }
                    })
                }
            }
        }

        viewModel.getErrorMessage().observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotBlank()) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getResultLogin().observe(viewLifecycleOwner, {
            if (it != null) {
                if (it > 0) {
                    viewModel.updateUserId(it)
                    Toast.makeText(requireContext(), "Berhasil login", Toast.LENGTH_SHORT).show()
                }
                viewModel.getResultLogin().value = null
            }
        })
    }
}
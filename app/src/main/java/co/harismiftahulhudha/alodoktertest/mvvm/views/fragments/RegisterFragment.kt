package co.harismiftahulhudha.alodoktertest.mvvm.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.FragmentRegisterBinding
import co.harismiftahulhudha.alodoktertest.mvvm.models.UserModel
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.UserViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        subscribeListeners()
        subscribeObservers()
    }

    private fun initComponents(view: View) {
        binding = FragmentRegisterBinding.bind(view)
        binding.apply {
            inputName.setText(viewModel.nameRegister)
            inputPhone.setText(viewModel.phoneRegister)
            inputEmail.setText(viewModel.emailRegister)
            inputPassword.setText(viewModel.passwordRegister)
            switchGender.isChecked = viewModel.genderRegister == UserModel.MALE
        }
    }

    private fun subscribeListeners() {
        binding.apply {
            inputName.addTextChangedListener {
                viewModel.nameRegister = inputName.text.toString()
                if (inputName.text.toString().isNotBlank()) {
                    txtNameError.visibility = View.GONE
                }
            }
            inputPhone.addTextChangedListener {
                viewModel.phoneRegister = inputPhone.text.toString()
                if (inputPhone.text.toString().isNotBlank()) {
                    txtPhoneError.visibility = View.GONE
                }
            }
            inputEmail.addTextChangedListener {
                viewModel.emailRegister = inputEmail.text.toString()
                if (inputEmail.text.toString().isNotBlank()) {
                    txtEmailError.visibility = View.GONE
                }
            }
            inputPassword.addTextChangedListener {
                viewModel.passwordRegister = inputPassword.text.toString()
                if (inputPassword.text.toString().isNotBlank()) {
                    txtPasswordError.visibility = View.GONE
                }
            }
            switchGender.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    viewModel.genderRegister = if (isChecked) {
                        1
                    } else {
                        0
                    }
                }

            })
            btnLogin.setOnClickListener {
                viewModel.onClickLoginPage()
            }
            btnRegister.setOnClickListener {
                viewModel.onClickRegister()
            }
        }
    }

    private fun subscribeObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userEvent.collect { event ->
                when (event) {
                    is UserViewModel.UserEvent.NavigateToMain -> {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    is UserViewModel.UserEvent.ShowInputLoginError -> {
                        when (event.key) {
                            UserViewModel.NAME_REGISTER -> {
                                binding.txtNameError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtNameError.visibility = View.VISIBLE
                                } else {
                                    binding.txtNameError.visibility = View.GONE
                                }
                            }
                            UserViewModel.PHONE_REGISTER -> {
                                binding.txtPhoneError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtPhoneError.visibility = View.VISIBLE
                                } else {
                                    binding.txtPhoneError.visibility = View.GONE
                                }
                            }
                            UserViewModel.EMAIL_REGISTER -> {
                                binding.txtEmailError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtEmailError.visibility = View.VISIBLE
                                } else {
                                    binding.txtEmailError.visibility = View.GONE
                                }
                            }
                            UserViewModel.PASSWORD_REGISTER -> {
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

        viewModel.getResultRegister().observe(viewLifecycleOwner, {
            if (it != null) {
                if (it > 0) {
                    viewModel.updateUserId(it)
                    Toast.makeText(requireContext(), "Berhasil register", Toast.LENGTH_SHORT).show()
                }
                viewModel.getResultRegister().value = null
            }
        })
    }
}
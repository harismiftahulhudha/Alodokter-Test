package co.harismiftahulhudha.alodoktertest.mvvm.views.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.databinding.FragmentUserBinding
import co.harismiftahulhudha.alodoktertest.helpers.PermissionHelper
import co.harismiftahulhudha.alodoktertest.helpers.RequestCodeHelper
import co.harismiftahulhudha.alodoktertest.mvvm.models.UserModel
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.UserViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.CustomGalleryActivity
import co.harismiftahulhudha.alodoktertest.mvvm.views.activities.MainActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "UserFragment"

@AndroidEntryPoint
class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserViewModel by viewModels()
    private var itemEdit: MenuItem? = null
    private var itemClose: MenuItem? = null
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        subscribeListeners()
        subscribeObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_user_menu, menu)
        itemEdit = menu.findItem(R.id.editUser)
        itemClose = menu.findItem(R.id.closeEdit)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editUser -> {
                viewModel.updateIsUserEdit(true)
            }
            R.id.closeEdit -> {
                viewModel.updateIsUserEdit(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCodeHelper.REQUEST_STORAGE) {
            if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodeHelper.OPEN_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val path = data.getStringExtra(CustomGalleryActivity.FILE)!!
            Glide.with(requireContext())
                .load(path)
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.gallery_kosong))
                .error(ContextCompat.getDrawable(requireContext(), R.drawable.gallery_kosong))
                .into(binding.imgUserEdit)
            viewModel.photoEdit = path
        }
    }

    private fun initComponents(view: View) {
        binding = FragmentUserBinding.bind(view)
        (requireActivity() as MainActivity).binding.bnvMain.menu.findItem(R.id.userNav)
            .setChecked(true)
        permissionHelper = PermissionHelper(requireContext(), this)
    }

    private fun subscribeListeners() {
        binding.apply {
            inputName.addTextChangedListener {
                viewModel.nameEdit = inputName.text.toString()
                if (inputName.text.toString().isNotBlank()) {
                    txtNameError.visibility = View.GONE
                }
            }
            inputPhone.addTextChangedListener {
                viewModel.phoneEdit = inputPhone.text.toString()
                if (inputPhone.text.toString().isNotBlank()) {
                    txtPhoneError.visibility = View.GONE
                }
            }
            inputEmail.addTextChangedListener {
                viewModel.emailEdit = inputEmail.text.toString()
                if (inputEmail.text.toString().isNotBlank()) {
                    txtEmailError.visibility = View.GONE
                }
            }
            switchGender.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    viewModel.genderEdit = if (isChecked) {
                        UserModel.MALE
                    } else {
                        UserModel.FEMALE
                    }
                }
            })

            imgUserEdit.setOnClickListener {
                viewModel.onClickGalleryPage()
            }
            btnSave.setOnClickListener {
                viewModel.onClickUpdateUser()
            }
        }
    }

    private fun subscribeObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userEvent.collect { event ->
                when (event) {
                    is UserViewModel.UserEvent.ShowInputLoginError -> {
                        when (event.key) {
                            UserViewModel.NAME_EDIT -> {
                                binding.txtNameError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtNameError.visibility = View.VISIBLE
                                } else {
                                    binding.txtNameError.visibility = View.GONE
                                }
                            }
                            UserViewModel.PHONE_EDIT -> {
                                binding.txtPhoneError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtPhoneError.visibility = View.VISIBLE
                                } else {
                                    binding.txtPhoneError.visibility = View.GONE
                                }
                            }
                            UserViewModel.EMAIL_EDIT -> {
                                binding.txtEmailError.text = event.text
                                if (event.text.isNotBlank()) {
                                    binding.txtEmailError.visibility = View.VISIBLE
                                } else {
                                    binding.txtEmailError.visibility = View.GONE
                                }
                            }
                            else -> {
                                //
                            }
                        }
                    }
                    is UserViewModel.UserEvent.NavigateToGallery -> {
                        if (permissionHelper.hasReadAndWriteStoragePermission()) {
                            pickImageFromGallery()
                        } else {
                            permissionHelper.requestReadAndWriteStoragePermission()
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
            viewModel.getUser(viewModel.preferencesFlow.first().id).observe(viewLifecycleOwner, {
                if (it != null) {
                    binding.apply {
                        viewModel.user = it
                        txtName.text = it.name
                        txtPhone.text = it.phone
                        txtEmail.text = it.email
                        txtGender.text = if (it.gender == UserModel.MALE) {
                            "Male"
                        } else {
                            "Female"
                        }
                        if (it.photo != null) {
                            Glide.with(requireContext())
                                .load(it.photo)
                                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.gallery_kosong))
                                .error(ContextCompat.getDrawable(requireContext(), R.drawable.gallery_kosong))
                                .into(imgUser)
                        } else {
                            imgUser.setImageResource(R.drawable.gallery_kosong)
                        }

                        viewModel.nameEdit = viewModel.user!!.name
                        viewModel.phoneEdit = viewModel.user!!.phone
                        viewModel.emailEdit = viewModel.user!!.email
                        viewModel.photoEdit = viewModel.user!!.photo
                        viewModel.genderEdit = viewModel.user!!.gender

                        inputName.setText(viewModel.nameEdit)
                        inputPhone.setText(viewModel.phoneEdit)
                        inputEmail.setText(viewModel.emailEdit)
                        switchGender.isChecked = viewModel.genderEdit == UserModel.MALE
                        if (viewModel.photoEdit != null) {
                            Glide.with(requireContext())
                                .load(viewModel.photoEdit)
                                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.gallery_kosong))
                                .error(ContextCompat.getDrawable(requireContext(), R.drawable.gallery_kosong))
                                .into(imgUserEdit)
                        } else {
                            imgUserEdit.setImageResource(R.drawable.gallery_kosong)
                        }
                    }
                }
            })
            viewModel.preferencesFlow.collect {
                Log.d(TAG, "subscribeObservers: flowOvers ${it.isUserEdit}")
                Handler(Looper.getMainLooper()).postDelayed({
                    if (itemEdit != null && itemClose != null) {
                        if (it.isUserEdit) {
                            itemEdit!!.setVisible(false)
                            itemClose!!.setVisible(true)
                            binding.constraintRead.visibility = View.GONE
                            binding.constraintEdit.visibility = View.VISIBLE
                        } else {
                            itemEdit!!.setVisible(true)
                            itemClose!!.setVisible(false)
                            binding.constraintRead.visibility = View.VISIBLE
                            binding.constraintEdit.visibility = View.GONE
                        }
                    }
                }, 100)
            }
        }

        viewModel.getErrorMessage().observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotBlank()) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getResultEdit().observe(viewLifecycleOwner, {
            if (it != null) {
                if (it > 0) {
                    Toast.makeText(requireContext(), "Berhasil edit profil", Toast.LENGTH_SHORT).show()
                    binding.inputPassword.setText("")
                    viewModel.updateIsUserEdit(false)
                    viewModel.getResultEdit().value = null
                }
            }
        })
    }

    private fun pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(requireActivity(), CustomGalleryActivity::class.java)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(CustomGalleryActivity.IS_MULTIPLE, false)
            startActivityForResult(intent, RequestCodeHelper.OPEN_GALLERY)
        } else {
            val intent = Intent(requireActivity(), CustomGalleryActivity::class.java)
            intent.putExtra(CustomGalleryActivity.IS_MULTIPLE, false)
            startActivityForResult(intent, RequestCodeHelper.OPEN_GALLERY)
        }
    }
}
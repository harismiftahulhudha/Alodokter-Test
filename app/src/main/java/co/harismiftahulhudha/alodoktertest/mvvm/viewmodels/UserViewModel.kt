package co.harismiftahulhudha.alodoktertest.mvvm.viewmodels

import android.util.Log
import androidx.lifecycle.*
import co.harismiftahulhudha.alodoktertest.BuildConfig
import co.harismiftahulhudha.alodoktertest.helpers.FormatStringHelper
import co.harismiftahulhudha.alodoktertest.mvvm.models.UserModel
import co.harismiftahulhudha.alodoktertest.mvvm.repositories.UserRepository
import co.harismiftahulhudha.alodoktertest.mvvm.views.fragments.LoginFragment
import co.harismiftahulhudha.alodoktertest.mvvm.views.fragments.RegisterFragment
import co.harismiftahulhudha.alodoktertest.mvvm.views.fragments.UserFragment
import co.harismiftahulhudha.alodoktertest.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val state: SavedStateHandle
): ViewModel() {

    fun getUsers(): LiveData<MutableList<UserModel>> {
        return userRepository.getUsers()
    }

    fun getUser(userId: Int): LiveData<UserModel> {
        return userRepository.getUser(userId)
    }

    fun updateUserId(userId: Int) = viewModelScope.launch {
        sessionManager.updateId(userId)
    }

    fun updateIsUserEdit(isUserEdit: Boolean = false) = viewModelScope.launch {
        sessionManager.updateIsUserEdit(isUserEdit)
    }

    fun getErrorMessage() = userRepository.errorMessage
    fun getResultLogin() = userRepository.resultLogin
    fun getResultRegister() = userRepository.resultRegister
    fun getResultEdit() = userRepository.resultEdit

    var user = state.get<UserModel>(USER)
        set(value) {
            field = value
            state.set(USER, value)
        }

    var emailLogin = state.get<String>(EMAIL_LOGIN) ?: ""
        set(value) {
            field = value
            state.set(EMAIL_LOGIN, value)
        }

    var passwordLogin = state.get<String>(PASSWORD_LOGIN) ?: ""
        set(value) {
            field = value
            state.set(PASSWORD_LOGIN, value)
        }

    var nameRegister = state.get<String>(NAME_REGISTER) ?: ""
        set(value) {
            field = value
            state.set(NAME_REGISTER, value)
        }

    var phoneRegister = state.get<String>(PHONE_REGISTER) ?: ""
        set(value) {
            field = value
            state.set(PHONE_REGISTER, value)
        }

    var emailRegister = state.get<String>(EMAIL_REGISTER) ?: ""
        set(value) {
            field = value
            state.set(EMAIL_REGISTER, value)
        }

    var passwordRegister = state.get<String>(PASSWORD_REGISTER) ?: ""
        set(value) {
            field = value
            state.set(PASSWORD_REGISTER, value)
        }

    var genderRegister = state.get<Int>(GENDER_REGISTER) ?: UserModel.FEMALE
        set(value) {
            field = value
            state.set(GENDER_REGISTER, value)
        }

    var nameEdit = state.get<String>(NAME_EDIT) ?: ""
        set(value) {
            field = value
            state.set(NAME_EDIT, value)
        }

    var phoneEdit = state.get<String>(PHONE_EDIT) ?: ""
        set(value) {
            field = value
            state.set(PHONE_EDIT, value)
        }

    var emailEdit = state.get<String>(EMAIL_EDIT) ?: ""
        set(value) {
            field = value
            state.set(EMAIL_EDIT, value)
        }

    var passwordEdit = state.get<String>(PASSWORD_EDIT) ?: ""
        set(value) {
            field = value
            state.set(PASSWORD_EDIT, value)
        }

    var genderEdit = state.get<Int>(GENDER_EDIT) ?: UserModel.FEMALE
        set(value) {
            field = value
            state.set(GENDER_EDIT, value)
        }

    var photoEdit = state.get<String>(PHOTO_EDIT)
        set(value) {
            field = value
            state.set(PHOTO_EDIT, value)
        }

    val preferencesFlow = sessionManager.preferencesFlow

    private val userEventChannel = Channel<UserEvent>()
    val userEvent = userEventChannel.receiveAsFlow()

    fun onClickLogin() = viewModelScope.launch {
        var countError = 0
        if (emailLogin.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(EMAIL_LOGIN, "Email tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(EMAIL_LOGIN, ""))
        }

        if (passwordLogin.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(PASSWORD_LOGIN, "Password tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(PASSWORD_LOGIN, ""))
        }

        if (countError == 0) {
            userRepository.login(emailLogin, FormatStringHelper.getMD5(passwordLogin))
        }
    }

    fun onClickRegister() = viewModelScope.launch {
        var countError = 0
        if (nameRegister.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(NAME_REGISTER, "Nama tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(NAME_REGISTER, ""))
        }
        if (phoneRegister.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(PHONE_REGISTER, "Telepon tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(PHONE_REGISTER, ""))
        }
        if (emailRegister.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(EMAIL_REGISTER, "Email tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(EMAIL_REGISTER, ""))
        }
        if (passwordRegister.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(PASSWORD_REGISTER, "Password tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(PASSWORD_REGISTER, ""))
        }

        if (countError == 0) {
            val user = UserModel(nameRegister, phoneRegister, emailRegister, FormatStringHelper.getMD5(passwordRegister), genderRegister)
            userRepository.insert(user)
        }
    }

    fun onClickUpdateUser() = viewModelScope.launch {
        var countError = 0
        if (nameEdit.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(NAME_EDIT, "Nama tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(NAME_EDIT, ""))
        }
        if (phoneEdit.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(PHONE_EDIT, "Telepon tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(PHONE_EDIT, ""))
        }
        if (emailEdit.isBlank()) {
            countError++
            userEventChannel.send(UserEvent.ShowInputLoginError(EMAIL_EDIT, "Email tidak boleh kosong"))
        } else {
            userEventChannel.send(UserEvent.ShowInputLoginError(EMAIL_EDIT, ""))
        }
        val password = if (passwordEdit.isBlank()) {
            user?.password!!
        } else {
            FormatStringHelper.getMD5(passwordEdit)
        }

        if (countError == 0) {
            Log.d(TAG, "onClickUpdateUser: ${nameEdit}")
            Log.d(TAG, "onClickUpdateUser: ${genderEdit}")
            val updateUser = user?.copy(
                name = nameEdit,
                phone = phoneEdit,
                email = emailEdit,
                password = password,
                gender = genderEdit,
                photo = photoEdit
            )!!
            userRepository.update(updateUser, emailEdit)
        }
    }

    fun onClickGalleryPage() = viewModelScope.launch {
        userEventChannel.send(UserEvent.NavigateToGallery)
    }

    fun onClickLoginPage() = viewModelScope.launch {
        userEventChannel.send(UserEvent.NavigateToLogin)
    }

    fun onClickRegisterPage() = viewModelScope.launch {
        userEventChannel.send(UserEvent.NavigateToRegister)
    }

    fun onNavigateToMainPage() = viewModelScope.launch {
        userEventChannel.send(UserEvent.NavigateToMain)
    }

    sealed class UserEvent {
        data class ShowInputLoginError(val key: String, val text: String): UserEvent()
        object NavigateToLogin: UserEvent()
        object NavigateToRegister: UserEvent()
        object NavigateToGallery: UserEvent()
        object NavigateToMain: UserEvent()
    }

    companion object {
        val EMAIL_LOGIN = "${BuildConfig.APPLICATION_ID}_${LoginFragment::class.java.simpleName}_EMAIL_LOGIN"
        val PASSWORD_LOGIN = "${BuildConfig.APPLICATION_ID}_${LoginFragment::class.java.simpleName}_PASSWORD_LOGIN"

        val NAME_REGISTER = "${BuildConfig.APPLICATION_ID}_${RegisterFragment::class.java.simpleName}_NAME_REGISTER"
        val PHONE_REGISTER = "${BuildConfig.APPLICATION_ID}_${RegisterFragment::class.java.simpleName}_PHONE_REGISTER"
        val EMAIL_REGISTER = "${BuildConfig.APPLICATION_ID}_${RegisterFragment::class.java.simpleName}_EMAIL_REGISTER"
        val PASSWORD_REGISTER = "${BuildConfig.APPLICATION_ID}_${RegisterFragment::class.java.simpleName}_PASSWORD_REGISTER"
        val GENDER_REGISTER = "${BuildConfig.APPLICATION_ID}_${RegisterFragment::class.java.simpleName}_GENDER_REGISTER"

        val USER = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_USER"
        val NAME_EDIT = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_NAME_EDIT"
        val PHONE_EDIT = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_PHONE_EDIT"
        val EMAIL_EDIT = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_EMAIL_EDIT"
        val PASSWORD_EDIT = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_PASSWORD_EDIT"
        val GENDER_EDIT = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_GENDER_EDIT"
        val PHOTO_EDIT = "${BuildConfig.APPLICATION_ID}_${UserFragment::class.java.simpleName}_PHOTO_EDIT"
    }
}
package co.harismiftahulhudha.alodoktertest.mvvm.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import co.harismiftahulhudha.alodoktertest.database.dao.UserDao
import co.harismiftahulhudha.alodoktertest.mvvm.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class UserRepository @Inject constructor(
    private val dao: UserDao
): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val errorMessage: MutableLiveData<String> = MediatorLiveData()
    val resultLogin: MutableLiveData<Int> = MediatorLiveData()
    val resultRegister: MutableLiveData<Int> = MediatorLiveData()
    val resultEdit: MutableLiveData<Int> = MediatorLiveData()

    fun getUsers(): LiveData<MutableList<UserModel>> {
        val userFlow = dao.getUsers()

        return userFlow.asLiveData()
    }

    fun getUser(userId: Int): LiveData<UserModel> {
        val userFlow = dao.getUser(userId)

        return userFlow.asLiveData()
    }

    suspend fun insert(user: UserModel) {
        withContext(Dispatchers.IO) {
            if (dao.checkIfEmailExists(user.email) == 0) {
                dao.insert(user)
                val newUser = dao.getLastUser()
                resultRegister.postValue(newUser.id)
            } else {
                errorMessage.postValue("Email sudah dipakai")
            }
        }
    }

    suspend fun update(user: UserModel, realEmail: String) {
        withContext(Dispatchers.IO) {
            if (user.email.equals(realEmail)) {
                dao.update(user)
                resultEdit.postValue(1)
            } else {
                if (dao.checkIfEmailExists(user.email) == 0) {
                    dao.update(user)
                    resultEdit.postValue(1)
                } else {
                    errorMessage.postValue("Email sudah dipakai")
                }
            }
        }
    }

    suspend fun login(email: String, password: String) {
        withContext(Dispatchers.IO) {
            val res = dao.checkIfUserIsCorrect(email, password)
            if (res > 0) {
                errorMessage.postValue("")
            } else {
                errorMessage.postValue("Password atau Email anda salah !")
            }
            val result = res
            resultLogin.postValue(result)
        }
    }
}
package co.harismiftahulhudha.alodoktertest.database.dao

import androidx.room.*
import co.harismiftahulhudha.alodoktertest.mvvm.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    fun getUsers(): Flow<MutableList<UserModel>>

    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUser(userId: Int): Flow<UserModel>

    @Query("SELECT id FROM user_table WHERE email = :email AND password = :password")
    fun checkIfUserIsCorrect(email: String, password: String): Int

    @Query("SELECT COUNT(*) FROM user_table WHERE email = :email")
    fun checkIfEmailExists(email: String): Int

    @Query("SELECT * FROM user_table ORDER BY id DESC LIMIT 1")
    fun getLastUser(): UserModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserModel)

    @Update
    suspend fun update(user: UserModel)
}
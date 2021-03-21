package co.harismiftahulhudha.alodoktertest.mvvm.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.harismiftahulhudha.alodoktertest.database.DateConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "user_table")
@TypeConverters(DateConverter::class)
@Parcelize
data class UserModel(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val gender: Int = FEMALE,
    var photo: String? = null,
    val createdAt: Date = Date(System.currentTimeMillis()),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Parcelable {
    companion object {
        val MALE = 1
        val FEMALE = 0
    }
}
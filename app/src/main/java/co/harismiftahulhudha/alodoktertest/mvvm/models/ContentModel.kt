package co.harismiftahulhudha.alodoktertest.mvvm.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.harismiftahulhudha.alodoktertest.database.DateConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "content_table")
@TypeConverters(DateConverter::class)
@Parcelize
data class ContentModel(
    val description: String,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val createdAt: Date = Date(Calendar.getInstance().time.time),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Parcelable
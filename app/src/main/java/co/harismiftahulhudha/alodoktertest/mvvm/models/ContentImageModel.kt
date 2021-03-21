package co.harismiftahulhudha.alodoktertest.mvvm.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "content_image_table")
@Parcelize
data class ContentImageModel(
    val path: String,
    @ColumnInfo(name = "content_id")
    val contentId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Parcelable
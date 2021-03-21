package co.harismiftahulhudha.alodoktertest.mvvm.joinmodels

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentImageModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentImageJoinModel(
    @Embedded
    val content: ContentModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "content_id"
    )
    val images: MutableList<ContentImageModel>
): Parcelable
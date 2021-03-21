package co.harismiftahulhudha.alodoktertest.database.dao

import androidx.room.*
import co.harismiftahulhudha.alodoktertest.mvvm.joinmodels.ContentImageJoinModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentImageModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentModel
import co.harismiftahulhudha.alodoktertest.utils.SortContents
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    fun getContents(userId: Int, query: String, sortContents: SortContents): Flow<MutableList<ContentImageJoinModel>> = when(sortContents) {
        SortContents.NEWEST -> getNewestContents(userId, query)
        SortContents.OLDEST -> getOldestContents(userId, query)
    }

    @Transaction
    @Query("SELECT * FROM content_table WHERE user_id = :userId AND description LIKE '%' || :query || '%' ORDER BY id DESC")
    fun getNewestContents(userId: Int, query: String): Flow<MutableList<ContentImageJoinModel>>

    @Transaction
    @Query("SELECT * FROM content_table WHERE user_id = :userId AND description LIKE '%' || :query || '%' ORDER BY id ASC")
    fun getOldestContents(userId: Int, query: String): Flow<MutableList<ContentImageJoinModel>>

    @Query("SELECT * FROM content_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastContent(): ContentModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(content: ContentModel)

    @Update
    suspend fun update(content: ContentModel)

    @Delete
    suspend fun delete(content: ContentModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ContentImageModel)

    @Query("DELETE FROM content_image_table WHERE content_id = :contentId")
    suspend fun deleteContentImages(contentId: Int)
}
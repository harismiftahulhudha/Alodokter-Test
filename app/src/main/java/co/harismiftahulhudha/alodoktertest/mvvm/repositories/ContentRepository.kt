package co.harismiftahulhudha.alodoktertest.mvvm.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import co.harismiftahulhudha.alodoktertest.database.dao.ContentDao
import co.harismiftahulhudha.alodoktertest.mvvm.joinmodels.ContentImageJoinModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentImageModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentModel
import co.harismiftahulhudha.alodoktertest.utils.SortPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class ContentRepository @Inject constructor(
    private val dao: ContentDao
): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun getContents(userId: Int, searchQuery: Flow<String>, preferencesFlow: Flow<SortPreferences>): LiveData<MutableList<ContentImageJoinModel>> {
        val contentsFlow = combine(searchQuery, preferencesFlow) { query, sortPreferences ->
            Pair(query, sortPreferences)
        }.flatMapLatest { (query, sortPreferences) ->
            dao.getContents(userId, query, sortPreferences.sortContents)
        }

        return contentsFlow.asLiveData()
    }

    suspend fun insert(content: ContentModel, images: MutableList<String>) {
        dao.insert(content)
        val newContent = dao.getLastContent()
        images.forEach { path ->
            dao.insertImage(ContentImageModel(path, newContent.id))
        }
    }

    suspend fun updateContent(contentImageJoinModel: ContentImageJoinModel) {
        contentImageJoinModel.apply {
            dao.update(content)
            dao.deleteContentImages(content.id)
            images.forEach { image ->
                dao.insertImage(image)
            }
        }
    }

    suspend fun delete(contentImageJoinModel: ContentImageJoinModel) {
        withContext(Dispatchers.IO) {
            contentImageJoinModel.apply {
                dao.deleteContentImages(content.id)
                dao.delete(content)
            }
        }
    }
}
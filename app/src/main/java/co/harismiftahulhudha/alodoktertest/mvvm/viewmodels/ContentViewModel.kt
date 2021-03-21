package co.harismiftahulhudha.alodoktertest.mvvm.viewmodels

import androidx.lifecycle.*
import co.harismiftahulhudha.alodoktertest.BuildConfig
import co.harismiftahulhudha.alodoktertest.mvvm.joinmodels.ContentImageJoinModel
import co.harismiftahulhudha.alodoktertest.mvvm.repositories.ContentRepository
import co.harismiftahulhudha.alodoktertest.mvvm.views.fragments.HomeFragment
import co.harismiftahulhudha.alodoktertest.utils.SessionManager
import co.harismiftahulhudha.alodoktertest.utils.SortContents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val sessionManager: SessionManager,
    state: SavedStateHandle
): ViewModel() {

    val searchQuery = state.getLiveData(SEARCH_QUERY, "")
    val preferencesFlow = sessionManager.preferencesFlow

    val isNewest = state.get<Boolean>("isNewest") ?: true
    val contentImageJoinModel = state.get<ContentImageJoinModel>("contentImageJoinModel")
    val detailModel = state.get<ContentImageJoinModel>("detailModel")

    fun updateIsContentScrollToTop(isContentScrollToTop: Boolean = false) = viewModelScope.launch {
        sessionManager.updateIsContentScrollToTop(isContentScrollToTop)
    }

    private val contentsEventChannel = Channel<ContentEvent>()
    val contentsEvent = contentsEventChannel.receiveAsFlow()

    fun onSort(sortContents: SortContents) = viewModelScope.launch {
        sessionManager.updateSortContents(sortContents)
    }

    fun logout() = viewModelScope.launch {
        sessionManager.logout()
    }

    fun getContents(userId: Int, ): LiveData<MutableList<ContentImageJoinModel>> {
        return contentRepository.getContents(userId, searchQuery.asFlow(), preferencesFlow)
    }

    fun deleteContent() = viewModelScope.launch {
        contentRepository.delete(contentImageJoinModel!!)
    }

    fun onClickContent(model: ContentImageJoinModel) = viewModelScope.launch {
        contentsEventChannel.send(ContentEvent.NavigateToDetail(model))
    }

    fun onLongClickContent(model: ContentImageJoinModel) = viewModelScope.launch {
        contentsEventChannel.send(ContentEvent.NavigateToOption(model))
    }

    fun onClickDeleteContent() = viewModelScope.launch {
        contentsEventChannel.send(ContentEvent.DeleteContent(contentImageJoinModel!!))
    }

    sealed class ContentEvent {
        data class NavigateToOption(val contentImageJoin: ContentImageJoinModel): ContentEvent()
        data class NavigateToDetail(val contentImageJoin: ContentImageJoinModel): ContentEvent()
        data class DeleteContent(val contentImageJoin: ContentImageJoinModel): ContentEvent()
    }

    companion object {
        val SEARCH_QUERY = "${BuildConfig.APPLICATION_ID}_${HomeFragment::class.java.simpleName}_SEARCH_QUERY"
    }
}
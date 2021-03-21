package co.harismiftahulhudha.alodoktertest.mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.harismiftahulhudha.alodoktertest.mvvm.repositories.CustomGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import id.haris.galleryapplication.CustomGalleryFolderModel
import id.haris.galleryapplication.CustomGalleryModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomGalleryFolderViewModel @Inject constructor(
    private val repository: CustomGalleryRepository,
    private val state: SavedStateHandle
): ViewModel() {

    fun getFolders() = viewModelScope.launch {
        repository.getFolders()
    }

    fun getFiles(bucketId: String, selectedFiles: MutableList<String> = ArrayList()) = viewModelScope.launch {
        repository.getFiles(bucketId, selectedFiles)
    }

    fun files(): LiveData<MutableList<CustomGalleryModel>> = repository.files
    fun folders(): LiveData<MutableList<CustomGalleryFolderModel>> = repository.folders
    fun folderNames(): LiveData<MutableList<String>> = repository.folderNames
}
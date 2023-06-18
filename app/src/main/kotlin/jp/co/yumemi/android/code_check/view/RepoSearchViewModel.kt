package jp.co.yumemi.android.code_check.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.repository.GithubItemRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model class for handling GitHub repository search functionality.
 *
 * This view model is responsible for handling the search logic and holding the search results.
 *
 */
@HiltViewModel
class RepoSearchViewModel @Inject constructor(private val itemRepository: GithubItemRepository) : ViewModel() {

    private val _results = MutableLiveData<List<RepoItem>>()
    val results: LiveData<List<RepoItem>> get() = _results
    val searchQuery = MutableLiveData<String>()

    fun search() = viewModelScope.launch {
        try {
            val response = itemRepository.getGithubRepoInfo(searchQuery.value ?: "")
            _results.value = response?.items
        } catch (e: Exception) {
            Log.e("RepoSearchViewModel", "Search failed", e)
        }
    }
}
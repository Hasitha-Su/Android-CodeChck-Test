package jp.co.yumemi.android.code_check.ui.repoSearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.model.RepoItem
import jp.co.yumemi.android.code_check.repository.GithubItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uiMessage = MutableLiveData<String>()
    val uiMessage: LiveData<String> = _uiMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        _isLoading.value = false
    }

    fun startLoading() {
        _isLoading.value = true
    }

    private fun stopLoading() {
        _isLoading.value = false
    }

    fun search() = viewModelScope.launch {
        try {
            val response = withContext(Dispatchers.IO) {
                itemRepository.getGithubRepoInfo(searchQuery.value!!)
            }

            if (response == null || response.items.isEmpty()) {
                _uiMessage.postValue("No results found.")
            } else {
                _results.postValue(response.items)
            }

        } catch (e: Exception) {

            when (e) {
                is SocketTimeoutException -> {
                    _uiMessage.postValue("The request timed out. Please try again.")
                }
                else -> {
                    _uiMessage.postValue("Failed to load repositories")
                    Log.e("RepoSearchViewModel", "Search failed", e)
                }
            }
        } finally {
            stopLoading()
        }
    }
}
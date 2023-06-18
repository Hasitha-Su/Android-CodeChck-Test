package jp.co.yumemi.android.code_check.repository

import jp.co.yumemi.android.code_check.model.GithubRepoSearchResponse
import jp.co.yumemi.android.code_check.network.GithubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
/**
 * Repository class responsible for fetching GitHub repository data.
 *
 * This class acts as an intermediary between the ViewModel and the GitHub API service.
 * It provides methods to fetch repository data from the remote data source using the provided API service.
 *
 * @param itemApiService The service interface for communicating with the GitHub API.
 *
 */
class GithubItemRepository @Inject constructor(
    private val itemApiService: GithubApiService
) {
    suspend fun getGithubRepoInfo(query: String): GithubRepoSearchResponse? =
        withContext(Dispatchers.IO) {
            getGithubRepositoryList(query)
        }

    private suspend fun getGithubRepositoryList(query: String): GithubRepoSearchResponse? {
        val response = itemApiService.getItems(query)
        return if (response.isSuccessful) response.body() else null
    }
}
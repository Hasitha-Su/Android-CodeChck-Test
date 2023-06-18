package jp.co.yumemi.android.code_check.network

import jp.co.yumemi.android.code_check.constants.Constants
import jp.co.yumemi.android.code_check.model.GithubRepoSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Retrofit service interface for the GitHub API.
 *
 * This interface defines the endpoints and their corresponding HTTP methods to interact with the GitHub API.
 *
 */
interface GithubApiService {
    @Headers("Accept: application/vnd.github.v3+json")
    @GET(Constants.PATH_SEARCH)
    suspend fun getItems(@Query("q") query: String): Response<GithubRepoSearchResponse>
}

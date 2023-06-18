package jp.co.yumemi.android.code_check.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Represents the response data model for a search query on GitHub repositories.
 *
 * This data class is used to deserialize the response from the GitHub API search endpoint.
 *
 * @property totalCount The total count of repositories matching the search query.
 * @property incompleteResults Indicates if the search results are incomplete.
 * @property items The list of repository items matching the search query.
 *
 * @constructor Creates an instance of the GithubRepoSearchResponse data class.
 *
 */
@Parcelize
data class GithubRepoSearchResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("incomplete_results") val incompleteResults: Boolean,
    @SerializedName("items") val items: List<RepoItem>
) : Parcelable
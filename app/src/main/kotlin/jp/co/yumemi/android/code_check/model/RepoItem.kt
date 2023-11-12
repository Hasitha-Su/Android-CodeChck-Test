package jp.co.yumemi.android.code_check.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Represents a GitHub repository item.
 *
 * This data class is used to deserialize the information of a single GitHub repository from the API response.
 *
 */
@Parcelize
data class RepoItem(
    @SerializedName("name") val name: String,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val stargazersCount: Long,
    @SerializedName("watchers_count") val watchersCount: Long,
    @SerializedName("forks_count") val forksCount: Long,
) : Parcelable
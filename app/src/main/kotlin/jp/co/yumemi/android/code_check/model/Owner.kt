package jp.co.yumemi.android.code_check.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Represents the owner of a GitHub repository.
 *
 * This data class is used to deserialize the owner information of a GitHub repository from the API response.
 *
 * @property avatarUrl The URL of the avatar image for the owner.
 *
 * @constructor Creates an instance of the Owner data class.
 *
 */
@Parcelize
data class Owner(
    @SerializedName("avatar_url") val avatarUrl: String
) : Parcelable
package jp.co.yumemi.android.code_check.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

/**
 * Utility class for checking the availability of a network connection.
 *
 * @property connectivityManager An instance of [ConnectivityManager] injected by the constructor.
 *                               It is used to query network capabilities and network state information.
 */
class NetworkUtils @Inject constructor(private val connectivityManager: ConnectivityManager) {

    /**
     * Determines whether there is an active network connection and if it is capable of
     * accessing the Internet.
     *
     * This method uses [ConnectivityManager.getNetworkCapabilities] to query the active
     * network for its capabilities and checks if it has the capability [NetworkCapabilities.NET_CAPABILITY_INTERNET].
     *
     * @return A [Boolean] indicating whether a network connection is available and is connected to the Internet.
     */
    fun isNetworkAvailable(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
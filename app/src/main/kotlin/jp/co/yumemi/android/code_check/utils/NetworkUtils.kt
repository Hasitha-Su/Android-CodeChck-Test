package jp.co.yumemi.android.code_check.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

/**
 * Utility class for monitoring network connectivity status.
 *
 * This class provides functionality to check for network availability and observe network
 * connectivity changes. It uses [ConnectivityManager] to register a network callback and
 * updates a LiveData accordingly.
 *
 * @param connectivityManager The [ConnectivityManager] used to access network information.
 */
@RequiresApi(Build.VERSION_CODES.N)
class NetworkUtils @Inject constructor(private val connectivityManager: ConnectivityManager) {

    private val _networkStatus = MutableLiveData<Boolean>()

    /**
     * LiveData to observe network connectivity changes.
     * Emits `true` when network is available and `false` when it is not.
     */
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _networkStatus.postValue(true)
        }

        override fun onLost(network: Network) {
            _networkStatus.postValue(false)
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    /**
     * Checks if an active network connection is available and capable of accessing the Internet.
     *
     * @return `true` if a network connection is available and connected to the Internet, `false` otherwise.
     */
    fun isNetworkAvailable(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    /**
     * Unregisters the network callback to prevent memory leaks.
     * Should be called when the utility is no longer needed or the lifecycle it's attached to is destroyed.
     */
    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

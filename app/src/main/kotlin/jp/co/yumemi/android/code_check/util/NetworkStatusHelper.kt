package jp.co.yumemi.android.code_check.util

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.MutableLiveData

class NetworkStatusHelper(private val connectivityManager: ConnectivityManager) {

    val networkStatus = MutableLiveData<Boolean>()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkStatus.postValue(true)
        }

        override fun onLost(network: Network) {
            networkStatus.postValue(false)
        }
    }

}


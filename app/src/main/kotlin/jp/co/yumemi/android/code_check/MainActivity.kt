/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.utils.MessageUtils
import jp.co.yumemi.android.code_check.utils.NetworkUtils
import javax.inject.Inject

/**
 * Main activity for the application.
 *
 * This activity is responsible for hosting the primary user interface. On creation, it checks
 * the network availability and displays a network error dialog if there is no active internet connection.
 * This dialog can direct users to the device's network settings if they need to establish a connection.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkUtils: NetworkUtils
    private var networkErrorDialog: AlertDialog? = null

    /**
     * Checks for network availability on activity creation and displays an error dialog if
     * the network is unavailable.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!networkUtils.isNetworkAvailable()) {
            showNetworkErrorDialog()
        }
    }

    /**
     * Displays a network error dialog with options to open network settings or dismiss the dialog.
     * The dialog will only be shown if it's not already being displayed.
     */
    private fun showNetworkErrorDialog() {
        if (networkErrorDialog?.isShowing != true) {
            networkErrorDialog = MessageUtils.showNetworkErrorDialog(
                context = this,
                onSettingsClick = {
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                },
                onDismiss = {
                    networkErrorDialog?.dismiss()
                }
            )
        }
    }
}

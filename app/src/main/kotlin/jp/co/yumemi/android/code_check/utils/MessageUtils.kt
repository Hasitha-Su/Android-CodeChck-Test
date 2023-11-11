package jp.co.yumemi.android.code_check.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import jp.co.yumemi.android.code_check.R

/**
 * Utility class for displaying common user interface messages and dialogs.
 *
 * This class provides static methods to display various types of dialogs,
 * such as error dialogs and network error dialogs, with customizable parameters
 * for title, message, and actions.
 */
class MessageUtils {
    companion object {

        /**
         * Shows a customizable error dialog.
         *
         * This method creates and displays a dialog with a specified title, message,
         * and action buttons. The dialog appearance and actions are customizable
         * based on the provided parameters.
         *
         * @param context The context in which the dialog should be shown.
         * @param title The title of the dialog.
         * @param message The message to be displayed in the dialog.
         * @param positiveButtonText The text for the positive button.
         * @param onPositiveButtonClick The action to be performed when the positive button is clicked.
         * @param negativeButtonText The text for the negative button.
         * @param onNegativeButtonClick The action to be performed when the negative button is clicked.
         * @param icon The icon to be displayed in the dialog; defaults to a standard error icon.
         * @return The displayed AlertDialog instance.
         */
        fun showErrorDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String,
            onPositiveButtonClick: () -> Unit,
            negativeButtonText: String,
            onNegativeButtonClick: () -> Unit,
            icon: Int = android.R.drawable.ic_dialog_alert
        ): AlertDialog {
            return AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText) { _, _ -> onPositiveButtonClick() }
                .setNegativeButton(negativeButtonText) { _, _ -> onNegativeButtonClick() }
                .setIcon(icon)
                .setCancelable(false)
                .show()
        }

        /**
         * Shows a predefined network error dialog.
         *
         * This method creates and displays a network error dialog with a default title
         * and message informing the user of a network connectivity issue. It offers
         * options to either open network settings or dismiss the dialog.
         *
         * @param context The context in which the dialog should be shown.
         * @param onSettingsClick The action to be performed when the "Open Settings" button is clicked.
         * @param onDismiss The action to be performed when the dialog is dismissed.
         * @return The displayed AlertDialog instance.
         */
        fun showNetworkErrorDialog(
            context: Context,
            onSettingsClick: () -> Unit,
            onDismiss: () -> Unit
        ): AlertDialog {
            return showErrorDialog(
                context = context,
                title = context.getString(R.string.network_error_title),
                message = context.getString(R.string.network_error_message),
                positiveButtonText = context.getString(R.string.open_settings),
                onPositiveButtonClick = onSettingsClick,
                negativeButtonText = context.getString(R.string.dismiss),
                onNegativeButtonClick = onDismiss
            )
        }
    }
}

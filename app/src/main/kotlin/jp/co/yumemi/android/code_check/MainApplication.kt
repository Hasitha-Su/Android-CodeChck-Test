package jp.co.yumemi.android.code_check

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The main application class for the Android application.
 *
 * This class serves as the entry point of the application and is responsible for initializing various components and configurations.
 * It extends the base `Application` class provided by the Android framework.
 *
 * Use the `@HiltAndroidApp` annotation to enable Hilt dependency injection for the application.
 *
 *
 * @constructor Creates an instance of the MainApplication class.
 */
@HiltAndroidApp
class MainApplication : Application()

package com.masarnovsky.big

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the BIG app.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class BigApplication : Application()

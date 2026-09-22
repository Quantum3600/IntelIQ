package com.techbrains.inteliq

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val defaultBaseUrl: String = "http://10.0.2.2:8080"
}

actual fun getPlatform(): Platform = AndroidPlatform()

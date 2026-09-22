package com.techbrains.inteliq

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val defaultBaseUrl: String = "http://localhost:8080"
}

actual fun getPlatform(): Platform = IOSPlatform()

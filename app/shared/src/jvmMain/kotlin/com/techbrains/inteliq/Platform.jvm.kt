package com.techbrains.inteliq

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val defaultBaseUrl: String = "http://localhost:8080"
}

actual fun getPlatform(): Platform = JVMPlatform()

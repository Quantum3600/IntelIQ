package com.techbrains.inteliq

interface Platform {
    val name: String
    val defaultBaseUrl: String
}

expect fun getPlatform(): Platform

package com.techbrains.inteliq

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
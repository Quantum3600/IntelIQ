package com.techbrains.inteliq

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val defaultBaseUrl: String = "http://localhost:8080"
}

actual fun getPlatform(): Platform = WasmPlatform()

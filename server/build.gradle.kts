plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
}

group = "com.techbrains.inteliq"
version = "1.0.0"

application {
    mainClass = "com.techbrains.inteliq.ApplicationKt"
}

dependencies {
    api(project(":core"))

    implementation(libs.logback)

    // Ktor
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serializationKotlinxJson)

    // Serialization
    implementation(libs.kotlinxSerializationJson)

    // MongoDB
    implementation(libs.mongodbDriver)

    // Tests
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
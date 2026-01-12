// Project-level build.gradle fayli

plugins {
    // Android Application & Library pluginlari
    alias(libs.plugins.android.application) apply false

    // Kotlin pluginlari (Android va Compose uchun)
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Dependency Injection (Hilt)
    alias(libs.plugins.hilt) apply false

    // Google Services (Firebase uchun)
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Firebase Crashlytics (Agar toml'da bo'lsa alias ishlating, bo'lmasa quyidagicha)
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}
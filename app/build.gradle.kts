plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "dev.samandar.walletapp"
    compileSdk = 35

    ndkVersion = "28.0.12433566"


    defaultConfig {
        applicationId = "dev.samandar.walletapp"
        minSdk = 24
        targetSdk = 35 // Android 15 qo'llab-quvvatlash
        versionCode = 33 // Google Console uchun doim oshirib boring
        versionName = "1.28"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(providers.gradleProperty("STORE_FILE").get())
            storePassword = providers.gradleProperty("STORE_PASSWORD").get()
            keyAlias = providers.gradleProperty("KEY_ALIAS").get()
            keyPassword = providers.gradleProperty("KEY_PASSWORD").get()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    bundle {
        language.enableSplit = false
    }

    packaging {
        resources {
            // Dublikat bo'lishi mumkin bo'lgan barcha litsenziya fayllari
            excludes += "META-INF/LGPL-2.1.md"
            excludes += "META-INF/LGPL-2.1.txt"
            excludes += "META-INF/MPL-2.0.txt"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/NOTICE.md"

            // Agar yana shunga o'xshash xato chiqsa, barcha litsenziyalarni rad etish:
            excludes += "META-INF/LICENSE*"
            excludes += "META-INF/NOTICE*"
            excludes += "META-INF/ASL2.0"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    // --- Compose & UI ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.compose.ui.text)
    debugImplementation(libs.androidx.ui.tooling)
    implementation("androidx.compose.material:material-icons-extended")

    // --- Core & Lifecycle ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // --- Navigation & Hilt ---
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    // --- Database & Storage ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.datastore.preferences)

    // --- Network ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // --- WorkManager ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Utilities (Lottie, Coil, Accompanist) ---
    implementation(libs.lottie.compose)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)


    val camerax_version = "1.4.1"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")
    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")

    // Guava (CameraX ishlashi uchun ba'zida kerak bo'ladi)
    implementation ("com.google.guava:guava:32.1.3-android")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.google.mlkit:barcode-scanning:17.3.0") // Bu yaxshi, lekin toml-dan kelsa yaxshiroq


    // --- Google Services ---
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.material3)

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.google.firebase:firebase-analytics")

    implementation("com.github.librepdf:openpdf:1.3.30")

    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")



}
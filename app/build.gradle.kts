plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.walletapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.walletapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
}

dependencies {
    // --- 🧩 Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.common.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.hilt.common)
    implementation(libs.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    debugImplementation(libs.androidx.ui.tooling)
    implementation("androidx.compose.material:material-icons-extended")

    // --- 🔄 Lifecycle & Activity ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- 🧭 Navigation (Compose) ---
    implementation("androidx.navigation:navigation-compose:2.8.3")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    // implementation(libs.firebase.firestore.ktx) // <--- ESKI, XATOLIK KELTIRGAN QATOR O'CHIRILADI
    implementation("com.google.firebase:firebase-firestore-ktx") // <--- YANGI: Versiya raqamisiz yozildi

    // --- 💉 Hilt (Dependency Injection) ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- 🗂️ DataStore ---
    implementation("androidx.datastore:datastore-preferences:1.1.0-alpha06")

    // --- 🧱 Room (Local Database) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    testImplementation("androidx.room:room-testing:2.6.1")

    // --- 🌐 Retrofit & Networking ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // --- 🎞️ Lottie (Animations) ---
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // --- 🖼️ Coil (Image Loader) ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- 🌈 System UI Controller ---
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // --- ⚙️ AndroidX qo‘shimchalar ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)

    // --- 📡 Google Services (masalan, joylashuv) ---
    implementation(libs.play.services.location)

    // --- 🧪 Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0") // so‘nggi




}

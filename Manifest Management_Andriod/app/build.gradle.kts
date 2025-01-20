plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin") version "2.5.0"
    kotlin("kapt")
}

android {
    namespace = "com.sko.manifestmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sko.manifestmanagement"
        minSdk = 24
        targetSdk = 34
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
        viewBinding=true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")  // or the latest version
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation ("com.auth0.android:jwtdecode:2.0.0")
    implementation ("com.auth0.android:jwtdecode:2.0.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("androidx.room:room-ktx:2.6.1")
    // Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Lifecycle Scope
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    kapt("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.1") // For Kotlin
    // ContextCompat for permission handling
    implementation ("androidx.core:core-ktx:1.10.0")
    // For Gson
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation ("com.auth0.android:jwtdecode:2.0.0")
    implementation ("com.auth0.android:jwtdecode:2.0.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0") // or the appropriate version
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")  // Barcode Scanner
    implementation("androidx.camera:camera-core:1.4.0")  // CameraX Core
    implementation("androidx.camera:camera-view:1.4.0")  // CameraX View
    implementation("androidx.camera:camera-lifecycle:1.4.0")





}
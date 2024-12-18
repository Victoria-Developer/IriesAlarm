plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.iries.youtubealarm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.iries.youtubealarm"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.addAll(
                arrayOf(
                    "x86", "x86_64",
                    "armeabi-v7a", "arm64-v8a"
                )
            )
        }

        splits {
            abi {
                isEnable = true
                reset()
                include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
                isUniversalApk = true
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Coil
    implementation(libs.coil.compose)

    // Fancy scrollable timePicker
    implementation (libs.wheelpickercompose)

    // Livedata observeAsState
    implementation (libs.androidx.runtime.livedata)

    // Firebase dependencies for sha-1
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)


    // Google Sign-In
    implementation(libs.play.services.auth)
    // YouTube api
    implementation(libs.google.api.client.android)
    implementation("com.google.apis:google-api-services-youtube:v3-rev20240123-2.0.0") {
        exclude("org.apache.httpcomponents")
    }

    // Json, Gson
    implementation(libs.google.http.client.gson)
    implementation(libs.json.io)
    implementation(libs.gson)

    // mp3 converter
    implementation(libs.library)
    implementation(libs.ffmpeg)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    // Compose navigation
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
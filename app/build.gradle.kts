plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.chefapp"
    compileSdk = 35

    defaultConfig {
        vectorDrawables.useSupportLibrary=true
        applicationId = "com.example.chefapp"
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
}

dependencies {
    val fragment_version = "1.6.1"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.fragment:fragment:$fragment_version")
    implementation("com.google.android.material:material:1.12.0")
    // Dodanie Retrofit
    implementation(libs.retrofit)

    // Dodanie Retrofit Converter Gson
    implementation(libs.retrofit.converter.gson)

    // Dodanie OkHttp
    implementation(libs.okhttp)

    // Dodanie OkHttp Logging Interceptor
    implementation(libs.okhttp.logging.interceptor)

    // Dodanie Gson
    implementation(libs.gson)
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventhub"

    // API 36 is the current requirement for your dependencies
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eventhub"

        // FIXED: Must be at least 23 to support navigationevent library
        minSdk = 23

        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val localProperties = java.util.Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(java.io.FileInputStream(localPropertiesFile))
        }
        val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: "YOUR_API_KEY_HERE"
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    // Ensures Java and Kotlin both use bytecode version 21
    kotlin {
        jvmToolchain(21)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx") // Optional: Can remove if only using Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // GSON (For Event storage)
    implementation("com.google.code.gson:gson:2.13.2")
    // Core Android Libraries
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Compose (Using BOM from your libs.versions.toml)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)


    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Generative AI (Gemini) for Chatbot
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Maps (OSM) and Location
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("com.google.android.gms:play-services-location:21.3.0")
}
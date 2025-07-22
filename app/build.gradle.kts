import org.gradle.kotlin.dsl.implementation


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.pizzazone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pizzazone"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.lottie)
    implementation(libs.glide)
    implementation(libs.gson)
    // Lottie library Dependency - නවතම stable version එක භාවිතා කරන්න
    implementation("com.airbnb.android:lottie:6.4.1") // 2025 ජූලි වන විට 6.4.1 නවතම stable version එකයි.

        // ... existing dependencies

        // Firebase Storage
        implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // නවතම BOM version එක පාවිච්චි කරන්න
        implementation("com.google.firebase:firebase-storage-ktx")

        // Glide for image loading (පින්තූර load කිරීමට)
    implementation("com.github.bumptech.glide:glide:4.16.0") // නවතම stable version එක
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0") // MPAndroidChart for charts
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")












}
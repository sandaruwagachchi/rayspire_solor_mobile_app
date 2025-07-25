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
    // Import the Firebase BoM to manage Firebase library versions
    implementation(platform(libs.firebase.bom))

    // AndroidX and Material Design dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase dependencies (versions are now managed by the BoM)
    // IMPORTANT: Use the *non-KTX* versions as KTX functionality is now in main modules.
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore) // Using the non-KTX alias
    implementation(libs.firebase.storage)   // Using the non-KTX alias
    implementation(libs.google.firebase.auth)
    // Credentials and Google ID
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Other libraries
    implementation(libs.litert.support.api)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // UI/Utility libraries
    implementation(libs.lottie) // Using the alias from libs.versions.toml
    implementation(libs.glide)
    implementation(libs.gson)

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // The explicit Lottie version can be removed if libs.lottie points to the desired version.
    // Keeping it here for now if you intend to override the libs.versions.toml entry,
    // but typically you'd just use libs.lottie.
    // implementation("com.airbnb.android:lottie:6.4.1")
}

apply(plugin = "com.google.gms.google-services")
import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services) // මෙය plugin එකක් ලෙස උඩම තිබිය යුතුයි
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
    // ඔබ දැනටමත් මෙය භාවිතා කරන නිසා, අලුත්ම stable version එක යොදන්න
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
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Google Sign-in Library
    // මෙය අත්‍යවශ්‍යයි Google Sign-in සඳහා. ඔබගේ libs.versions.toml හි playServicesAuth යනුවෙන් alias එකක් ඇත්දැයි පරීක්ෂා කරන්න.
    // නැතිනම්, මෙය අතින් එකතු කරන්න.
    implementation("com.google.android.gms:play-services-auth:21.0.0") // 2025-07-26 දිනට අදාල අලුත්ම stable version එක යොදන්න.
    // ඔබට libs.play.services.auth වැනි alias එකක් තිබේ නම්, එය භාවිතා කරන්න.

    // Credentials and Google ID (ඔබ දැනටමත් මේවා එකතු කර ඇති නිසා වෙනස්කම් අවශ්‍ය නැත)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth) // මෙයත් play services auth සඳහා අවශ්‍ය වේ.
    implementation(libs.googleid)

    // Other libraries
    implementation(libs.litert.support.api)
    implementation(libs.firebase.ai) // මෙය නිවැරදි dependency name එකක්දැයි පරීක්ෂා කරන්න. Litert.support.api සාමාන්‍ය දෙයක් නොවේ.

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // UI/Utility libraries
    implementation(libs.lottie)
    implementation(libs.glide)
    implementation(libs.gson)

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // The explicit Lottie version can be removed if libs.lottie points to the desired version.
    // implementation("com.airbnb.android:lottie:6.4.1") // මෙය අතිරික්ත නම් ඉවත් කරන්න
}

// Google Services plugin එකේ apply() method එක plugins block එක තුළට ගෙන යන්න.
// මෙය Kotlin DSL එකේදී plugins block එක තුළටම දැමීම වඩාත් හොඳ පුරුද්දකි.
// apply(plugin = "com.google.gms.google-services")
plugins {
    // Android Plugin
    alias(libs.plugins.android.application)

    // Dependency Injection (Hilt)
    alias(libs.plugins.hilt)

    // Kotlin Plugins
    alias(libs.plugins.kotlin.parcelize)
    id("com.google.devtools.ksp")

}


android {
    namespace = "com.nz.coloringgamesforkidsdoodle.drawingkids.painting"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.nz.coloringgamesforkidsdoodle.drawingkids.painting"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}


dependencies {

    // ==============================
    // 🧱 Core Android Libraries
    // ==============================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)


    // ==============================
    // 🧪 Testing
    // ==============================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ==============================
    // 🔄 Lifecycle (ViewModel + LiveData)
    // ==============================
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    // ==============================
    // 🎨 UI / UX Libraries
    // ==============================
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.dots.indicator)
    implementation(libs.lottie)
    implementation(libs.shimmer)
    implementation(libs.power.menu)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // ==============================
    // 💉 Dependency Injection (Hilt)
    // ==============================
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // ==============================
    // 🔧 Background Work
    // ==============================
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.common)


    // animation
    implementation("com.daimajia.androidanimations:library:2.4@aar")


    // ==============================
    // ADS SDK
    // ==============================
    implementation(libs.firebaseConfig)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // ==============================
    // Colors by numbers
    // ==============================
    implementation("com.otaliastudios:zoomlayout:1.9.0")
    implementation("com.github.guilhe:circular-progress-view:2.0.0")
    implementation("com.caverock:androidsvg-aar:1.4")
    implementation(libs.konfetti.xml)

    // ----------------------------
    // 🗄️ Data & Networking
    // ----------------------------
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    // ----------------------------
    // 🏠 Room Database
    // ----------------------------
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)



}
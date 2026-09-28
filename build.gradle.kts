// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {

    //---------
    // Android Plugins
    //---------
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    //---------
    // Dependency Injection (Hilt)
    //---------
    alias(libs.plugins.hilt) apply false

    //---------
    // Kotlin Plugins
    //---------
    alias(libs.plugins.kotlin.parcelize) apply false
    id("com.google.devtools.ksp") version "2.3.3" apply false


}
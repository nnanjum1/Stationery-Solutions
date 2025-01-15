// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google()  // Ensure Google repository is included
        mavenCentral()
    }

    dependencies {
        // Use the latest version of AGP and Firebase services plugin
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

allprojects {
    repositories {
        google()  // Ensure Google repository is included
        mavenCentral()
    }
}

// settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()  // Move Google repository here
        mavenCentral()
    }
}


rootProject.name = "Stationery Solutions"
include(":app")
 
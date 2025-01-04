// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val agpVersion by extra("8.3.2") // Use AGP 8.3.2, latest available for now.
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
    }
}

plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}



plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.medioman"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medioman"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        // Exclude duplicate META-INF/DEPENDENCIES file
        resources {
            // Use the add method to correctly add exclusions
            excludes.add("META-INF/DEPENDENCIES")
        }
    }

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("androidx.fragment:fragment:1.8.5")

    // Firebase BoM for version management
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Libraries (choose one for the database)
    implementation("com.google.firebase:firebase-database-ktx") // Use if using Kotlin
    implementation("com.google.firebase:firebase-database") // Use if using Java

    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)


    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.13.0")
    implementation("com.google.api:gax:2.11.0")
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")


// Firebase Storage
    implementation ("com.google.firebase:firebase-storage:20.2.0")

    // iText library for PDF generation
    implementation ("com.itextpdf:itext7-core:7.2.5")

    // MPAndroidChart Library for charts (BarChart, PieChart, etc.)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.github.chrisbanes:PhotoView:2.3.0")



    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
}

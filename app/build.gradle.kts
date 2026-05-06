import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.servicerca.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.servicerca.app"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Leer credenciales SMTP preferiblemente desde local.properties (NO subir al repositorio).
        // Si no existe, también se aceptan properties en gradle.properties o -P en la línea de comandos.
        val localProps = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            localProps.load(localFile.inputStream())
        }

        val smtpUser: String? = (localProps.getProperty("smtpUser") ?: project.findProperty("smtpUser")) as? String
        val smtpPassword: String? = (localProps.getProperty("smtpPassword") ?: project.findProperty("smtpPassword")) as? String
        val smtpHost: String? = (localProps.getProperty("smtpHost") ?: project.findProperty("smtpHost")) as? String
        val smtpPort: String? = (localProps.getProperty("smtpPort") ?: project.findProperty("smtpPort")) as? String
        val smtpFrom: String? = (localProps.getProperty("smtpFrom") ?: project.findProperty("smtpFrom")) as? String

        buildConfigField("String", "SMTP_USER", if (smtpUser != null) "\"$smtpUser\"" else "\"\"")
        buildConfigField("String", "SMTP_PASSWORD", if (smtpPassword != null) "\"$smtpPassword\"" else "\"\"")
        buildConfigField("String", "SMTP_HOST", if (smtpHost != null) "\"$smtpHost\"" else "\"smtp.gmail.com\"")
        buildConfigField("String", "SMTP_PORT", if (smtpPort != null) "\"$smtpPort\"" else "\"587\"")
        buildConfigField("String", "SMTP_FROM", if (smtpFrom != null) "\"$smtpFrom\"" else "\"no-reply@servicerca.app\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
        // Habilitar generación de BuildConfig para los campos SMTP configurados
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)

    // Firebase BOM y Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation("com.airbnb.android:lottie:6.7.1")
    implementation("com.airbnb.android:lottie-compose:6.7.1")
    // JavaMail para Android (solo para desarrollo / SMTP propio)
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    implementation("com.kizitonwose.calendar:compose:2.6.0")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.maps.android)
    implementation(libs.maps.compose)
    implementation("com.google.zxing:core:3.5.4")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:1.3.2")
    implementation("com.google.guava:guava:33.0.0-android")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // --- Hilt Core ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Hilt + Compose Navigation ---
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.data.store)
}

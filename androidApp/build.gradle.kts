import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// SEC-02: google-services.json is not in git (public repo). Without it the build still runs,
// but Firebase is not configured and sign-in/sync fail at runtime.
val hasGoogleServices = file("google-services.json").exists()
if (hasGoogleServices) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
} else {
    logger.warn("w: androidApp/google-services.json missing: building without Firebase config (download it from the Firebase console, project subtrack-f4dd3)")
}

// Upload key for Play (SEC-03): kept outside the repo, paths and passwords in local.properties.
// Without them the release builds unsigned and Gradle warns; see "Publicación" in CLAUDE.md.
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val uploadStoreFile: String? = localProps.getProperty("subtrack.signing.storeFile")

android {
    namespace = "com.gondroid.subtrack.app"
    compileSdk = 37

    defaultConfig {
        // Published identifier: never change it
        applicationId = "com.gondroid.subtrack"
        minSdk = 24
        targetSdk = 37
        versionCode = libs.versions.app.versionCode.get().toInt()
        versionName = libs.versions.app.versionName.get()
        // The google-services plugin generates this string; without the JSON, Google sign-in gets an empty client id
        if (!hasGoogleServices) resValue("string", "default_web_client_id", "")
    }

    signingConfigs {
        if (uploadStoreFile != null) {
            create("release") {
                storeFile = file(uploadStoreFile)
                storePassword = localProps.getProperty("subtrack.signing.storePassword")
                keyAlias = localProps.getProperty("subtrack.signing.keyAlias")
                keyPassword = localProps.getProperty("subtrack.signing.keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
                ?: null.also { logger.warn("w: subtrack.signing.* missing in local.properties: the release build is unsigned") }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = !hasGoogleServices
    }

    // Lint the shared library too: `./gradlew :androidApp:lintDebug` covers both modules
    lint {
        checkDependencies = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}

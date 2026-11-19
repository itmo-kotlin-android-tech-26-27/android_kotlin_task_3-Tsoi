plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.task_3"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.task_3"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "DEMO_DATA_FILE",
            "\"${providers.gradleProperty("demoDataFile").getOrElse("sports_sections.json")}\"",
        )
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)
    ksp(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

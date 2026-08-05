plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "rpt.com.hybridwalk"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "rpt.com.hybridwalk"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(project(":shared"))
}
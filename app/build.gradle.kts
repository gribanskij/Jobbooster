import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}




val dataPropertiesFile = rootProject.file("data.properties")
val dataProperties = Properties()
dataProperties.load(FileInputStream(dataPropertiesFile))

android {
    namespace = "com.gribansky.jobbooster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gribansky.jobbooster"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        buildConfigField("String", "clientId", "\"${dataProperties["clientId"]}\"" )
        buildConfigField("String", "clientSecret", "\"${dataProperties["clientSecret"]}\"" )
        buildConfigField("String", "appName", "\"${dataProperties["appName"]}\"" )
        buildConfigField("String", "email", "\"${dataProperties["email"]}\"" )
        buildConfigField("String", "resumeId", "\"${dataProperties["resumeId"]}\"" )
        buildConfigField("String", "accessToken", "\"${dataProperties["accToken"]}\"" )
        buildConfigField("String", "refreshToken", "\"${dataProperties["refToken"]}\"" )


    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.koin.android)
    implementation(libs.okhttp)
    implementation(libs.okio)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.json)
    implementation(libs.workmanager)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
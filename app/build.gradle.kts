import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
//    kotlin("dagger.hilt.android.plugin")
}


android {
    compileSdkVersion(Apps.compileSdk)
    defaultConfig {
        minSdkVersion(Apps.minSdk)
        targetSdkVersion(Apps.targetSdk)
        versionCode = Apps.versionCode
        versionName = Apps.versionName
        multiDexEnabled = true
        setProperty("archivesBaseName", "$applicationId-v$versionName($versionCode)")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    (kotlinOptions as KotlinJvmOptions).apply {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
//    implementation(project(":baseWebview"))
    implementation(project(":baseSecurity"))
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(Libs.kotlin)
    implementation(Libs.appcompat)
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("com.google.android.material:material:1.2.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation ("junit:junit:4.+")
    androidTestImplementation ("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.3.0")


    implementation(Libs.tedpermission)
    implementation(Libs.coroutines)
    implementation(Libs.activityKtx)
    implementation(Libs.fragmentKtx)
    implementation(platform(Firebase.bom))
    implementation(Firebase.config)
    implementation(Firebase.crashlytics)
    implementation(Firebase.analytics)
    implementation(Anko.common)
    implementation(Timber.common)
    implementation(Groupie.groupie)

    // Retrofit
    implementation (Retrofit.RETROFIT)
    implementation (Retrofit.CONVERTER_GSON)
    implementation (Retrofit.CONVERTER_JAXB)

    // dagger hilt
    implementation (Hilt.DAGGER_HILT)
    kapt (Hilt.DAGGER_HILT_COMPILER)
    implementation (Hilt.DAGGER_HILT_VIEW_MODEL)
    kapt (Hilt.DAGGER_HILT_ANDROIDX_COMPILER)

    //okHttp
    implementation (OkHttp.OKHTTP)
    implementation (OkHttp.LOGGING_INTERCEPTOR)

}
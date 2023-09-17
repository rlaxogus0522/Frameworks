import Versions.KOTEST_VERSION
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
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    (kotlinOptions as KotlinJvmOptions).apply {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    testOptions{
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project(":baseWebview"))
    implementation(project(":baseSecurity"))
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(Libs.kotlin)
    implementation(Libs.appcompat)
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("com.google.android.material:material:1.2.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation(project(mapOf("path" to ":baseWebview")))
    testImplementation ("junit:junit:")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:${KOTEST_VERSION}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${KOTEST_VERSION}")

    testImplementation ("com.squareup.okhttp3:mockwebserver:4.9.0")


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
    implementation (Retrofit.RxJava3CallAdapterFactory)

    // dagger hilt
    implementation (Hilt.DAGGER_HILT)
    kapt (Hilt.DAGGER_HILT_COMPILER)
    implementation (Hilt.DAGGER_HILT_VIEW_MODEL)
    kapt (Hilt.DAGGER_HILT_ANDROIDX_COMPILER)

    //okHttp
    implementation (OkHttp.OKHTTP)
    implementation (OkHttp.LOGGING_INTERCEPTOR)

}
import Versions.nav_version

object Apps {
    const val compileSdk = 32
    const val minSdk = 28
    const val targetSdk = 31
    const val versionCode = 101
    const val versionName = "1.0.1"
}

object Versions {
    const val gradle = "4.1.1"
    const val kotlin = "1.3.72"
    const val appcompat = "1.2.0"
    const val junit = "4.12"
    const val nav_version = "2.4.2"
    const val KOTEST_VERSION = "5.5.4"
}

object Libs {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val tedpermission = "gun0912.ted:tedpermission:2.2.3"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
    const val activityKtx = "androidx.activity:activity-ktx:1.3.1"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.6"
    const val splash = "androidx.core:core-splashscreen:1.0.0-beta02"
}

object Firebase {
    const val bom = "com.google.firebase:firebase-bom:30.3.1"
    const val core = "com.google.firebase:firebase-core:17.0.1"
    const val fireStore = "com.google.firebase:firebase-firestore:20.2.0"
    const val auth = "com.google.firebase:firebase-auth-ktx"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val config = "com.google.firebase:firebase-config:19.2.0"
    const val messaging = "com.google.firebase:firebase-messaging:19.0.1"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
}

object OkHttp {
    const val OKHTTP = "com.squareup.okhttp3:okhttp:4.9.1"
    const val LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:4.9.1"
}

object Retrofit {
    const val RETROFIT = "com.squareup.retrofit2:retrofit:2.9.0"
    const val RxJava3CallAdapterFactory = "com.squareup.retrofit2:adapter-rxjava3:2.9.0"
    const val CONVERTER_GSON = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val CONVERTER_JAXB = "com.squareup.retrofit2:converter-jaxb:2.9.0"
}

object Anko {
    const val common = "org.jetbrains.anko:anko:0.10.8"
}

object Timber {
    const val common = "com.jakewharton.timber:timber:4.7.1"
}


object Groupie {
    const val version = "2.7.0"
    const val groupie = "com.xwray:groupie:$version"
    const val databinding = "com.xwray:groupie-databinding:$version"
}


object Hilt {
    const val DAGGER_HILT = "com.google.dagger:hilt-android:2.38.1"
    const val DAGGER_HILT_COMPILER = "com.google.dagger:hilt-android-compiler:2.38.1"
    const val DAGGER_HILT_VIEW_MODEL = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
    const val DAGGER_HILT_ANDROIDX_COMPILER = "androidx.hilt:hilt-compiler:1.0.0"
}

object Navi {
    const val navigation_fragment = "androidx.navigation:navigation-fragment-ktx:$nav_version"
    const val navigation_ui = "androidx.navigation:navigation-ui-ktx:$nav_version"
}

object Image {
    const val glideImageView = "com.github.bumptech.glide:glide:4.11.0"
}


object TedImagePicker {
    val common = "gun0912.ted:tedimagepicker:1.0.8"
}
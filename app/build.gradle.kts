import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.navigation.safeargs)
}

val properties = Properties()
properties.load(FileInputStream("local.properties"))

android {
    namespace = "com.ilyeong.flickora"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ilyeong.flickora"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TMDB_API_KEY", properties.getProperty("TMDB_API_KEY"))
        buildConfigField("String", "ACCOUNT_ID", properties.getProperty("ACCOUNT_ID"))
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:model"))
    implementation(project(":core:data:data-oauth"))
    implementation(project(":core:data:data-user"))
    implementation(project(":core:data:data-movie"))
    implementation(project(":core:data:data-tv"))

    implementation(project(":feature:login"))
    implementation(project(":feature:home"))
    implementation(project(":feature:watchlist"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:genre"))
    implementation(project(":feature:search"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // serialization
    implementation(libs.kotlinx.serialization)

    // hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.android.compiler)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.serialization)

    // okhttp logging interceptor
    implementation(libs.logging.interceptor)

    // customtabs
    implementation(libs.androidx.browser)

    // datastore
    implementation(libs.androidx.datastore)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.okhttp)

    // recyclerview
    implementation(libs.recyclerview)

    // shimmer
    implementation(libs.shimmer)

    // paging
    implementation(libs.paging)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mmt.guitarlab"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.mmt.guitarlab"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    flavorDimensions += listOf("mode")

    productFlavors {
        create("standard") {
            dimension = "mode"
            buildConfigField("String", "FLAVOR_TYPE", "\"standard\"")
            buildConfigField("Boolean", "IS_TABS_FLAVOR", "false")
            resValue("string", "app_flavor_name", "GuitarLab")
        }
        create("tabs") {
            dimension = "mode"
            applicationIdSuffix = ".tabs"
            versionNameSuffix = "-tabs"
            buildConfigField("String", "FLAVOR_TYPE", "\"tabs\"")
            buildConfigField("Boolean", "IS_TABS_FLAVOR", "true")
            resValue("string", "app_flavor_name", "TabLab")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) // Match your target bytecode version (e.g., JVM_17 or JVM_21)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(project(":core:ui"))
    implementation(project(":core:audio"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:parser"))
    implementation(project(":core:data"))
    implementation(project(":core:tuxguitar"))
    implementation(project(":core:tuxguitar-engine"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:tab"))
    implementation(project(":feature:metronome"))
    implementation(project(":feature:tuner"))
    implementation(project(":feature:drums"))
    implementation(project(":feature:practice"))
    implementation(project(":feature:tuxguitar-browser"))
    implementation(project(":feature:tuxguitar-channels"))
    implementation(project(":feature:tuxguitar-preferences"))
    implementation(project(":feature:tuxguitar-editor"))
    implementation(project(":core:tuxguitar-android"))
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.google.android.material:material:1.14.0")
    implementation("org.apache.commons:commons-compress:1.28.0")

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

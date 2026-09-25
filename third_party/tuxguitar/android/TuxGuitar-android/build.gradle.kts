plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "app.tuxguitar.android"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("test") {
            java.setSrcDirs(listOf("test"))
            kotlin.setSrcDirs(listOf("test"))
        }
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.setSrcDirs(
                listOf(
                    "../../common/TuxGuitar-lib/src/main/java",
                    "../../common/TuxGuitar-editor-utils/src/main/java",
                    "../../common/TuxGuitar-gm-utils/src",
                    "../../common/TuxGuitar-compat/src/main/java",
                    "../../common/TuxGuitar-midi/src/main/java",
                    "../../common/TuxGuitar-gtp/src",
                    "../../common/TuxGuitar-gpx/src",
                    "../../common/TuxGuitar-ptb/src",
                    "./src",
                ),
            )
            kotlin.setSrcDirs(listOf("./src"))
            res.setSrcDirs(listOf("res"))
            resources.setSrcDirs(
                listOf(
                    "../../common/TuxGuitar-compat/share",
                    "../../common/TuxGuitar-midi/share",
                    "../../common/TuxGuitar-gtp/share",
                    "../../common/TuxGuitar-gpx/share",
                    "../../common/TuxGuitar-ptb/share",
                ),
            )
            assets.setSrcDirs(listOf("assets"))
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.0.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("org.apache.commons:commons-compress:1.26.0")
}

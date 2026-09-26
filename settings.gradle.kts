pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GuitarLab"
include(":app")
include(":core:ui")
include(":core:tuxguitar")
include(":core:tuxguitar-engine")
include(":feature:settings")
include(":feature:tab")
include(":feature:metronome")
include(":feature:tuner")
include(":feature:drums")
include(":feature:practice")
include(":feature:tuxguitar-browser")
include(":feature:tuxguitar-channels")
include(":feature:tuxguitar-preferences")
include(":tuxguitar-android")
project(":tuxguitar-android").projectDir =
    file("third_party/tuxguitar/android/TuxGuitar-android")

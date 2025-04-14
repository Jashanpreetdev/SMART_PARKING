pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "sk.eyJ1IjoiaGltYW5zaHUxMDA5IiwiYSI6ImNtOTRhczZyaTBhcmIybHNqcnJ6dTc0MnQifQ.NMcTFUjIgCR7xE4FcKmG1w"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

}

rootProject.name = "SMART PARKING"
include(":app")
 
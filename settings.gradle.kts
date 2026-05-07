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

rootProject.name = "Movieverse"

include(":app")

include(":core:model")
include(":core:ui")
include(":core:network")
include("core:datastore:datastore-user")
include(":core:data:data-user")
include(":core:data:data-movie")
include(":core:data:data-oauth")

include(":feature:detail")
include(":feature:genre")
include(":feature:login")
include(":feature:profile")
include(":feature:search")
include(":feature:home")
include(":feature:watchlist")
include(":datastore-user")

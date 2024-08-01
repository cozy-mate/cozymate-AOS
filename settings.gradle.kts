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
        maven ("https://jitpack.io")
        maven ("https://devrepo.kakao.com/nexus/content/groups/public/") // 카카오 로그인
    }
}

rootProject.name = "Cozymate"
include(":app")

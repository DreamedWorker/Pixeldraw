pluginManagement {
    repositories {
//        google()
//        mavenCentral()
//        gradlePluginPortal()
        maven ( url = "https://jitpack.io" )
        maven ( url = "https://maven.aliyun.com/repository/public")
        maven ( url = "https://maven.aliyun.com/repository/jcenter")
        maven ( url = "https://maven.aliyun.com/repository/google")
        maven ( url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven ( url = "https://maven.aliyun.com/repository/gradle-core")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        google()
//        mavenCentral()
        maven ( url = "https://jitpack.io" )
        maven ( url = "https://maven.aliyun.com/repository/public")
        maven ( url = "https://maven.aliyun.com/repository/jcenter")
        maven ( url = "https://maven.aliyun.com/repository/google")
        maven ( url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven ( url = "https://maven.aliyun.com/repository/gradle-core")
    }
}
rootProject.name = "Pixeldraw"
include (":app")

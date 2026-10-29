pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "kotlin-101-labs"
include(":ch01-values-and-types")
include(":ch02-control-flow-and-functions")
include(":ch03-null-safety-and-errors")
include(":ch04-classes-and-modeling")
include(":ch05-collections-and-complexity")

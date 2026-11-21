plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

val reference = providers.gradleProperty("sharedReference").isPresent
val choice = if (reference) "solution" else "practice"
layout.buildDirectory.set(layout.projectDirectory.dir("build/${if (reference) "reference" else "practice"}"))

// The Chapter 19 label may already have been moved to commonMain.
val labelStarter = layout.projectDirectory.file("src/starter/androidMain/kotlin/StudyLabel.kt")
val androidLabel = layout.projectDirectory.file("src/practice/androidMain/kotlin/StudyLabel.kt")
val commonLabel = layout.projectDirectory.file("src/practice/commonMain/kotlin/StudyLabel.kt")
val plannerStarter = layout.projectDirectory.file("src/starter/commonMain/kotlin/SessionPlanner.kt")
val plannerAnswer = layout.projectDirectory.file("src/practice/commonMain/kotlin/SessionPlanner.kt")
val preparePractice by tasks.registering {
    group = "workshop"
    description = "Creates missing study label and session planner practice files."
    doLast {
        if (!androidLabel.asFile.exists() && !commonLabel.asFile.exists()) {
            androidLabel.asFile.parentFile.mkdirs()
            labelStarter.asFile.copyTo(androidLabel.asFile, overwrite = false)
        }
        if (!plannerAnswer.asFile.exists()) {
            plannerAnswer.asFile.parentFile.mkdirs()
            plannerStarter.asFile.copyTo(plannerAnswer.asFile, overwrite = false)
        }
    }
}

kotlin {
    jvmToolchain(17)
    androidLibrary {
        namespace = "workshop.studylab.shared"
        compileSdk = 36
        minSdk = 30
        withHostTestBuilder {}
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "StudyShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("src/$choice/commonMain/kotlin")
            dependencies { implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0") }
        }
        androidMain { kotlin.srcDir("src/$choice/androidMain/kotlin") }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}

if (!reference) {
    tasks.configureEach {
        if (name.startsWith("compile")) dependsOn(preparePractice)
    }
}

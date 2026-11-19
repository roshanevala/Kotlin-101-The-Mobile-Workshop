plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

val reference = providers.gradleProperty("sharedReference").isPresent
val choice = if (reference) "solution" else "practice"
layout.buildDirectory.set(layout.projectDirectory.dir("build/${if (reference) "reference" else "practice"}"))

// Moving the answer into commonMain is the mission. Both locations count as an existing answer.
val starter = layout.projectDirectory.file("src/starter/androidMain/kotlin/StudyLabel.kt")
val androidAnswer = layout.projectDirectory.file("src/practice/androidMain/kotlin/StudyLabel.kt")
val commonAnswer = layout.projectDirectory.file("src/practice/commonMain/kotlin/StudyLabel.kt")
val preparePractice by tasks.registering {
    group = "workshop"
    description = "Creates the Chapter 19 answer once, preserving a moved or edited file."
    onlyIf { !androidAnswer.asFile.exists() && !commonAnswer.asFile.exists() }
    doLast {
        androidAnswer.asFile.parentFile.mkdirs()
        starter.asFile.copyTo(androidAnswer.asFile, overwrite = false)
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
    iosArm64()
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "StudyShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain { kotlin.srcDir("src/$choice/commonMain/kotlin") }
        androidMain { kotlin.srcDir("src/$choice/androidMain/kotlin") }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}

if (!reference) {
    tasks.configureEach {
        if (name.startsWith("compile")) dependsOn(preparePractice)
    }
}

plugins {
    id("com.android.application")
    kotlin("android")
}

val starterFile = layout.projectDirectory.file("src/starter/kotlin/workshop/studylab/MainActivity.kt")
val practiceFile = layout.projectDirectory.file("src/practice/kotlin/workshop/studylab/MainActivity.kt")
val preparePractice by tasks.registering {
    group = "application"
    description = "Creates the local Activity answer once, preserving existing work."
    inputs.file(starterFile)
    outputs.file(practiceFile)
    onlyIf("the Activity answer does not exist") { !practiceFile.asFile.exists() }
    doLast {
        val target = practiceFile.asFile
        target.parentFile.mkdirs()
        starterFile.asFile.copyTo(target, overwrite = false)
        logger.lifecycle("Created your Activity: src/practice/kotlin/workshop/studylab/MainActivity.kt")
    }
}

android {
    namespace = "workshop.studylab"
    compileSdk = 36
    buildToolsVersion = "35.0.0"
    defaultConfig {
        applicationId = "workshop.studylab"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += "workshop"
    productFlavors {
        create("practice") {
            dimension = "workshop"
            applicationIdSuffix = ".practice"
            manifestPlaceholders["workshopLabel"] = "Study Lab practice"
        }
        create("reference") {
            dimension = "workshop"
            applicationIdSuffix = ".reference"
            manifestPlaceholders["workshopLabel"] = "Study Lab reference"
        }
    }
    sourceSets.getByName("reference").java.srcDir("src/solution/kotlin")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { jvmToolchain(17) }

// Only practice variants need the learner's Activity; references never prepare/compile it.
tasks.configureEach {
    if (name.startsWith("compilePractice") && name.endsWith("Kotlin")) {
        dependsOn(preparePractice)
    }
}

dependencies {
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}

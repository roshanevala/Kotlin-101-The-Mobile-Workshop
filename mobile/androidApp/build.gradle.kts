plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val starterFile = layout.projectDirectory.file("src/starter/kotlin/workshop/studylab/MainActivity.kt")
val practiceFile = layout.projectDirectory.file("src/practice/kotlin/workshop/studylab/MainActivity.kt")
val composeStarter = layout.projectDirectory.file("src/starter/kotlin/workshop/studylab/StudyPlannerActivity.kt")
val composePractice = layout.projectDirectory.file("src/practice/kotlin/workshop/studylab/StudyPlannerActivity.kt")
val prepareComposePractice by tasks.registering {
    group = "application"
    description = "Creates the Chapter 14 Compose answer once, preserving earlier work."
    inputs.file(composeStarter)
    outputs.file(composePractice)
    onlyIf("the Compose answer does not exist") { !composePractice.asFile.exists() }
    doLast {
        val target = composePractice.asFile
        target.parentFile.mkdirs()
        composeStarter.asFile.copyTo(target, overwrite = false)
        logger.lifecycle("Created your Compose Activity: src/practice/kotlin/workshop/studylab/StudyPlannerActivity.kt")
    }
}

val preparePractice by tasks.registering {
    dependsOn(prepareComposePractice)
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
    buildFeatures { compose = true }
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
    val composeBom = platform("androidx.compose:compose-bom:2025.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    // Compose 1.9 brings an older Espresso; use the API 36-compatible test release.
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}

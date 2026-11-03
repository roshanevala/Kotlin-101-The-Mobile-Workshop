plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("workshop.ch08.SessionSnapshotsKt")
}

val starterFile = layout.projectDirectory.file("src/starter/kotlin/workshop/ch08/SessionSnapshots.kt")
val practiceFile = layout.projectDirectory.file("src/main/kotlin/workshop/ch08/SessionSnapshots.kt")

val starterTestFile = layout.projectDirectory.file("src/starterTest/kotlin/workshop/ch08/SnapshotPracticeTest.kt")
val practiceTestFile = layout.projectDirectory.file("src/practiceTest/kotlin/workshop/ch08/SnapshotPracticeTest.kt")

val preparePracticeTests by tasks.registering {
    group = "application"
    description = "Creates your local test working copy once, preserving existing tests."
    inputs.file(starterTestFile)
    outputs.file(practiceTestFile)
    onlyIf("a test working copy does not already exist") { !practiceTestFile.asFile.exists() }
    doLast {
        val target = practiceTestFile.asFile
        target.parentFile.mkdirs()
        starterTestFile.asFile.copyTo(target, overwrite = false)
        logger.lifecycle("Created your test working copy: src/practiceTest/kotlin/workshop/ch08/SnapshotPracticeTest.kt")
    }
}

val preparePractice by tasks.registering {
    dependsOn(preparePracticeTests)
    group = "application"
    description = "Creates your local working copy once, preserving existing work."
    inputs.file(starterFile)
    outputs.file(practiceFile)
    onlyIf("a working copy does not already exist") { !practiceFile.asFile.exists() }
    doLast {
        val target = practiceFile.asFile
        target.parentFile.mkdirs()
        starterFile.asFile.copyTo(target, overwrite = false)
        logger.lifecycle("Created your working copy: src/main/kotlin/workshop/ch08/SessionSnapshots.kt")
    }
}

tasks.named("compileKotlin") {
    dependsOn(preparePractice)
}

kotlin.sourceSets.named("test") {
    kotlin.srcDir("src/practiceTest/kotlin")
}
tasks.named("compileTestKotlin") {
    dependsOn(preparePracticeTests)
}

// Reference code has its own classpath: neither path compiles against the other.
val solution by sourceSets.creating
val solutionTest by sourceSets.creating {
    compileClasspath += solution.output
    runtimeClasspath += solution.output
}
kotlin.sourceSets.named("solutionTest") {
    kotlin.srcDir("src/test/kotlin")
    kotlin.srcDir("src/solutionTest/kotlin")
}

configurations[solution.implementationConfigurationName].extendsFrom(configurations.implementation.get())
configurations[solutionTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[solutionTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runSolution") {
    group = "application"
    description = "Runs the separate reference solution."
    classpath = solution.runtimeClasspath
    mainClass.set("workshop.ch08.SessionSnapshotsKt")
}

tasks.register<Test>("checkSolution") {
    group = "verification"
    description = "Checks the reference solution against the mission examples."
    testClassesDirs = solutionTest.output.classesDirs
    classpath = solutionTest.runtimeClasspath
}

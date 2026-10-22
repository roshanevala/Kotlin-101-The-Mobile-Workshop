plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("workshop.ch01.TimerKt")
}

// Reference code has its own classpath: neither path compiles against the other.
val solution by sourceSets.creating
val solutionTest by sourceSets.creating {
    compileClasspath += solution.output
    runtimeClasspath += solution.output
}
kotlin.sourceSets.named("solutionTest") {
    kotlin.srcDir("src/test/kotlin")
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
    mainClass.set("workshop.ch01.TimerKt")
}

tasks.register<Test>("checkSolution") {
    group = "verification"
    description = "Checks the reference solution against the mission examples."
    testClassesDirs = solutionTest.output.classesDirs
    classpath = solutionTest.runtimeClasspath
}

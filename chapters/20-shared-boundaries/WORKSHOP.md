# Workshop 20 The first valid session gets the second ID

Study Lab's session planner accepts a request for zero minutes and issues it an ID. Your task is to restore the input rules from Chapter 3 and make the ID source observable at the point where the planner accepts a session. Read the [lesson](LESSON.md) first.

## 1. Predict, then run the starter

Start in the repository's **`mobile` directory**, with JDK 17 and Android SDK 36 configured. The mobile wrapper uses Gradle 8.14.3 and Kotlin 2.2.21. You can finish the Android path on the host JVM without launching an emulator. [Mobile setup](../../mobile/README.md)

```sh
./gradlew :shared:preparePractice
```

Open `shared/src/practice/commonMain/kotlin/SessionPlanner.kt`. This is your editable copy. The [tracked starter](../../mobile/shared/src/starter/commonMain/kotlin/SessionPlanner.kt) supplies the initial contents when your answer is missing. In the [common tests](../../mobile/shared/src/commonTest/kotlin/SessionPlannerTest.kt), read `RecordingIds`. Give one planner this source and predict the result and the running call count after each request.

| Attempt | Topic | Minutes |
| --- | --- | --- |
| 1 | `"  Kotlin  flows  "` | 0 |
| 2 | `"  Kotlin  flows  "` | 25 |
| 3 | `null` | 120 |

Run the five Chapter 20 tests.

```sh
./gradlew :shared:testAndroidHostTest --tests 'workshop.studylab.shared.SessionPlannerTest'
```

The starter passes the test for 1 and 120 minutes and the test that calls the platform ID factory. Three tests fail. Find the first rejected duration that came back as a session, then look at the two topic failures. Open `shared/build/practice/reports/tests/testAndroidHostTest/index.html` for the assertions.

## 2. Repair the common planner

Edit the body of `plan` in your practice copy. The function accepts a nullable topic and an integer duration. Give each input a single meaning on both platforms.

- Accept 1 through 120 minutes, including both endpoints. Any other duration returns `null` and leaves the source's call count unchanged.
- Trim the edges of a supplied topic, preserving spaces inside it. Use `"Untitled"` for a missing, empty, or whitespace-only topic.
- Each accepted session gets exactly one ID from the injected source. Keep that ID and the requested minutes in the result.

Run the Android command again. Compare your three predictions with the repaired behavior. If a test reports the right return value and the wrong call count, trace when Kotlin evaluates the call to `nextId()`. [Progressive hints](HINTS.md)

## 3. Run the same rules on the simulator

For this step, use an Apple silicon Mac with full Xcode and an installed iOS simulator runtime. Follow the simulator setup in Chapter 19. In the terminal that will run Gradle, assign your available workshop simulator's UDID to `study_simulator`. [Chapter 19 setup and simulator selection](../19-project-and-build-model/WORKSHOP.md)

**Starting from a fresh download?** After preparation, complete the Chapter 19 move in your practice tree. Look for `shared/src/practice/commonMain/kotlin/StudyLabel.kt`. If you already moved your label there, keep your copy. If it exists only under `shared/src/practice/androidMain/kotlin/`, move that existing file into `commonMain/kotlin/`. The simulator compiles every common test before applying the test filter. An unresolved `studyLabel` in `StudyLabelTest.kt` points to the earlier file move.

```sh
./gradlew :shared:iosSimulatorArm64Test --device "$study_simulator" --tests 'workshop.studylab.shared.SessionPlannerTest'
```

Expect the same five cases to pass. Open `shared/build/practice/reports/tests/iosSimulatorArm64Test/index.html`. Find `platformSourceSuppliesAUuidToAnAcceptedSession` in both reports. Which platform method supplied the value each time? Which assertions used the predictable source instead? If you have only the Android toolchain, you can trace the Apple implementation in the source and identify the simulator run as the check still to do.

## 4. Explain the boundary

**Optional compiler experiment.** Use a separate copy of the workshop. In `shared/src/androidMain/kotlin/PlatformSessionIds.android.kt`, change just the function name from `platformSessionIds` to `platformSessionIDs`. Leave the common declaration alone. From that copy's `mobile` directory, run the Android compiler.

```sh
./gradlew -PsharedReference :shared:compileAndroidMain
```

Find the diagnostic saying the renamed function has no corresponding expected declaration. On a Mac, predict what `./gradlew -PsharedReference :shared:compileKotlinIosSimulatorArm64` will do, then try it. Which source set supplies the factory to that compiler? Restore the original spelling and run the Android command again.

**Interview prompt.** Explain how one common test can count ID requests on both platforms even though Java and Foundation create IDs differently. Point to the constructor parameter, the factory, and one `actual` implementation in your explanation.

**Changed request.** A command-line tool will import existing sessions. For a valid row, its ID source returns an existing ID such as `"import-007"`. Which part would you supply to the planner, and what should happen to the source when a row has 121 minutes? Sketch a check with the invalid row followed by a valid row. Compare it with the [worked solution](SOLUTION.md).

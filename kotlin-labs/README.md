# Kotlin exercise setup

This workspace runs the Kotlin exercises without an Android SDK, emulator, or Xcode. Read the [Chapter 01 lesson](../chapters/01-values-and-types/LESSON.md) first. Use this setup guide when you are ready for the [workshop](../chapters/01-values-and-types/WORKSHOP.md).

Lesson pages live in the repository's `chapters` folder, next to this workspace. Keep the lesson open in your browser or Markdown viewer; use Android Studio for the exercise code in `kotlin-labs`.

## Requirements

Use a **JDK 17** installation and an internet connection for the first Gradle and dependency downloads. The checked-in wrapper selects **Gradle 8.14.3**; the build selects **Kotlin 2.2.21**. You do not need to install Kotlin or Gradle separately.

If you need a JDK, [Eclipse Temurin provides JDK 17](https://adoptium.net/temurin/releases/?version=17). Choose the JDK package for your operating system and processor. The course pins this compatible toolchain instead of following each new release. [Kotlin Gradle compatibility](https://kotlinlang.org/docs/gradle-configure-project.html).

## Open in Android Studio

1. Choose **Open** and select the repository's `kotlin-labs` folder.
2. In **Settings → Build, Execution, Deployment → Build Tools → Gradle**, select JDK 17 as the Gradle JDK. Use the IDE's download option if needed.
3. Let Gradle sync. Open the IDE terminal in `kotlin-labs` and run the command below. This first chapter uses the terminal so its instructions also work outside the IDE.

## Run from a terminal

Set `JAVA_HOME` to your installed JDK 17 and ensure its `bin` directory is on `PATH`. Open a new terminal after installing a JDK. From the repository root:

```sh
cd kotlin-labs
java -version
./gradlew --version
./gradlew --console=plain :ch01-values-and-types:run
```

`java -version` and the JVM shown by `./gradlew --version` should identify Java 17. If they disagree, check `JAVA_HOME`; Gradle can use that installation even when the `java` command on `PATH` points elsewhere.

The Kotlin version printed by Gradle's version command belongs to Gradle's own scripting runtime. The lesson compiler version is declared in this workspace's `build.gradle.kts`.

On Windows PowerShell, replace `./gradlew` with `./gradlew.bat` in every command. First-run downloads can take a few minutes. The initial timer output is deliberately `1 h 0 min`.

<details>
<summary>More Chapter 01 commands</summary>

Run these from `kotlin-labs`:

| Command after `./gradlew --console=plain` | Purpose |
| --- | --- |
| `:ch01-values-and-types:run` | Run your starter or edited implementation |
| `:ch01-values-and-types:test` | Check your implementation against seven acceptance examples |
| `:ch01-values-and-types:runSolution` | Run the separate reference implementation |
| `:ch01-values-and-types:checkSolution` | Check the reference with the same seven examples |

Some starter tests intentionally fail until the mission is solved. A general `check` or `build` includes those tests and therefore also fails on an untouched starter. Maintainer CI checks the reference separately.

</details>

<details>
<summary>If setup fails</summary>

- **Java is missing or the Java version is unsupported:** install JDK 17 and check both version commands above. A new terminal may be needed.
- **Permission denied for the wrapper on macOS or Linux:** run `chmod +x gradlew` once, then retry.
- **Download or dependency resolution failed:** check connectivity and proxy settings, then rerun the same command. The build uses the Gradle distribution service, plugin portal, and Maven Central.
- **Could not find the project or task:** check that the terminal is in `kotlin-labs`, which contains `settings.gradle.kts`.
- **Tests show expected and actual values:** setup worked; use the [workshop](../chapters/01-values-and-types/WORKSHOP.md) to investigate the difference.

For reports, include the command, tool versions, and relevant error text.

</details>

**[Continue to the workshop](../chapters/01-values-and-types/WORKSHOP.md)**

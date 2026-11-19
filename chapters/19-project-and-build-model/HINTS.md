# Chapter 19 Hints

Open one hint, then return to the [workshop](WORKSHOP.md).

<details>
<summary>1. Where does the compiler look?</summary>

Both tests use the same package name for the label. Compare the folders that contribute production code to Android and to the simulator. Is the label inside the sets visible to both test compilations?

</details>

<details>
<summary>2. Follow a source root from the build script</summary>

Look for the two `kotlin.srcDir` calls in the shared module. Replace the source-choice variable with `practice` on paper. Which resulting path is attached to a platform leaf, and which path feeds every target?

</details>

<details>
<summary>3. Why would copying the function break Android?</summary>

The Android target already sees its own sources and the common sources. If the same declaration exists in each, that target sees it twice. Move the single practice file into the source root all targets can read, then repeat each target's checks.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

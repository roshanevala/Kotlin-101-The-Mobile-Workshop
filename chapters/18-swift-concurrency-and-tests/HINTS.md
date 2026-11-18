# Chapter 18 Hints

Use the first hint that helps, then return to the [workshop](WORKSHOP.md).

<details>
<summary>1. Which call is allowed to change the screen?</summary>

Give the two Swift requests different names on paper. Draw a line where each call suspends. At the end of the second call, what information still distinguishes the first call's eventual answer from a current answer?

</details>

<details>
<summary>2. What changes when someone taps Cancel?</summary>

A SwiftUI task has a cancellation flag. The loader also owns a decision about which attempt may publish. A caller can invalidate that decision while the old task is suspended. Check both the successful and throwing paths. Before clearing a cancelled call's loading state, first establish that it still owns that state.

For one way to identify attempts, import Foundation and create a fresh `UUID()` when a load starts. Which ID should the call remember locally, and which ID should the loader remember across calls?

</details>

<details>
<summary>3. When would a topic-only check let a stale failure through?</summary>

Keep each pending request returned by the controlled source. Both can carry the same topic. Fail the first handle while the second is waiting. Because the helper waits for that loader call to finish, you can inspect the screen state immediately after the stale failure. Then use the second handle to finish the request that still owns the screen.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

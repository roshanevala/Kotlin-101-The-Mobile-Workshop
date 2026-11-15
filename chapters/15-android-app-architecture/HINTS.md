# Chapter 15 Hints

Try the [workshop](WORKSHOP.md) first. Keep edits in the generated ViewModel answer.

<details>
<summary>Hint 1 — Which lifetime did you actually test?</summary>

Rotation normally retains an Activity-owned ViewModel. Its ordinary fields can therefore appear safe without any saved-state representation. What would a new model read? Which handle key identifies the note independently of its topic text?

For requests, identify the current Job and the point where a repository result becomes screen state. A cancelled older coroutine must not publish there.

</details>

<details>
<summary>Hint 2 — Classify results and preserve cancellation</summary>

Each new request cancels the preceding one and publishes Loading. An empty result and an IO failure have distinct states. Cancellation should leave the coroutine, rather than taking the recoverable-error branch.

Check coroutine activity before success and failure publication. Keep notes separate from load state so Reload cannot erase a draft.

</details>

<details>
<summary>Hint 3 — Read and write the same saved value</summary>

`savedState.getStateFlow("note-$id", "")` provides a read-only note stream. Assign accepted text through `savedState["note-$id"]`. Validate current Content membership and `text.length <= 120` before assigning; preserve accepted whitespace.

The fixed factory obtains the handle from creation extras. Do not create a new empty handle inside the ViewModel or cache another authoritative note map beside it. Navigation already supplies the ID and shares the Activity owner.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

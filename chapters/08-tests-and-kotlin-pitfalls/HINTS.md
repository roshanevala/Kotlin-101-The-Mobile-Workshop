# Chapter 08 Hints

Try the [workshop](WORKSHOP.md), then reveal one level at a time.

<details>
<summary>Level 1 Make a change that the wrong implementation cannot hide</summary>

An identity check accepts an object compared with itself. A shared empty list still looks empty until one side changes. Which input and edit would expose each bug? Save a value before making the edit whose effect should be isolated.

</details>

<details>
<summary>Level 2 Trace the mutable objects</summary>

- `==` asks about equality; `===` asks about one instance.
- A generated data-class copy retains the same list reference unless you supply another list.
- History needs protection when accepting a plan and when returning editable plans.
- An outer list snapshot does not copy its plan elements or their editable lists.
- Planned-only input distinguishes a completed total from an unconditional total.

</details>

<details>
<summary>Level 3 Sketch a regression, then repair its boundary</summary>

Arrange a plan containing one session, obtain a copy, clear the copy's list, and assert that the original still contains the session. For saved history, perform the edit after `save`; for returned history, edit the returned plan and read again.

A plan copy can supply `sessions = plan.sessions.toMutableList()`. Reuse the independent-plan helper when capturing a saved version and when returning versions. `map` can build the outer result while converting each stored plan to an independent one.

In a total test, use a planned 30-minute session and assert `0L`. Keep the expected value separate from the function under test. Run your test class with `--tests workshop.ch08.SnapshotPracticeTest` to watch your own assertions fail before the repair.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution after your attempt](SOLUTION.md)

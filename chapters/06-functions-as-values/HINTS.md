# Chapter 06 Hints

Open one level at a time after trying the [workshop](WORKSHOP.md).

<details>
<summary>Level 1 Ask what changes and what stays the same</summary>

The list traversal stays the same when a report's rule changes. Which parameter contains that decision? Does composing two rules require any session yet? For previews, mark when each stage needs an element and when it can stop.

</details>

<details>
<summary>Level 2 Match a concept to each helper</summary>

- `allOf` returns a lambda. `&&` already implements the required short circuit.
- `filter` selects entries; `map` changes their representation. `also` returns its receiver.
- In `StudySession.label`, `this` is the receiving session.
- Validate before the chain. Keep `take` after `map` for this eager experiment.
- `asSequence` changes evaluation; `toList` materializes the final result.

</details>

<details>
<summary>Level 3 Sketch one step before filling the rest</summary>

Start the combined rule with `return { session -> ... }`. Call the supplied rules inside it, passing the same `session` to each.

The preview shapes are `sessions.filter(...).map(...).take(limit)` and `sessions.asSequence().filter(...).map(...).take(limit).toList()`. Fill in the supplied callbacks; do not call them before passing them to a stage. Put `require(limit >= 0)` before either expression.

Reset the counters between experiments in `main`; otherwise the second result includes work from the first.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution after your attempt](SOLUTION.md)

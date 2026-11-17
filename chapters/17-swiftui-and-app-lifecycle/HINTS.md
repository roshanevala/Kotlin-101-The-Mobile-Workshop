# Chapter 17 Hints

Try the [workshop](WORKSHOP.md) first. Keep core edits in the generated Practice screen file.

<details>
<summary>Hint 1 — Is the observed object the same owner?</summary>

Observation can make changes visible without retaining the object across a new parent value. Compare the shell's managed Boolean with the root's plain class property. Keep the root at a stable identity.

Find every use of a fixed ID or editable topic as navigation/row identity.

</details>

<details>
<summary>Hint 2 — Which draft does Save actually read?</summary>

The store already has a draft for each ID. Give TopicField a Binding whose getter and setter use that ID. Remove the child's authoritative State copy and its on-appear reload.

Clear and Cancel should change the value that the field displays. Save and disabled-state validation must read that same owner. Save commits only the selected ID.

</details>

<details>
<summary>Hint 3 — Activation preserves an existing scene</summary>

Use State for the root's store, and use Identifiable IDs for ForEach/navigation. The fixed filter demonstrates Bindable, while the fixed field consumes Binding and owns only focus.

Remove the handler that calls reset when the scene becomes active. Keep phase observation in the fixed App as a diagnostic. Its events are neither a new-model request nor a durable storage mechanism.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

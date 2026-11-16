# Chapter 16 Hints

Try the [workshop](WORKSHOP.md) before opening these. Keep edits in the generated Swift answer.

<details>
<summary>Hint 1 — Did a binding copy an object?</summary>

Inspect StudyPlan's type declaration. A second class reference still reaches the same instance. Changing only a local binding to var does not clone that instance. Which stored values should be independent in this mission?

For the callback, draw every strong reference after the outside owner becomes nil.

</details>

<details>
<summary>Hint 2 — Keep absence and mutation explicit</summary>

Guard against missing input, trim before Int conversion, and preserve a failed conversion as nil. Validate positive ID before duration, then resolve the topic fallback.

A value plan's editing method must be mutating. Unwrap `firstIndex(where:)`, handle an already completed first match, and change one array element. Generic map can read the required topic property from each element.

</details>

<details>
<summary>Hint 3 — Let a retained closure outlive its owner safely</summary>

A `[weak self]` capture gives the stored closure an optional non-owning reference. Handle its nil case inside the closure when it is invoked; that is when the owner may already be gone.

Check a weak observer after assigning the last outside optional owner to nil. A separately retained callback should still be callable and produce the specified fallback. Do not replace the lifetime contract with an unowned reference or an artificial delay.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

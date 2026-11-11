# Chapter 11 Hints

Try the [workshop](WORKSHOP.md) before expanding these. Keep changes in your local `src/main` file.

<details>
<summary>Hint 1 — Which work should a new query replace?</summary>

The quiet period and source call belong to the same replaceable branch. What happens if a blank query is removed before it reaches that boundary? What happens if a debounce hides the new query from a running search?

A query recipe should not run during construction. Which operator starts collection, and which operators merely return another flow?

</details>

<details>
<summary>Hint 2 — Match operators to contracts</summary>

Trim with `map`, suppress adjacent normalized duplicates with `distinctUntilChanged`, and return an inner flow with `flatMapLatest`. Delay only for nonblank input. Keep cancellation ahead of `IllegalStateException` recovery and keep the final `emit` outside the source `try`.

Use `combine` for the latest search/progress pair. Use `stateIn` once in the holder for a shared current screen, with the supplied owner and explicit eager policy.

</details>

<details>
<summary>Hint 3 — Sketch the boundaries</summary>

Validate before returning the operator chain. Inside each branch, either emit idle, or delay → emit loading → calculate source outcome → emit outcome. Add `buffer(0)` after the latest chain.

The holder's chain begins with `searchStates(query, source, debounceMillis)`, then passes it with progress to `screenStates`. The initial screen uses idle and `progress.value`; `submit` assigns the private query value. Do not use a loop that waits for a StateFlow to finish.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

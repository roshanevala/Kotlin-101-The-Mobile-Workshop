# Chapter 07 Hints

Try the [workshop](WORKSHOP.md), then open one level at a time.

<details>
<summary>Level 1 Identify the object that owns each piece of state</summary>

Who owns the map? Which caller chooses that instance? Can the report learn about a save when it reads another instance? For `complete`, think about what the injected store must know before a copy can be saved.

</details>

<details>
<summary>Level 2 Match a language feature to each operation</summary>

- A generic map stores `Int` keys and `T` values. The constructor function extracts the key.
- Assigning a value at an existing map key replaces it. `values.toList()` creates a snapshot.
- Interface delegation forwards without inheriting a concrete storage implementation.
- An Elvis early return handles an absent record before calling `copy`.
- The report's dependency exposes reads, so it cannot require `save`.

</details>

<details>
<summary>Level 3 Sketch the boundaries</summary>

The storage field can start as `private val items = mutableMapOf<Int, T>()`. In `save`, connect `idOf(item)` to `item`; retrieval and listing can then use that same map.

The reader wrapper's header ends with `: Reader<T> by source`. Remove its placeholder overrides, which would otherwise replace generated forwarding.

Start completion with `val original = store.findById(id) ?: return false`. Save a copied completed value through that same `store`, then report success. Avoid creating or downcasting storage in either consumer.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution after your attempt](SOLUTION.md)

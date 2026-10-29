# Workshop 05 Optional hints

Open one hint, then return to the [workshop](WORKSHOP.md) and try the next helper.

<details>
<summary>Hint 1 Name the identity</summary>

Is a duplicate defined by every model property, or only by `id`? What should happen when a planned record appears before a completed record with that same ID? Decide which records survive before doing any arithmetic.

</details>

<details>
<summary>Hint 2 Give each collection one job</summary>

Use a mutable set of integer IDs to remember what you have seen. Its `add` result tells you whether an ID is new. Keep a separate mutable list for retained records in encounter order.

For totals, look up the current topic's `Long` total or start at `0L`. For the index, assign a retained session at its ID key. Both helpers can reuse the deduplicated list.

</details>

<details>
<summary>Hint 3 Follow the reporting pipeline</summary>

```text
unique records:
    for each input record, keep it only when its ID is newly added to the seen set
totals:
    for each unique record, add its minutes to its topic only if it is completed
index:
    for each unique record, store it under its ID
topics:
    return the completed totals' keys in natural sorted order
```

Keep mutable result state local to each call. Returning the supplied list directly would keep an alias to a mutable input; sorting or removing records in that input would change the caller's history.

</details>

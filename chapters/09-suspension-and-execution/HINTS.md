# Chapter 09 Hints

Try the [workshop](WORKSHOP.md), then reveal one level at a time.

<details>
<summary>Level 1 Identify the dependency and the owner</summary>

Does the minutes request need the topic result? When is the second request created relative to the first wait? Which scope should stay alive until both results arrive? The caller supplies execution policy to the third helper.

</details>

<details>
<summary>Level 2 Choose each coroutine operation for its job</summary>

- Sequential suspending calls need ordinary local variables.
- `coroutineScope` creates a local owner that waits for its children.
- `async` returns a result handle; `launch` returns a lifetime handle.
- Create both result handles before calling either `await`.
- `withContext` runs a block with the supplied context and returns its result.

</details>

<details>
<summary>Level 3 Sketch the structure before filling the calls</summary>

The concurrent helper can be an expression body ending in `= coroutineScope { ... }`. Inside, start one `async` for each source method. The final dashboard uses their awaited values. Keep both child declarations before that final expression.

The dispatcher-aware helper can return `withContext(dispatcher) { ... }`, invoking the concurrent helper inside. Use the argument rather than choosing another dispatcher. No custom scope, thread, or blocking bridge is needed in either helper.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution after your attempt](SOLUTION.md)

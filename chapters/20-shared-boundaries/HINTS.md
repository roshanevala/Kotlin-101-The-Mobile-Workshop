# Chapter 20 Hints

Open one hint at a time, then return to the [workshop](WORKSHOP.md).

<details>
<summary>1. What happened before the return?</summary>

A request for zero minutes has two observable outcomes. What value does the caller receive? How many times did the planner ask for an ID? Trace the starter for that single input.

</details>

<details>
<summary>2. Where should the topic rule run?</summary>

Both targets need the same rule for a missing or blank topic. A nullable string can be trimmed with a safe call. When is a topic containing just a tab ready to be checked for emptiness?

</details>

<details>
<summary>3. In which order can you make the decisions?</summary>

Try a guard at the beginning of the function. Think about what `minutes !in 1..120` tells you before the source has done any work. For an accepted request, form the topic, then build one session using the source passed to the constructor.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

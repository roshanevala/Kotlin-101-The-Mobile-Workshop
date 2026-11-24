# Chapter 22 Progressive hints

<details>
<summary>Hint 1 — Compare two requests</summary>

Put a breakpoint in `draft(for:)`. Open the planner, type in it, close it, and open it again. The method can run several times. Concentrate on the design and object returned when the sheet reopens. Who still exists at that moment?

</details>

<details>
<summary>Hint 2 — Give each design an address</summary>

A single stored draft could let the native editor read the shared editor's text. Sketch two boxes labeled Native and Shared. What Swift collection would let the owner look up an existing box by its `DraftDesign`? Put that collection somewhere that a new `PlannerDraftOwner` will get its own empty copy.

</details>

<details>
<summary>Hint 3 — Count factory calls</summary>

Try tracing the design sequence Native, Shared, Native. The third lookup should return the first object. Construct through `makeDraft` only when that owner's lookup misses, and store what the factory returned before handing it to the caller. Clearing a draft changes the fields of that object.

</details>

[Back to the workshop](WORKSHOP.md)

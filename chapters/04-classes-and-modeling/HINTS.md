# Workshop 04 Optional hints

Open one hint, then return to the [workshop](WORKSHOP.md) and try again.

<details>
<summary>Hint 1 Decide what each value means</summary>

Which three properties define an equal session? Does a missing topic mean rejection, or does it have a fallback? Should completing a session change the caller's original or return another value? Write those answers before changing the helpers.

</details>

<details>
<summary>Hint 2 Use the model's operations</summary>

The `data` modifier supplies value equality and `copy`. A named argument to `copy` replaces one constructor property and keeps the others. For creation, handle a rejected duration before constructing an accepted session. A safe call can trim the topic; checking for `null` and emptiness handles the fallback.

In a sealed `when`, `is SessionResult.Accepted` lets you read the session. An enum branch names a value such as `SessionStatus.PLANNED`; it does not need `is`.

</details>

<details>
<summary>Hint 3 Give each branch one job</summary>

Use this outline for the description:

```text
when the result is accepted:
    read its session
    choose the status label with an exhaustive enum when
    produce the description using that label and the session's values
when the result is rejected:
    produce its message unchanged
```

Return the outer `when` expression. In a branch with braces, the final string expression is the branch's value. For completion, return a copy with the completed status; assigning the original to another local variable creates no new object.

</details>

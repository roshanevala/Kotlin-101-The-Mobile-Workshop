# Workshop 02 Optional hints

Open one hint, then return to the [workshop](WORKSHOP.md) and try again.

<details>
<summary>Hint 1 Separate the three jobs</summary>

Which function answers a yes/no question? Which one chooses text? Which one repeats work? Solve them in that order. Write the valid endpoints and category transitions on paper before translating them to code.

</details>

<details>
<summary>Hint 2 Use the lesson tools</summary>

`in` checks an inclusive range, and `!in` checks values outside it. `when` can choose text from several ranges. An early `return` can stop an invalid request before it reaches a loop. For valid requests, `1..rounds` gives the numbering the specification asks for.

</details>

<details>
<summary>Hint 3 Build the result in stages</summary>

Use this outline for the final function:

```text
reject an invalid duration
reject an invalid round count
get the session label
start an empty local string
for each round:
    add a newline if this is not the first round
    add the numbered line
return the accumulated string
```

The lesson's `+=` example shows how to add text, and `\n` separates lines. Add the separator between lines so there is no extra newline at the end.

</details>

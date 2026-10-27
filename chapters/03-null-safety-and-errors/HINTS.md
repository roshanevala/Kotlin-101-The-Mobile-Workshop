# Workshop 03 Optional hints

Open one hint, then return to the [workshop](WORKSHOP.md) and try the corresponding helper.

<details>
<summary>Hint 1 Name the missing thing</summary>

Is the input absent, is the trimmed string empty, or did a supplied number fail a rule? These are different questions. A missing topic gets a fallback. Missing minutes need an explanation rather than an invented number.

</details>

<details>
<summary>Hint 2 Follow the nullable types</summary>

Use `?.` to work with supplied text and `trim()` to remove whitespace at its edges. `toIntOrNull()` returns an `Int?`; the 1–120 duration rule belongs in the next helper. Elvis can exit early for a missing parsed value. A non-null error string can be returned directly.

</details>

<details>
<summary>Hint 3 Connect the helpers in order</summary>

Use this outline for the final function:

```text
parse the minutes
if there is no number, return the parsing message
ask the duration helper for an error
if an error exists, return it
get the cleaned topic or fallback
return the formatted description
```

For the topic helper, trim a supplied value before testing whether it is empty. Elvis by itself only handles `null`. The parser does not need `!!`, a default number, or a `try`/`catch` block.

</details>

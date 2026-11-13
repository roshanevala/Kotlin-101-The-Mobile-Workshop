# Chapter 13 Hints

Attempt the [workshop](WORKSHOP.md) before expanding these. Edit only the generated practice Activity.

<details>
<summary>Hint 1 — Which object owned the missing values?</summary>

A new Activity gets new fields. Which two values describe the small draft? When saving, where is the newest topic text if the user has typed without pressing another button?

A restored label does not automatically restore the integer used by the next click.

</details>

<details>
<summary>Hint 2 — Match writes, reads, and defaults</summary>

Use `putString`/`getString` for topic text and `putInt`/`getInt` for the backing count, under the provided stable keys. Keep the `super` calls. Restore after obtaining View references and before rendering.

Use a null/absent-value fallback for text; an explicitly empty string is still a saved value. Do not use a companion object as draft storage.

</details>

<details>
<summary>Hint 3 — Check the whole transition</summary>

Saving reads `topic.text.toString()` and `completed`. Creation reads from nullable `savedInstanceState`, falling back to the default string resource and zero. Assign the restored backing value before calling `renderCompleted`.

Try type → recreate with no count click. Then try complete twice → recreate → complete once → recreate. The first catches stale text, the second catches display-only restoration. Compare PID separately when investigating a real process restart.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

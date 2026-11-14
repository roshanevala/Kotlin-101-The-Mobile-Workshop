# Chapter 14 Hints

Try the [workshop](WORKSHOP.md) before opening these. Keep edits in the generated Compose answer.

<details>
<summary>Hint 1 — Which text does Save read?</summary>

Look at the child's field value and its edit callback. Does the route receive the same text the user sees? What happens when the parent loads another committed topic but the child remembers its first value?

Selection, committed topic, and draft describe different things. State their ownership before changing declarations.

</details>

<details>
<summary>Hint 2 — Use events for authoritative state</summary>

The field displays `draftTopic` and forwards to `onDraftChange`. Use saveable state for the six authoritative values. In a different-row selection event, set selection and load that row's committed topic; same-row selection preserves draft.

Save branches on selected ID. Toggle branches on the event's row ID. Cancel loads the selected committed topic. A rendering function or selection effect should not overwrite a restored unsaved draft.

</details>

<details>
<summary>Hint 3 — Separate the notice lifetime</summary>

Keep revision and visibility in ordinary `remember`. A successful Save increments the revision and shows feedback. `LaunchedEffect(savedRevision)` cancels the previous timer and gives the latest save its own delay, then clears only feedback.

Try duplicate topic names, changing one checkbox while another row is being edited, and recreation with a committed topic plus a different unsaved draft. IDs remain 1/2 even when names match.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)

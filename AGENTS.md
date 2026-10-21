# Repository guidance

## Purpose

Kotlin 101 — The Mobile Workshop teaches Kotlin and Android first, then iOS and KMP. Lessons are practical missions for beginners and returning developers. Develop one chapter at a time; the roadmap is not a claim that all chapters exist.

## Read only what the task needs

- Discover files with `rg --files`; search narrowly with `rg -n`.
- Read the target files and applicable nested `AGENTS.md` before editing. Do not reload instructions already present in context.
- For continuation or milestone planning, use `docs/PROJECT_STATUS.md`.
- For topic scope, use `docs/CURRICULUM.md`; for lesson design, use `docs/LEARNING_EXPERIENCE.md`.
- For exercise layout, app structure, or chapter checkpoints, use `docs/PROJECT_STRUCTURE.md`.
- For dependency or toolchain decisions, use `docs/RESEARCH.md` and verify current official documentation.
- For agent workflow questions, use `docs/AGENT_WORKFLOW.md`; for repository publication, use `docs/GITHUB_SETUP.md`.
- Do not read every linked document before a small edit. These links are routes, not a reading checklist.

## Work efficiently

- Prefer one agent. When the user requests delegation, give each worker a bounded task, minimal context, and distinct file ownership.
- Batch independent reads; keep dependent edits sequential. Limit tool output to useful matches and failure details.
- Make focused changes. Reuse existing commands, fixtures, and examples before adding tools or dependencies.
- Record durable decisions and milestone changes in their canonical files. Keep transcripts, temporary logs, and personal paths out of tracked documentation.
- Do not add agent services, paid tools, or model-specific configuration just to reduce context. Do not claim measured savings without usage evidence.

## Verification

- Documentation check: `python3 scripts/check_docs.py`.
- There is currently no Gradle wrapper or runnable mobile application. Do not invent build commands or claim Android or iOS verification.
- When code is introduced, document and run the affected exercise's actual commands. Use tests that check observable behavior and relevant boundary cases.
- After applicable checks pass, broaden testing only for a concrete unresolved risk. Report unrun checks plainly.
- Finish with what changed, verification, and any remaining limitation. Update status only when progress materially changes.

## Code Review Rules

Flag incorrect explanations, exercises requiring untaught concepts, broken run instructions, solution spoilers in starter files, platform APIs leaking into common code, and verification claims unsupported by execution. Keep formatting feedback proportional to its effect on learners.

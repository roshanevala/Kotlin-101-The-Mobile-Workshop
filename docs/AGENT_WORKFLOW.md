# Working efficiently with coding agents

This repository uses short persistent instructions, targeted context, and focused verification. The goal is to spend less work rediscovering the project while keeping lessons correct. These are operating practices, not a guarantee of a particular token count or bill.

Guidance reviewed against official documentation on **21 October 2026**.

## Instruction layout

| File | Purpose | When to read |
| --- | --- | --- |
| [Root AGENTS.md](../AGENTS.md) | Shared operating rules and document routes | Repository work |
| [Chapter AGENTS.md](../chapters/AGENTS.md) | Lesson authoring rules | Chapter changes |
| [Docs AGENTS.md](AGENTS.md) | Document ownership and evidence rules | Documentation changes |
| [Project status](PROJECT_STATUS.md) | Current stage and next implementation | Continuing work or planning |
| This file | Prompt patterns and delegation guidance | Agent workflow changes or onboarding |

Codex supports root and nested instruction files, with more specific instructions taking precedence along its discovery path. Check which instructions a client loads; a root-launched session may need to read a target directory's guidance explicitly. [Official AGENTS.md documentation](https://learn.chatgpt.com/docs/agent-configuration/agents-md).

The short [Copilot instructions](../.github/copilot-instructions.md) point to the same repository guidance. Copilot support varies by client and feature; this file does not configure a specialist or start an agent. [GitHub custom instructions](https://docs.github.com/en/copilot/concepts/prompting-and-context/custom-instructions).

## Keep the default context small

Read the requested files first. Search for a symbol or topic before opening a long document. Expand to a related file only when a dependency or unclear requirement makes it relevant.

Keep stable project rules in `AGENTS.md`, current milestones in status, and detailed teaching decisions in their existing documents. Do not create a second memory file that copies the roadmap. Add a reusable skill only after a repeated workflow needs more guidance than an ordinary task prompt.

Use bounded terminal output and summarize long failures. Run deterministic checks locally rather than asking a model to inspect every link repeatedly. Keep reasoning and final responses proportional to the task while retaining evidence a reviewer needs.

This design follows OpenAI's advice to avoid mandatory document stacks and use progressive disclosure for detailed workflows. [Rethinking skills and prompts](https://developers.openai.com/blog/rethinking-skills-and-prompts-for-gpt-6-astra).

## Give a task a clear finish line

A useful request specifies the outcome, relevant paths, constraints, and evidence of completion. Here is a copyable prompt for the next deliverable:

```text
Implement Chapter 01's timer mission.
Use the pilot brief in docs/LEARNING_EXPERIENCE.md and the Chapter 01
scope in docs/CURRICULUM.md. Follow docs/PROJECT_STRUCTURE.md.
Put teaching material in chapters/01-values-and-types and executable
exercises in kotlin-labs/ch01-values-and-types with the minimal build
files needed to run them.
Keep starter, hints, solution, and verification separate.
Do not introduce Android or KMP setup yet.
Done when the documented commands run, boundary cases are checked,
and a beginner can follow the exercise without untaught concepts.
Update the chapter index and project status after verification.
```

For a correction, a shorter request is enough:

```text
Correct the explanation of integer division in the Chapter 01 lesson.
Check it against the example, preserve exercise behavior, and run the
documentation check. Report any technical inconsistency you find.
```

For review, request concrete findings and evidence rather than a rewrite:

```text
Review Chapter 01 for incorrect behavior, missing prerequisites,
misleading test results, and solution spoilers. Return actionable
findings with file references. Do not edit the lesson.
```

## Choose one agent or several

Use one agent for the initial chapter, a documentation correction, a small bug, or a change whose steps depend on each other. A parallel team can help with independent, substantial tasks when the user requests it, such as separate Android and iOS investigations.

Subagents perform their own model and tool work and consume more tokens than comparable single-agent runs. Treat parallelism as a potential latency or isolation benefit, then weigh it against total work. [Official subagent documentation](https://learn.chatgpt.com/docs/agent-configuration/subagents).

When delegation is requested:

1. Assign each worker an outcome and explicit file ownership. Use read-only review when edits would overlap.
2. Give the relevant paths and decisions, rather than a full transcript where the client permits a smaller handoff.
3. Specify the expected result: changed files, checks, findings, and unresolved questions.
4. Let one coordinator integrate changes and verify the affected boundary.
5. Stop redundant investigation once another worker has supplied sufficient evidence.

Do not delegate a tiny edit to several reviewers or ask every worker to reread the whole curriculum.

## Model and reasoning choices

Respect the user's selected model and client settings. Where contributors choose their own settings, start with a configuration appropriate to the bounded task and escalate for demonstrated failures, difficult debugging, or substantial design work. A short proofreading task and a concurrency investigation need different amounts of reasoning.

Do not hardcode a preferred model or a price table into repository instructions. Availability, accounting, and pricing change. Count success by correct completion and total work, including retries and delegated work, rather than assuming a cheaper individual call produces a cheaper outcome.

## Resume without repeating discovery

Keep one conversation per coherent outcome when practical. Use the client's supported resume or compaction features for long work; a new conversation can start from the latest project status and a bounded request. OpenAI documents prompt structure, reusable guidance, and conversation management in its [best practices](https://learn.chatgpt.com/guides/best-practices).

A useful handoff contains only:

```text
Outcome:
Changed files:
Checks and observed results:
Material decision and reason:
Remaining work or blocker:
Next concrete action:
```

Update `PROJECT_STATUS.md` for material milestone changes. Keep temporary work logs local and avoid copying full command output into tracked context.

## Measure before claiming savings

For comparable tasks, note the model, available usage metrics, number of workers, retries, completion time, and whether the result passed verification. Include delegated usage if the client exposes it. A reduction in printed text alone does not establish a reduction in total tokens or cost.

At this stage no token or monetary savings have been benchmarked. Keep the workflow if it makes work clearer and more reliable; revise it when actual usage shows unnecessary overhead.

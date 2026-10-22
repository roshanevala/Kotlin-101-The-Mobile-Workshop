# Documentation guidance

Apply the repository root guidance as well as these documentation rules.

- Keep learner navigation in the course README and chapters. Put planning, research, agent workflows, and verification records in `docs/maintainers`; the maintainer index is separate from the learning path.
- Keep one canonical owner for each fact: curriculum for topic scope, learning experience for teaching design, research for technology choices, status for current delivery, and GitHub setup for publication details.
- Link to those files instead of repeating whole sections.
- Mark proposals, planned lessons, and observed verification accurately. Never turn a planned feature into a completed feature through wording alone.
- Prefer relative repository links. Keep personal paths, account details, chat transcripts, and temporary logs out of public documents.
- Verify time-sensitive technical claims against official sources when introducing or changing them; attach the source and review date where useful.
- For prose-only changes, run `python3 scripts/check_docs.py` and review clarity. Mobile builds are unnecessary unless executable instructions or code changed.

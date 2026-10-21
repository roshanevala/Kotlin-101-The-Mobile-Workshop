# GitHub repository setup

Use **Kotlin 101 — The Mobile Workshop** as the public display name. Keep the technical name and workshop subtitle: together they communicate the subject and the hands-on format. The repository slug can use ordinary hyphens for a readable URL.

Metadata and commands reviewed on **21 October 2026**. The foundation is published at [roshanevala/Kotlin-101-The-Mobile-Workshop](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop), and local `main` tracks `origin/main`. The public visibility, description, and default branch were verified. Collaboration settings below remain recommendations until applied and verified.

## Repository metadata

| Field | Value |
| --- | --- |
| Display name in README | Kotlin 101 — The Mobile Workshop |
| Repository name | `Kotlin-101-The-Mobile-Workshop` |
| Owner | `roshanevala` |
| Default branch | `main` |
| Visibility | Public |
| Current release state | Course foundation; Chapter 00 available and runnable lessons in development |
| Website field | Leave empty until a reading site exists |
| Template repository | Leave disabled initially |

Copy this description into GitHub's About field:

```text
Learn Kotlin, Android, iOS, and KMP through practical missions, debugging challenges, interview practice, and a growing mobile app.
```

Suggested topics:

```text
kotlin android kotlin-multiplatform jetpack-compose compose-multiplatform
ios swift swiftui coroutines mobile-development interview-preparation
learn-to-code education
```

The topics describe the planned course scope. The README and status file must distinguish implemented lessons from future material.

## Prepared local files

| Path | Purpose |
| --- | --- |
| `AGENTS.md`, `chapters/AGENTS.md`, `docs/AGENTS.md` | Short repository and scoped agent instructions |
| `docs/AGENT_WORKFLOW.md` | Context, delegation, prompt, and handoff practices |
| `docs/PROJECT_STATUS.md` | Compact delivery state for resuming work |
| `.github/copilot-instructions.md` | Route supported Copilot clients to shared instructions |
| `CONTRIBUTING.md` | How learners and contributors can help |
| `.github/ISSUE_TEMPLATE/` | Lesson problem and lesson idea forms |
| `.github/PULL_REQUEST_TEMPLATE.md` | Learner impact and verification |
| `.gitignore`, `.gitattributes` | Local file exclusions and shared line-ending conventions |
| `scripts/check_docs.py` | Offline documentation validation |
| `.github/workflows/docs.yml` | Run documentation checks on GitHub |

Git is initialized locally on `main`. The `origin` remote is `https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop.git`.

## License recommendation

For a simple permissive starting point, consider **MIT for the repository's original material**, keeping required third-party notices. MIT allows reuse and modification subject to its notice requirements. [MIT license summary and text](https://choosealicense.com/licenses/mit/).

If you want a distinct attribution license for teaching prose, consider MIT for code and **CC BY 4.0** for documentation, with explicit coverage in each license notice. CC BY includes attribution and change-indication requirements. [Creative Commons license terms](https://creativecommons.org/licenses/by/4.0/).

No license has been applied yet. Choose the policy and copyright holder before adding the license text. Public visibility and permission to reuse are separate matters; GitHub explains the role of a repository license in its [licensing guidance](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/licensing-a-repository).

## Local verification and commits

From the repository root, run the checks and inspect the proposed contents:

```sh
python3 scripts/check_docs.py
git status --short
git add AGENTS.md README.md CONTRIBUTING.md .gitignore .gitattributes .github chapters docs scripts
```

After choosing and adding a license, stage its file as well. Then review and commit:

```sh
git diff --cached --check
git diff --cached --stat
git diff --cached
git commit -m "docs: improve the workshop learning material"
```

If Git asks for an identity, configure the name and email you want attached to this repository's commits. A GitHub-provided private commit email is an option if you use one. Avoid copying personal machine paths into tracked examples.

## Push to the existing repository

The remote repository already exists. After reviewing and committing the local content, push with Git:

```sh
git remote -v
git push -u origin main
```

If authentication is needed and you use GitHub CLI, run `gh auth login`, then `gh auth setup-git` to configure Git to use that login. These are account setup steps performed locally; do not put tokens in the remote URL or tracked files.

Apply the proposed repository settings after a successful push:

```sh
workshop_repo="roshanevala/Kotlin-101-The-Mobile-Workshop"

gh repo edit "$workshop_repo" \
  --description "Learn Kotlin, Android, iOS, and KMP through practical missions, debugging challenges, interview practice, and a growing mobile app." \
  --default-branch main \
  --enable-issues \
  --enable-discussions \
  --enable-wiki=false \
  --enable-squash-merge \
  --enable-merge-commit=false \
  --enable-rebase-merge=false \
  --delete-branch-on-merge \
  --add-topic kotlin,android,kotlin-multiplatform,jetpack-compose,compose-multiplatform,ios,swift,swiftui,coroutines,mobile-development,interview-preparation,learn-to-code,education
```

See the [GitHub CLI edit reference](https://cli.github.com/manual/gh_repo_edit) for settings supported by your installed CLI. Repository settings and features can also depend on account permissions.

GitHub also documents importing existing local content in its [local repository import guide](https://docs.github.com/en/migrations/importing-source-code/using-the-command-line-to-import-source-code/adding-locally-hosted-code-to-github).

## Collaboration settings

Start with Issues and Discussions enabled and the Wiki disabled so documentation stays with the lessons. Use small pull requests and squash merges. Once the documentation workflow has run successfully, make **Check documentation** a required check on `main` where repository rules are available. Do not require an external reviewer for solo work until another maintainer can provide reviews.

Suggested Discussions categories: Welcome, Help, Show and tell, and Course feedback. Pin a welcome post explaining what is available and where to begin. Add community guidance and a reporting route before actively inviting a wider community.

Suggested issue labels: `lesson-bug`, `clarity`, `setup`, `exercise`, `idea`, `good first issue`, and `help wanted`. Label an issue `good first issue` only when its scope and completion criteria are clear.

Optional milestones: First runnable mission, Starter release covering Chapters 00–03, Kotlin foundations, First Android feature, and First KMP feature. Keep the later curriculum in the roadmap instead of opening dozens of speculative issues.

## Verification and automation

Run `python3 scripts/check_docs.py` with Python 3.9 or later. It checks basic inline local Markdown links and balanced fenced code blocks, skipping generated directories. It does not validate external websites, heading fragments, reference-style links, or the technical correctness of a lesson.

The GitHub workflow runs this checker and a whitespace check. Its checkout action is pinned to the commit published for [actions/checkout v7.0.1](https://github.com/actions/checkout/releases/tag/v7.0.1). Add Android and iOS jobs when those builds exist, using each lesson's verified commands.

The workflow [passed on GitHub for the initial foundation commit](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions/runs/39550166652). Continue checking the workflow for subsequent changes and keep setup instructions consistent with the actual repository settings.

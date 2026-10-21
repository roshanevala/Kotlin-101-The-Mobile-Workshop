#!/usr/bin/env python3
"""Check basic inline local Markdown links and fenced code blocks, offline."""

import argparse
import os
from pathlib import Path
import re
from urllib.parse import unquote, urlsplit


SKIP_DIRS = {".git", ".gradle", ".kotlin", ".venv", "build", "node_modules", "Pods", ".agent-local"}
LINK = re.compile(r'!?\[[^\]\n]*\]\((<[^>\n]+>|[^\s)\n]+)(?:\s+"[^"\n]*")?\)')
FENCE = re.compile(r"^ {0,3}(`{3,}|~{3,})(.*)$")


def markdown_files(root):
    for directory, subdirs, files in os.walk(root, followlinks=False):
        subdirs[:] = sorted(name for name in subdirs if name not in SKIP_DIRS)
        for name in sorted(files):
            if name.endswith(".md"):
                yield Path(directory) / name


def check_file(path, root):
    errors = []
    label = path.relative_to(root)
    fence = None
    for number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        marker = FENCE.match(line)
        if fence:
            if marker and marker[1][0] == fence[0] and len(marker[1]) >= fence[1] and not marker[2].strip():
                fence = None
            continue
        if marker:
            fence = (marker[1][0], len(marker[1]), number)
            continue
        # Inline code is illustrative text, not a rendered Markdown link.
        visible = re.sub(r"(`+).*?\1", "", line)
        for match in LINK.finditer(visible):
            target = match[1].strip("<>")
            try:
                url = urlsplit(target)
            except ValueError:
                errors.append(f"{label}:{number}: malformed target {target}")
                continue
            if url.scheme or url.netloc or not url.path:
                continue
            relative = unquote(url.path)
            destination = (root / relative.lstrip("/") if relative.startswith("/") else path.parent / relative).resolve()
            if not destination.is_relative_to(root):
                errors.append(f"{label}:{number}: link leaves repository: {target}")
            elif not destination.exists():
                errors.append(f"{label}:{number}: missing target: {target}")
    if fence:
        errors.append(f"{label}:{fence[2]}: unclosed code fence")
    return errors


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    root = parser.parse_args().root.resolve()
    if not root.is_dir():
        parser.error(f"not a directory: {root}")
    files = list(markdown_files(root))
    if not files:
        print("No Markdown files found.")
        return 1
    errors = [error for path in files for error in check_file(path, root)]
    if errors:
        print("\n".join(errors))
        print(f"Documentation check failed: {len(errors)} issue(s).")
        return 1
    print(f"Documentation check passed: {len(files)} Markdown files.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

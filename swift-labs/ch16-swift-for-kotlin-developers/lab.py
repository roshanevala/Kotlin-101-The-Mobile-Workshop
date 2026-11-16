#!/usr/bin/env python3
"""Build the Chapter 16 Swift mission with one explicit source choice."""

import argparse
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parent
BUILD = ROOT / ".build"
PRACTICE = ROOT / "src/practice/StudyPlan.swift"


def prepare():
    PRACTICE.parent.mkdir(parents=True, exist_ok=True)
    try:
        with PRACTICE.open("xb") as target:
            target.write((ROOT / "src/starter/StudyPlan.swift").read_bytes())
        print("Created src/practice/StudyPlan.swift", flush=True)
    except FileExistsError:
        pass


def compile_and_run(sources, output, optimized):
    output.parent.mkdir(parents=True, exist_ok=True)
    cache = BUILD / "module-cache"
    cache.mkdir(parents=True, exist_ok=True)
    subprocess.run([
        "xcrun", "swiftc", "-swift-version", "6",
        "-O" if optimized else "-Onone", "-module-cache-path", str(cache),
        *(str(source) for source in sources), "-o", str(output),
    ], check=True)
    subprocess.run([str(output)], check=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("action", choices=(
        "prepare", "run", "test", "run-solution", "check-solution", "examples", "clean"
    ))
    parser.add_argument("--optimized", action="store_true", help="Use -O instead of -Onone.")
    args = parser.parse_args()
    if args.action == "clean":
        if BUILD.exists():
            shutil.rmtree(BUILD)
        return
    if args.action == "prepare":
        prepare()
        return
    mode = "optimized" if args.optimized else "debug"
    if args.action == "examples":
        for source in sorted((ROOT / "examples").glob("*.swift")):
            print(f"Example {source.stem}", flush=True)
            compile_and_run([source], BUILD / f"examples-{mode}" / source.stem, args.optimized)
        return
    reference = args.action in ("run-solution", "check-solution")
    if not reference:
        prepare()
    source = ROOT / "src/solution/StudyPlan.swift" if reference else PRACTICE
    checks = args.action in ("test", "check-solution")
    entry = ROOT / ("src/checks/main.swift" if checks else "src/runner/main.swift")
    variant = "reference" if reference else "practice"
    compile_and_run([source, entry], BUILD / f"{variant}-{mode}" / ("checks" if checks else "mission"), args.optimized)


if __name__ == "__main__":
    try:
        main()
    except FileNotFoundError:
        print("Tool or source missing. Check Xcode/xcrun setup and the chapter files.", file=sys.stderr)
        sys.exit(2)
    except subprocess.CalledProcessError as error:
        sys.exit(error.returncode if error.returncode > 0 else 1)

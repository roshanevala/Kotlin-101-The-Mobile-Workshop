#!/usr/bin/env python3
"""Prepare and build one native Study Lab source choice."""

import argparse
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parent
BUILD = ROOT / "build"


def prepare():
    practice = ROOT / "Practice"
    practice.mkdir(exist_ok=True)
    for name in ("StudyScreens.swift", "ReviewLoader.swift", "ReviewRegressionTests.swift"):
        answer = practice / name
        template = (ROOT / "Starter" / name).read_bytes()
        try:
            with answer.open("xb") as target:
                target.write(template)
            print(f"Created Practice/{name}", flush=True)
        except FileExistsError:
            pass


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("action", choices=("prepare", "build", "build-tests", "run", "test", "test-unit", "check-model", "clean"))
    parser.add_argument("--reference", action="store_true")
    parser.add_argument("--device", help="Explicit iOS simulator UDID, required for run/test.")
    parser.add_argument("--only-test", action="append", default=[], help="Xcode test identifier to run; repeat for several tests.")
    args = parser.parse_args()
    if args.action == "clean":
        if BUILD.exists():
            shutil.rmtree(BUILD)
        return
    if args.action == "prepare":
        prepare()
        return
    if args.action == "check-model":
        output = BUILD / "model-checks"
        cache = BUILD / "model-module-cache"
        cache.mkdir(parents=True, exist_ok=True)
        subprocess.run(["xcrun", "swiftc", "-swift-version", "6", "-module-cache-path", str(cache),
                        str(ROOT / "Sources/StudySession.swift"), str(ROOT / "Sources/StudyStore.swift"),
                        str(ROOT / "Tests/ModelChecks/main.swift"), "-o", str(output)], check=True)
        subprocess.run([str(output)], check=True)
        return
    if args.action in ("run", "test", "test-unit") and not args.device:
        parser.error("run/test/test-unit require --device with the intended simulator UDID")
    if args.only_test and args.action != "test":
        parser.error("--only-test is available with the test action")
    if not args.reference:
        prepare()
    variant = "reference" if args.reference else "practice"
    scheme = "StudyLabReference" if args.reference else "StudyLabPractice"
    destination = f"platform=iOS Simulator,id={args.device}" if args.device else "generic/platform=iOS Simulator"
    command = ["xcodebuild", "-project", str(ROOT / "StudyLab.xcodeproj"), "-scheme", scheme,
               "-configuration", "Debug", "-sdk", "iphonesimulator", "-destination", destination,
               "-derivedDataPath", str(BUILD / variant), "CODE_SIGNING_ALLOWED=NO"]
    if args.action in ("test", "test-unit"):
        unit_only = args.action == "test-unit"
        result = BUILD / variant / ("unit.xcresult" if unit_only else "results.xcresult")
        if result.exists():
            shutil.rmtree(result)
        command += ["-parallel-testing-enabled", "NO", "-resultBundlePath", str(result), "test"]
        if unit_only:
            command.append(f"-only-testing:{scheme}Tests")
        command += [f"-only-testing:{identifier}" for identifier in args.only_test]
    elif args.action == "build-tests":
        command.append("build-for-testing")
    else:
        command.append("build")
    subprocess.run(command, check=True)
    if args.action == "run":
        app = BUILD / variant / "Build/Products/Debug-iphonesimulator" / f"{scheme}.app"
        subprocess.run(["xcrun", "simctl", "install", args.device, str(app)], check=True)
        identifier = f"workshop.studylab.ios.{variant}"
        subprocess.run(["xcrun", "simctl", "launch", "--terminate-running-process", args.device, identifier], check=True)


if __name__ == "__main__":
    try:
        main()
    except FileNotFoundError:
        print("Tool or source missing. Check Xcode setup and chapter files.", file=sys.stderr)
        sys.exit(2)
    except subprocess.CalledProcessError as error:
        sys.exit(error.returncode if error.returncode > 0 else 1)

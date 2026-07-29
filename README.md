[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9b4cb2fec91146649dcf514278f24eab)](https://www.codacy.com/gh/codacy/codacy-duplication-jscpd?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=codacy/codacy-duplication-jscpd&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/9b4cb2fec91146649dcf514278f24eab)](https://www.codacy.com/gh/codacy/codacy-duplication-jscpd?utm_source=github.com&utm_medium=referral&utm_content=codacy/codacy-duplication-jscpd&utm_campaign=Badge_Coverage)
[![Build Status](https://circleci.com/gh/codacy/codacy-duplication-jscpd.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/codacy-duplication-jscpd)
[![Docker Version](https://images.microbadger.com/badges/version/codacy/codacy-duplication-jscpd.svg)](https://microbadger.com/images/codacy/codacy-duplication-jscpd "Get your own version badge on microbadger.com")

# Codacy Duplication jscpd

This is the duplication docker we use at Codacy to have [jscpd](https://github.com/kucherenko/jscpd) support.
You can also create a docker to integrate the tool and language of your choice!

## Usage

You can create the docker by doing:

```bash
sbt graalvm-native-image:packageBin
docker build -t codacy-duplication-jscpd .
```

You can run the docker with the following command:

```bash
docker run -it -v $srcDir:/src codacy-duplication-jscpd
```

## Test

For a faster development loop you can create a Docker image based on the JVM instead of creating a native-image:

```bash
sbt universal:stage
docker build -t codacy-duplication-jscpd --target dev .
```

We use the [codacy-plugins-test](https://github.com/codacy/codacy-plugins-test) to test our external tools integration.
You can follow the instructions there to make sure your tool is working as expected.

## Agent Playbook: Updating This Repository End-to-End

This section is written for an AI coding agent (or a human) tasked with updating this repo — most commonly bumping the wrapped `jscpd` npm package version, but also base image / orb bumps. Follow it top to bottom.

### 1. What this repository is

This is a **Codacy duplication engine**, not a pattern-based linting engine. It is a thin Scala wrapper (`src/main/scala/com/codacy/duplication/jscpd/Jscpd.scala`, built on `codacy-duplication-scala-seed`) that shells out to the real [jscpd](https://github.com/kucherenko/jscpd) CLI (`/node_modules/jscpd/bin/jscpd`, installed as an npm dependency and baked into the Docker image) to detect copy-pasted code, parses its JSON report, and converts it into Codacy's `DuplicationClone` API model.

There is **no `docs/patterns.json`** and **no pattern/rule catalog** — duplication tools don't have configurable rules the way linters do. The only thing under `docs/` is `docs/duplication-tests/`, a set of fixture directories (source files + expected `results.xml`) consumed by `codacy-plugins-test`'s duplication-test mode. There is no doc-generation step and nothing under `docs/` needs regenerating.

### 2. Files that encode versions — check all of these on every update

| File | What it controls | What to check |
|---|---|---|
| `package.json` → `dependencies.jscpd` | Which jscpd release is bundled | Bump the semver range/version to the target release. |
| `package-lock.json` | Locked/resolved jscpd version and its transitive deps | Regenerate via `npm install` after changing `package.json` — do not hand-edit. |
| `Dockerfile` → `ARG alpine_version` | Base OS image for both builder and runtime stages | Only bump if required (e.g. to pick up security fixes); check history for precedent (`Bump alpine version`, `Fix vulnerabilities` commits). |
| `build.sbt` → `com.codacy` %% `codacy-duplication-scala-seed` | The Codacy Scala seed/framework version this wrapper is built on | Bump only if the task explicitly calls for it; check the latest published version on Maven. |
| `.circleci/config.yml` → `codacy/base` orb | Shared CircleCI steps (checkout, sbt, docker publish) | Check the latest published orb version. |
| `.circleci/config.yml` → `codacy/plugins-test` orb | Runs `codacy-plugins-test` in CI (here with `run_duplication_tests: true`) | Same as above. |
| `.github/dependabot.yml` | Automated npm bump PRs and any pinned exclusions | Check for a stale `ignore` entry (e.g. an old jscpd version pin) that may block or conflict with a manual bump. |

Git history confirms the pattern: every prior jscpd bump (e.g. `build(deps): Bump jscpd from 4.0.5 to 4.2.4`) touched only `package.json` and `package-lock.json`. Base-image and orb bumps are separate, occasional commits.

### 3. Step-by-step update procedure

1. **Bump the version(s)** as scoped by the task (e.g. `npm install jscpd@<version>` to update both `package.json` and `package-lock.json` consistently — do not hand-edit the lockfile).
2. **Compile and format**: `sbt scalafmtSbt scalafmtAll` (matches the CI `populate_cache_and_compile` job).
3. **Build the Docker image** for a fast local dev loop:
   ```bash
   sbt universal:stage
   docker build -t codacy-duplication-jscpd --target dev .
   ```
   (Or build the full native-image release with `sbt graalvm-native-image:packageBin && docker build -t codacy-duplication-jscpd .`, matching CI's `publish_docker_local` job — slower, but what actually gets published.)
4. **Run `codacy-plugins-test` locally** before pushing — clone https://github.com/codacy/codacy-plugins-test and run its duplication-test mode against your local image tag and the fixtures in `docs/duplication-tests/`.
5. **Iterate on failures**, re-running only the relevant test command after each fix.
6. **Commit** the version bump(s) (e.g. `package.json` + `package-lock.json` together) in one change.
7. **Push and open a PR.**
8. **Poll the PR's real CI checks until they all pass — local validation is NOT the finish line.** After every push, run `gh pr checks <pr-url>` and keep re-polling (short sleep while any check is `pending`) until all checks finish. If a check fails, fetch its actual log (don't guess), find the true root cause, fix it, push again (never `--no-verify`, never force-push), and re-poll. Repeat until every check is green. **The CI environment's toolchain can differ from your local one**, so a clean local run does not guarantee CI passes. Only stop iterating when every check passes, or you hit a genuine product/infra decision that needs a human.

### 4. Common failure modes and fixes

- A jscpd major-version bump can change its CLI flags or JSON report shape (`jscpd-report.json` structure, field names like `fragment`/`tokens`/`lines`/`firstFile`/`secondFile`). If `plugins_test` duplication checks fail after a bump, diff the new jscpd CHANGELOG against `Jscpd.scala`'s parsing logic before assuming a fixture is stale.
- `package-lock.json` diffs can be very large (hundreds of lines) for a single dependency bump due to transitive dependency shuffling — this is expected and should not be hand-trimmed.
- `.github/dependabot.yml` has an `ignore` block for a specific old jscpd version; if a manual bump target overlaps with an ignored version, dependabot won't reconcile it automatically — that's fine for a one-off manual bump but worth knowing.

### 5. Definition of done

- Version bump(s) reflected in all files that encode them (`package.json`, `package-lock.json`, and `Dockerfile`/`build.sbt`/`.circleci/config.yml` if in scope).
- `sbt scalafmtSbt scalafmtAll` passes.
- Docker image builds successfully (dev target at minimum; native-image target if validating a release build).
- `codacy-plugins-test` duplication-test commands pass locally against the freshly built image.
- **After pushing and opening/updating the PR, every CI check on it is green.** Poll `gh pr checks <pr-url>` and iterate on any failure until all pass.

## What is Codacy?

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features:

- Identify new Static Analysis issues
- Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
- Auto-comments on Commits and Pull Requests
- Integrations with Slack, HipChat, Jira, YouTrack
- Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.

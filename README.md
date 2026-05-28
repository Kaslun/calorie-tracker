# Kalori

Personal Android calorie and nutrition tracker. Norway-focused, ADHD-friendly, built around frictionless logging, honest weight projection, and Health Connect sync. Single user.

## Documents
- `CLAUDE_CODE_HANDOFF.md` — engineering spec: stack, architecture, data model, food sources, Health Connect, deployment, and the supervisor/subagent build plan.
- `DESIGN_HANDOFF.md` — visual language, tone, motion, screens, and states for the designer.
- `.claude/agents/` — subagent definitions (orchestrator + 6 specialists) for Claude Code.

## Build entry point
Open this repo in Claude Code and invoke the `orchestrator` agent. It reads both handoffs, works the build phases in order, dispatches the specialist subagents, reviews their output, builds, and commits at each phase boundary.

## Getting it on a phone
- Dev loop: wireless ADB + `./gradlew installDebug`; LiveEdit for most Compose edits.
- Carry build: `./gradlew assembleDebug` → sideload the debug APK.
- v1.1: GitHub Releases + in-app update check for cable-free OTA.
- Stay on debug signing to preserve the local database across installs.

## Settled decisions (do not relitigate)
No streaks, no cheat/flex-day mechanic. Frozen nutrition snapshots on log entries. `null` nutrient ≠ `0.0`. Weekly (not daily) target adherence. Projection from day 3 with a confidence band. User-confirmed TDEE recalibration. Health Connect owns weight; the app owns nutrition.

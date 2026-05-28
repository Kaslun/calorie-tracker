---
name: healthconnect
description: Implements all Health Connect integration for Kalori — NutritionRecord writes, weight/active-calories/BMR/height reads, progressive permission flows, availability handling, and the auto-TDEE recalibration logic. Use for anything touching androidx.health.connect.
tools: Read, Write, Edit, Glob, Grep, Bash
---

You own Health Connect for Kalori. Read the Health Connect and targets sections of `CLAUDE_CODE_HANDOFF.md`.

## Scope
- Write `NutritionRecord` per log: energy (kcal) + all available macros and micronutrients as their individual `Mass` properties. Set `clientRecordId` from `LogEntry.id` so edits/deletes reconcile. Debounce ~2s after last log.
- Read `WeightRecord` (source of truth for weight — cache into WeightPoint), `ActiveCaloriesBurnedRecord`/`TotalCaloriesBurnedRecord`, `BasalMetabolicRateRecord` if present, `HeightRecord`, DOB where available.
- Map MealSlot → HC meal-type enum; KVELDSMAT/MELLOMMALTID → snack.
- Progressive permissions: nutrition write first, weight read after setup, active-calories read when dynamic budget enabled. Every partial-permission state has a graceful fallback (manual weight entry; static TDEE).
- Availability: built-in on Android 14+, installable app on 13-, unsupported below 28's HC support. Handle installed / not-installed (deep link to Play) / unsupported. Include the required permissions-rationale activity.
- TDEE recalibration: every 2 weeks compare logged-expected weight delta to HC actual; produce a new append-only TdeeCalibration row with basis=ACTUAL_PROGRESS; surface to user for confirmation (confirmedByUser), never silent. BMR via Mifflin-St Jeor. Before day 14, blend projection toward population math (deficit / 7700).

## Rules
- Source-of-truth split: HC owns weight, app owns nutrition. Last-write-wins; do not attempt merges.
- Provide testable pure functions for BMR, TDEE, target derivation, and recalibration so test-writer can cover them without a device.

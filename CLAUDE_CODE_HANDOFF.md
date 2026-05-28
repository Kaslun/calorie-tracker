# Kalori — Engineering handoff (Claude Code)

A personal Android calorie and nutrition tracker. Single user. Norway-focused. Built around frictionless logging, honest progress projection, and Health Connect sync. This document is the build spec. It is paired with `DESIGN_HANDOFF.md` (visual and interaction brief) and the agent definitions in `.claude/agents/`.

## What this app is for

The owner is losing weight by tracking calories. Every prior attempt failed for one reason: logging food was too much work, so the numbers became unreliable, so the projected progress never matched reality, so it felt pointless. This app's entire reason to exist is to make logging fast enough and accurate enough that the progress projection becomes trustworthy and therefore motivating.

The owner has ADHD. Friction kills the habit. Design and build accordingly: defaults over decisions, recents over search, two taps over ten.

Not an eating-disorder context. The owner wants direct, honest numbers including projections, not gentle obfuscation. No shame language, no celebration language, no coaching tone. Numbers speak for themselves.

## Hard product decisions already made

These are settled. Do not relitigate them in code comments or restructure around alternatives.

- No streaks. No "cheat day" / "flex day" mechanic. These were considered and rejected as net-harmful for this user.
- Logging is the daily behavior; target adherence is surfaced weekly; weight progress is the long-term motivator. Three screens, three jobs.
- Calorie target cares about the goal (this is a weight-loss tool), but adherence is shown at the weekly level, never as daily pass/fail.
- Projection is a core motivator and ships from day 3, with a confidence band that is wide early and narrows as personal data accumulates. Before day 14, blend toward population-standard math (deficit / 7700 kcal per kg).
- Auto-TDEE recalibration every 2 weeks, comparing logged-expected weight change to Health Connect actual, surfaced to the user with a confirmation step (not silent).
- Health Connect is the source of truth for weight; the app is the source of truth for nutrition.
- Setup is 3 core questions (sex, goal weight, goal pace); everything else is derived or pulled from Health Connect.

## Tech stack

- Kotlin, Jetpack Compose, Material 3 (heavily themed per design handoff)
- Single-activity, Compose Navigation. No fragments, no XML layouts.
- Min SDK 28 (Android 9). Health Connect requires SDK 28+. Target latest stable SDK.
- Room for local persistence
- DataStore (Preferences) for settings/flags
- CameraX + ML Kit Barcode Scanning (offline-capable, no API key)
- Retrofit + kotlinx.serialization for the Open Food Facts API (the only network dependency)
- androidx.health.connect:connect-client (use latest stable; alpha/beta channels move fast — pin a version and note it)
- Jetpack Glance for the home-screen widget
- WorkManager for widget refresh and the periodic TDEE-recalibration check
- Coil for food images
- Lottie only if the designer supplies Lottie assets; otherwise Compose animation

No backend. No account. No cloud sync in v1. Everything local.

## Architecture

Clean-ish, pragmatic. Don't over-modularize for a personal app.

- Single `:app` module for v1. Resist premature feature-module splitting.
- Keep the design system (colors, type, spacing, shared components) in a dedicated `ui/theme` + `ui/components` package so designer-driven token changes land in one place and propagate via LiveEdit. This is the single biggest lever for fast design iteration — treat it as a real boundary even though it's not a separate Gradle module.
- Repository pattern with interfaces (`FoodRepository`, `LogRepository`, `WeightRepository`, `GoalRepository`). Provide in-memory fakes for previews and tests. Every screen must render from a fake with no DB or network.
- ViewModels expose immutable UI state (a sealed/`data class` state per screen). Unidirectional data flow.
- DI: Hilt.

### Build flavors

- `debug`: developer menu (long-press the version string in Settings) that can jump to any date, seed fake logs, reset goal/calibration, force a recalibration prompt, and toggle every feature flag. Pre-populate the DB with a realistic set of Norwegian foods + a few weeks of logs on first debug install so design iteration never requires manually logging "havregryn" 50 times.
- `release`: stripped, no dev menu, no seed data.

Keep behavior identical between flavors apart from the dev affordances. Feature flags are plain Kotlin booleans in a single config object — no remote config.

## Data model

Authoritative. Implement as Room entities. The full rationale lives in this conversation; the load-bearing rules:

- **Foods store nutrition per 100g or per unit**, never per serving. Portions are applied at log time.
- **Log entries store a frozen, resolved nutrition snapshot** of what was actually consumed, plus a `foodId` reference for editing. The snapshot is the source of truth for all sums, trends, and projections. Editing a food later must not retroactively mutate historical logs.
- **`null` nutrient != `0.0`**. Null means unknown and is excluded from sums; the count of contributing items is tracked so the UI can honestly say "Vitamin D: 12 µg from 6 of 14 logged items."
- **`date` (the day it counts toward) is separate from `timestamp`** (when consumed/logged), so the end-of-day catch-up flow can assign late entries to the correct day.

```kotlin
// Embedded value object — same shape on Food, MealComponent (resolved), LogEntry
data class Nutrition(
    val kcal: Double,
    val protein: Double?, val carbs: Double?, val sugars: Double?,
    val fat: Double?, val saturatedFat: Double?, val fiber: Double?, val salt: Double?,
    val micros: Map<Micronutrient, Double>? // JSON column; sparse; null/absent = unknown
)

enum class Micronutrient {
    VITAMIN_A, VITAMIN_D, VITAMIN_E, VITAMIN_K,
    VITAMIN_B1, VITAMIN_B2, VITAMIN_B3, VITAMIN_B6, VITAMIN_B9, VITAMIN_B12, VITAMIN_C,
    CALCIUM, IRON, MAGNESIUM, PHOSPHORUS, POTASSIUM,
    SODIUM, ZINC, SELENIUM, IODINE, COPPER
}

enum class FoodSource { OFF, MATVARETABELLEN, MANUAL, RESTAURANT }
enum class NutritionBasis { PER_100G, PER_UNIT }
enum class MealSlot { FROKOST, LUNSJ, MELLOMMALTID, MIDDAG, KVELDSMAT }
enum class Confidence { HIGH, MEDIUM, LOW }
enum class Sex { MALE, FEMALE }       // metabolic input for BMR, not gender
enum class TargetMode { AUTO, MANUAL }
enum class CalibrationBasis { INITIAL_ESTIMATE, ACTUAL_PROGRESS }

data class Portion(val label: String, val grams: Double?, val units: Double?)

@Entity data class Food(
    @PrimaryKey val id: String,
    val name: String, val brand: String?, val barcode: String?,
    val source: FoodSource, val sourceRef: String?,
    val basis: NutritionBasis,
    @Embedded val nutrition: Nutrition,
    @Embedded(prefix = "def_") val defaultPortion: Portion?,
    val isFavorite: Boolean = false,
    val createdAt: Instant, val lastUsedAt: Instant?,
    val useCount: Int = 0, val isArchived: Boolean = false
)

@Entity data class FoodPortion( // all known portions for a food: package serving, custom, visual refs
    @PrimaryKey val id: String, val foodId: String,
    @Embedded val portion: Portion, val isPackageDefault: Boolean = false
)

@Entity data class Meal(
    @PrimaryKey val id: String, val name: String,
    val isFavorite: Boolean = false, val lastUsedAt: Instant?,
    val useCount: Int = 0, val createdAt: Instant
)

@Entity data class MealComponent(
    @PrimaryKey val id: String, val mealId: String, val foodId: String,
    @Embedded val portion: Portion
)

@Entity data class LogEntry(
    @PrimaryKey val id: String,
    val date: LocalDate, val timestamp: Instant, val mealSlot: MealSlot,
    val foodId: String?,            // null for quick-add
    val sourceMealId: String?,      // set if expanded from a Meal
    @Embedded val portion: Portion?,
    @Embedded val nutrition: Nutrition, // RESOLVED, frozen snapshot
    val confidence: Confidence,
    val photoPath: String?, val note: String?,
    val healthConnectId: String?    // clientRecordId written to HC; enables update/delete
)

@Entity data class WeightPoint(   // cache only; Health Connect is source of truth
    @PrimaryKey val date: LocalDate, val kg: Double, val source: String
)

@Entity data class UserGoal(
    @PrimaryKey val id: Int = 1,
    val sex: Sex, val heightCm: Double, val birthDate: LocalDate,
    val goalWeightKg: Double, val paceKgPerWeek: Double, // 0.25 / 0.5 / 0.75
    val proteinTargetG: Double, val proteinTargetMode: TargetMode,
    val acceptableRangeKcal: Int = 100, val createdAt: Instant
)

@Entity data class TdeeCalibration(  // append-only history
    @PrimaryKey val id: String,
    val effectiveDate: LocalDate, val tdee: Double, val bmr: Double,
    val basis: CalibrationBasis,
    val expectedDeltaKg: Double?, val actualDeltaKg: Double?,
    val confirmedByUser: Boolean
)
```

Storage notes: flatten the common macros (kcal, protein, carbs, fat, fiber) into Room columns for cheap aggregate queries; store `micros` as a JSON string via a `TypeConverter`. Use `@Embedded(prefix=...)` to avoid column collisions where `Portion`/`Nutrition` appear more than once. Use Instant/LocalDate type converters.

### Derived values (compute, don't store)

- **Current daily calorie target** = effective TDEE − (paceKgPerWeek × 7700 / 7). Effective TDEE = the `tdee` of the most recent `TdeeCalibration` with `effectiveDate <= date`.
- **Daily totals** = sum of `LogEntry.nutrition` for `date`, nulls excluded per nutrient, with contributing-item counts retained for the micronutrient honesty display.
- **7-day weight average** and **projection** computed on read from `WeightPoint` + recent `LogEntry` sums. Cheap at personal scale; do not cache.

## Food data sources

1. **Barcode scan** → check local `Food` cache by barcode → Open Food Facts API (`https://world.openfoodfacts.org/api/v2/product/{barcode}.json`) → if miss, fast manual-add flow. Every scanned/added item is cached permanently.
2. **Matvaretabellen** (matvaretabellen.no) CSV bundled at build time (~2000 Norwegian foods, full nutrient profile incl. NNR-relevant micros). This is the micronutrient backbone. Build an import pipeline that loads it into Room on first run (or ships as a pre-built DB asset).
3. **Manual + Restaurant** foods the user creates. Pre-seed a small set of Norwegian chains that publish nutrition (McDonald's, Burger King, Subway, Max).

Expect frequent OFF misses on Norwegian store brands (First Price, Coop, X-tra, Eldorado). The "scanner found nothing → add it in seconds" path is a primary flow, not an edge case. Make it fast and non-punishing.

## Targets and references

- Use **Nordic Nutrition Recommendations 2023 (NNR2023)**, not US RDAs, for vitamin/mineral targets. Bundle values segmented by sex and age band. User-overridable.
- Protein target default 1.8 g/kg of goal body weight (range 1.6–2.2), AUTO mode; MANUAL override available.
- BMR via Mifflin-St Jeor. Activity comes from Health Connect active calories (trailing 7–14 days), not a self-reported questionnaire. If no HC data, default "lightly active" and let auto-recalibration correct it.

## Health Connect

- `NutritionRecord` write per log entry: `energy` (kcal) plus all available macros and micronutrients (the record supports individual nutrient properties as `Mass`, e.g. `dietaryFiber`, `potassium`, `vitaminB6`, `vitaminC`). Set `clientRecordId` from `LogEntry.id` so edits/deletes reconcile.
- Read `WeightRecord`, `ActiveCaloriesBurnedRecord`/`TotalCaloriesBurnedRecord`, `BasalMetabolicRateRecord` if present, `HeightRecord`, date-of-birth where available.
- Map `MealSlot` to HC meal-type enum; `KVELDSMAT`/`MELLOMMALTID` → snack.
- Request permissions progressively (nutrition write first; weight read after setup; active-calories read when dynamic budget is enabled). Handle every partial-permission state with a graceful fallback (manual weight entry, static TDEE).
- Availability: built-in on Android 14+; a separate installable app on 13 and below. Handle installed / not-installed (deep link to Play) / unsupported. Include the required permissions-rationale activity.
- Writes are synchronous and fast; no WorkManager needed for HC. Debounce writes ~2s after the last log.

## Deployment to device

- Dev loop: wireless ADB + `./gradlew installDebug`; LiveEdit for most Compose changes.
- Carry build: `./gradlew assembleDebug` → debug-signed APK, sideload.
- v1.1 optional: GitHub Releases + in-app update check (compare versionCode against latest release via GitHub API, prompt to download). Owner has a GitHub MCP connected and wants low-friction OTA.
- Stay on debug signing throughout to preserve the local DB across installs (signature mismatch forces uninstall → wipes Room). If a re-sign is ever planned, ship CSV export first.

---

# Agent orchestration

Use a supervisor (orchestrator) plus focused subagents defined in `.claude/agents/`. The honest scope note: this app is small enough that the value is **role separation and clean per-agent context**, not heavy parallelism. Run subagents one or two at a time; only parallelize genuinely independent work (e.g. data layer vs. design-system scaffolding). Every inter-agent message is a model round trip — keep orchestrator instructions tight and don't spawn agents for trivial tasks.

Two field-tested cautions, both enforced in the agent files:
- **Restrict each subagent's tool allowlist.** A documented incident had a refactor agent with inherited `Bash` run `git reset` and destroy uncommitted work. Give Bash only to agents that need it, and never a destructive-git-capable agent that also auto-runs.
- **Commit at integration points.** The orchestrator commits after each phase so a misbehaving agent can't wipe much.

## Roles

- **orchestrator** — owns sequencing, phase gates, integration, and all git commits/pushes. Decomposes the build plan into subagent tasks, dispatches them, reviews returned diffs, runs the build, commits. Does not write feature code itself beyond glue.
- **android-architect** — project scaffolding, Gradle/Hilt/Room/Navigation setup, DI graph, repository interfaces + fakes, build flavors, dev menu. Runs first; most other work depends on it.
- **data-layer** — Room entities/DAOs/converters, Matvaretabellen import pipeline, OFF Retrofit client, repositories. Owns the data model above verbatim.
- **feature-dev** — Compose screens + ViewModels, one feature at a time, rendering from fakes. Consumes the design system.
- **healthconnect** — all Health Connect read/write, permission flows, availability handling, recalibration logic.
- **test-writer** — unit tests for nutrition math, projection, TDEE recalibration, portion resolution; DAO tests.
- **reviewer** — read-only diff review against this spec and the design handoff before the orchestrator commits.

## Suggested build phases (orchestrator drives)

1. **Scaffold** (android-architect): project, DI, Navigation, theme/component package stubs, repository interfaces + fakes, flavors, dev menu, seed-data harness. Commit.
2. **Data layer** (data-layer, can overlap design-system stubs): entities, DAOs, converters, Matvaretabellen importer, OFF client, repos with fakes. Tests for nutrition resolution. Commit.
3. **Core logging loop** (feature-dev + healthconnect): Today screen, Add-food (scan/recents/search), portion picker, LogEntry write, HC nutrition write. This is the heartbeat — get it under 10 seconds end to end. Commit.
4. **Meals + quick-add + recurring + catch-up** (feature-dev). Commit.
5. **Tracking** (feature-dev + healthconnect): weight read + 7-day smoothing, projection with confidence band, weekly stats, TDEE recalibration + confirmation. Commit.
6. **Macros + micronutrients UI** (feature-dev). Commit.
7. **Widget** (feature-dev): medium Glance widget with time-aware quick-log. Commit.
8. **Polish**: animations/haptics per design handoff, empty/error/loading states. Commit.

Micronutrient tracking, pattern-learning meal-slot adaptation, extra widget sizes, and GitHub OTA are explicitly v1.1 — keep them behind feature flags if started early.

## Definition of done per feature

Renders from a fake repository in a `@Preview` across empty/loaded/error states; ViewModel state is immutable and unidirectional; no business logic in composables; nutrition math has unit tests; passes reviewer against this spec and the design handoff.

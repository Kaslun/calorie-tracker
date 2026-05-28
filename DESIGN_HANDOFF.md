# Kalori — Design handoff (Claude Design)

A personal Android calorie and nutrition tracker. One user. The design has to carry the experience through intuitiveness and interaction, not through instructional copy. This is paired with `CLAUDE_CODE_HANDOFF.md` (the build spec).

## Who this is for

One person, losing weight, with ADHD. Two facts drive every design choice:

The owner abandoned every previous tracker because logging was too slow and fiddly, which made the data unreliable and the whole exercise feel pointless. So the design's first job is to make logging feel instant and almost automatic.

The owner is self-conscious about their weight and size — not about the act of tracking, and with no eating-disorder history. They want honest, direct numbers including a forward projection of their weight. They do not want softened or hidden outcomes. The risk to avoid is not bluntness; it's anything that feels childish, patronizing, or moralizing about food and bodies.

## Tone

Direct, adult, neutral. Think a well-made productivity tool, not a fitness app.

- No shame language ("over budget," "you failed"), no celebration language ("amazing!", "crushing it"), no coaching ("try a smaller portion"). Numbers speak for themselves.
- No "you" framing. "Today," not "Your day."
- No emoji in UI text. Icons yes, emoji no.
- No motivational copy anywhere. A logged item appearing is the feedback. A projection line moving is the reward.
- Minimal text generally: no tooltips, no onboarding overlays, no "tap here" hints. Affordances communicate, not instructions. Settings labels are short ("Haptics," not "Enable haptic confirmation feedback").

Reference apps to study and match in spirit: Things 3, Streaks, Bear, Apple Health, Linear, Arc. Explicitly NOT references: MyFitnessPal, Lose It!, Noom, or anything with a green-and-blue gradient and a coaching voice. This is closer to a productivity app than a fitness app — say that to yourself on every screen.

## Settled product decisions that affect design

- No streaks. No "cheat day"/"flex day." Do not design any chain, flame, or earned-reward mechanic.
- Three screens, three jobs: Today (fast logging, calm), Stats (honest weekly adherence), Trend (the long-term motivator — weight + projection).
- The daily view never shows pass/fail. Going over the target is visible but not alarming.
- The projection graph is the emotional center of the app. It is honest: a band, not a single confident line, wide early and narrowing as data accumulates.

## Visual system to define

Deliver a full system, light and dark:

- Color: a restrained palette. One primary, neutrals, and at most one secondary accent. Crucially — define how "over target" reads. Not red, not a warning. A subtle warmer shift only when significantly over (e.g. >300 kcal); otherwise the number just is what it is. Going over is information, never a fail state.
- Typography: a scale (display / title / body / caption / numeric). Numbers appear constantly (calories, grams, weight) — pick a typeface with good tabular figures so animated counters don't jitter.
- Spacing scale, corner radii, elevation.
- Iconography: one consistent style, stroke width, corner treatment. Unambiguous icons can stand alone without labels.
- Component states for everything: default, pressed, disabled, loading, error.

## Motion and "juice"

The app should feel alive and satisfying without ever feeling like a game. Calorie tracking touches body image; juice has to read as crafted, not celebratory. The line: confident and tactile, never cute.

Keep:
- Calorie ring animates with spring physics (slight overshoot, settle) rather than snapping.
- Numbers count up/down smoothly rather than jumping (needs tabular figures).
- Logged item slides into the day's list with a subtle settle.
- Haptic tick on a confirmed log (confirmation actions only — not on every tap).
- Shared-element transitions where natural.
- Pull-to-refresh with a little personality (custom, not a default spinner).
- Weight chart and projection band draw in on first view.
- Subtle idle "breathing" on the calorie ring.

Cut / never:
- No confetti, no particle bursts, no full-screen celebration takeovers.
- No flame/streak animations (the mechanic doesn't exist).
- No mascots or character illustrations with dialogue.
- Nothing over ~400ms on common actions. Motion layers over speed — it must never gate the next action.
- Sound off by default (available as an option).

Deliver a motion spec: for each interaction, the trigger, duration, easing curve, and what moves. "Smooth" is not a spec.

## Screens

Design these. Two flows must be nailed above all: logging a barcoded item in under 10 seconds (app open → logged), and logging an unknown restaurant/takeaway meal without the user abandoning the task.

1. **Today (home).** The log screen IS the home screen. Calorie ring as the primary element (fills toward target; continues past 100% without alarm). A small protein indicator near the ring (protein is a hit-the-target goal, distinct from calories). Today's logged items. A prominent add ("+") affordance, one tap to the scanner. Streak indicator does not exist. Calm.
2. **Add food.** Three zones: Scan (default, camera ready), Recent/Favorites (surfaced before search — this is where most logs come from), Search. A quick-add affordance ("logged something, ~600 kcal") always reachable. The scanner-found-nothing → add-in-seconds path is common, not an edge case; design it as a first-class, fast, non-punishing flow.
3. **Portion picker / food detail.** Pick portion (visual references — liten/medium/stor, knyttneve/håndflate — and grams), pick meal slot (pre-selected by time of day), log. Per-unit foods (one egg, one bar) ask for count, not grams.
4. **Build a meal.** Combine foods+portions into one reusable named meal (e.g. "Frokost – havregrøt") that logs in one tap forever.
5. **My foods.** Manage the personal food database; edit portions; favorite. Show data source/confidence subtly.
6. **Stats (weekly).** The honest screen. Days logged, days within target range, weekly average vs. target, expected vs. actual weight change. Direct numbers, no gamification dressing. This is where adherence lives — never on Today.
7. **Trend.** The motivator. Weight as a 7-day moving average (solid line) with raw daily weigh-ins as small dots; a projection band extending forward (central line + shaded ±band; narrower = more confident); goal weight as a dashed line; projected goal date where they cross. Tap a point for that day's detail; tap the band to see the math. Pinch to zoom. This is the screen the owner will open most after Today — give it the most care.
8. **Macro detail.** Protein/carbs/fat/fiber as rings or bars; grams and % where useful. Protein target emphasized.
9. **Micronutrient overview (v1.1).** Weekly average by default (not a daily checklist — that breeds orthorexia and exhaustion). Grid of nutrients vs. NNR2023 targets. Honest about unknowns: "Vitamin D: 12 µg from 6 of 14 items." Tap a nutrient → contributing foods + trend.
10. **Setup (first run).** Max ~5–6 screens, under 60 seconds: welcome → connect Health Connect (auto-pulls weight/height/age/activity) → (only if HC lacks them) height/DOB/sex → goal weight (current shown) → goal pace (three options, each showing its kcal/day deficit so the trade-off is visible) → land on Today.
11. **TDEE recalibration prompt.** A calm modal every ~2 weeks: shows logged average, expected vs. actual weight change, and the proposed target adjustment, with one-tap confirm. Frame as the system working, not as a verdict.
12. **Settings.** Goals/targets (editable), Health Connect status + per-data-type toggles, notification prefs (granular, all opt-in), haptics/sound, widget config. Short labels.

## States to design (every screen)

Empty, loaded, error, loading. Specifically:
- Empty: no foods logged today; no food history; scanner found nothing; no internet. Illustrated abstractly — no characters, no "no data" text dumps.
- Error: OFF unreachable (scanner still works offline against cache + bundled data — make that graceful, not a dead end); Health Connect denied/unavailable (fall back to manual weight entry / static target without nagging).
- Loading: projection still learning (days 1–13) — show a calm "learning your patterns, projection sharpening" state with a wide band rather than a blank screen.

## Widget (v1)

One widget for v1: medium (4×2). Calorie ring, remaining calories, current meal slot, and a "Log [time-appropriate meal]" button that uses time of day — at 12:30 it reads "Log lunch" and opens straight to recent lunches. The killer feature: a meal logged in about two taps from the home screen without opening the app. Design it for light and dark. (Small 2×2 and large 4×4 are v1.1.)

## Health Connect UI

Connection states: connected / partial (some permissions granted) / disconnected / not available on device. Permission request screens follow Google's Health Connect design guidance. Settings shows per-data-type toggles.

## Deliverables checklist

- Full design system, light + dark (color incl. the "over target" treatment, type with tabular figures, spacing, radii, elevation, icon set).
- All component states.
- Every screen above, with empty/error/loading variants.
- Motion spec: trigger, duration, easing, what moves — per interaction.
- Haptic + sound spec mapped to actions (confirmation-only haptics; sound off by default).
- Medium widget, light + dark.
- The two priority flows storyboarded: 10-second barcode log, and unknown-restaurant-meal log.

# Echo Drafts — Technical Description

## Stack (as actually implemented)
- **Framework**: Native Android, Kotlin + Jetpack Compose (this superseded the originally-planned React Native stack — noting the change here so this doc stays accurate to what's actually being built).
- **Local storage**: Room database (`DraftEntry`, `WeeklyPatternReport` entities, DAO + Repository) — the sole persistence layer. Firebase/Firestore was evaluated, found to conflict with the PRD's non-goals, and has been fully removed.
- **Speech-to-text**: Android `SpeechRecognizer`, with a fallback simulation path.
- **AI categorization**: `GeminiService` (replaces the originally-planned OpenRouter call — functionally equivalent, same prompt contract applies, see below).
- **RevenueCat Android SDK**: integrated at the code level (SDK init, entitlement state, paywall, gating). Dashboard-side setup still required — see Pre-Submission Checklist below.

## Architecture Overview
```
[user speaks]
     |
     v
Android SpeechRecognizer (+ fallback simulation)
     |
     v
Raw transcript (string)
     |
     v
GeminiService API call (categorization prompt)
     |
     v
Structured JSON { facts[], feelings[], nextSteps[] }
     |
     v
Room database (DraftEntry, WeeklyPatternReport)
     |
     v
RevenueCat SDK gates: weekly entry count (free tier), Search + Export + Weekly Report access (premium)
```

No custom backend server is needed for the core loop. This keeps build time low and matches a solo/small-team MVP built in a few days. Firebase/Firestore has been fully removed — the app is entirely local-first via Room.

## Data Model
```
Entry {
  id: string (uuid)
  createdAt: ISO timestamp
  rawTranscript: string
  facts: string[]
  feelings: string[]
  nextSteps: string[]
}
```

## Speech-to-Text
- iOS: native Speech framework (via Expo's speech recognition module, or a native module if not using Expo)
- Android: native SpeechRecognizer API
- **Fallback**: if on-device STT is unavailable or fails, allow manual text entry so the app stays usable and demo-safe.

## AI Categorization
**As implemented**: `GeminiService` calls the Gemini API directly. This replaces the originally-planned OpenRouter integration below; the prompt contract and defensive-parsing guidance still apply as-is to whichever provider is used.

**Original plan (OpenRouter), kept here for reference**:
- Endpoint: `https://openrouter.ai/api/v1/chat/completions`
- Model: pick a fast, low-cost model on OpenRouter — this is a simple classification task, prioritize latency over raw intelligence.
- System prompt (starting point, refine after testing):
  > "You are a classifier. Given a stream-of-consciousness transcript, extract three short lists: Facts (objective events mentioned), Feelings (emotion words or phrases), Next Steps (any decisions or actions mentioned). Respond ONLY with JSON in this exact shape: `{"facts": [...], "feelings": [...], "nextSteps": [...]}`. No preamble, no markdown fences."
- Parse defensively: strip ```json fences if present, wrap in try/catch, and fall back to showing the raw transcript uncategorized if parsing fails — never let a malformed response crash the entry.

## RevenueCat Integration — ✅ Code complete, dashboard setup pending

Code-level integration is done: SDK dependency, `Purchases.configure()` in `EchoApplication.kt`, live `isPremium` state in `EchoViewModel.kt`, `PaywallScreen.kt` matching the app's design tokens, and gating wired across the weekly quota, Search, Export, and Weekly Pattern Report.

### Dashboard setup — still required, nothing works end-to-end without this
1. Create a Project in the RevenueCat dashboard, add the Android app using the **exact** package name from `build.gradle.kts` (`applicationId`) — double-check this matches, since a mismatch silently breaks entitlement checks.
2. Define one Entitlement with ID `premium`.
3. Create a "default" Offering with one subscription product (Test Store is fine for Next Gen; see below if also targeting Design/HAMM).
4. Copy the real **Public Android API Key** from RevenueCat Project Settings into `.env` as `REVENUECAT_API_KEY` (read by `RevenueCatConfig.kt` via `BuildConfig`).

### If targeting Design Award or HAMM (real store listing)
Judges need to unlock premium features without paying:
- Configure a **free trial** on the subscription product in the RevenueCat dashboard, or
- Generate a **promo code** and include it in the Devpost submission form

The app currently has a manual "Demo Unlock" toggle in `PaywallScreen.kt` for offline testing — fine to keep for Next Gen (judged on video + code, no live purchase needed), but **remove or hide it from the build used for Design/HAMM submission**, and rely on the real free trial/promo code instead, so the monetization flow judges see is genuine rather than a manual bypass.

## Current Implementation Status

**Complete**: Compose UI shell (Timeline, VoiceCapture, EntryDetail, WeeklyPatterns, Settings), Room data layer, `SpeechManager` with real + fallback recognition, `GeminiService` categorization, design tokens, seed data, microphone permission flow, weekly quota tracking & enforcement, manual text entry fallback, system back navigation, dynamic weekly usage counter, full-text search, plain-text export, dynamic peak-day detection, RevenueCat SDK integration and entitlement gating (code-level), Firebase fully removed.

All planned build work is now done. What remains is dashboard configuration, end-to-end testing, and submission logistics — see the checklist below.

## Pre-Submission Checklist

### RevenueCat dashboard (blocks everything until done)
- [ ] Project created, Android app registered with the correct package name
- [ ] `premium` Entitlement defined
- [ ] Offering + Test Store product configured
- [x] Real API key swapped into `.env` (read dynamically by `RevenueCatConfig.kt` via `BuildConfig`)
- [ ] If targeting Design/HAMM: free trial or promo code configured, "Demo Unlock" toggle removed from that build

### End-to-end testing (on a real device, not just a preview)
- [ ] Mic permission prompt appears and recording works
- [ ] Speech-to-text transcribes correctly; manual text fallback works if you disable/deny mic access
- [ ] Gemini categorization returns Facts/Feelings/Next Steps correctly
- [ ] 4th weekly entry correctly triggers the paywall
- [ ] Search and Export correctly route to the paywall when not premium, and work correctly once unlocked
- [ ] Weekly Pattern Report gating works
- [ ] Full restart test: force-close and reopen the app, confirm entries persist (Room) and entitlement state is still correct

### Repo & submission (Next Gen requirements)
- [ ] Public GitHub repo
- [ ] OSS license (MIT recommended) visible in the repo's **About** section, not just a LICENSE file buried in the tree
- [ ] README with basic setup steps
- [ ] Demo video recorded, under 2 minutes, uploaded publicly to YouTube or Vimeo
- [ ] Devpost account created with a qualifying student/academic email
- [ ] Devpost submission form: text description, RevenueCat usage description, video link, repo link
- [ ] Submitted before **Sep 30, 2026, 11:45pm PDT** — submit early, don't wait for the last hour

## Repo Requirements (required for Next Gen Award eligibility)
- Public GitHub repository.
- An OSS license file (MIT recommended for simplicity) that is **detectable and visible in the repo's "About" section on GitHub** — this is a stated eligibility requirement, not optional polish.
- A README with basic setup instructions. Judges aren't required to run the code, but this strengthens the "technical care" judging criterion.
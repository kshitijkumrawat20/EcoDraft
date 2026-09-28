# Echo Drafts — Resources & Tools

## RevenueCat (required integration)
- SDK Docs: check the current RevenueCat SDK documentation for your chosen platform (React Native / Flutter) at the start of Day 1 — API surface changes over time, don't rely on memory of an older version.
- "Zero to Ship" guide and Codelabs (linked from the Shipaton hackathon resources page) — walks through integration step by step.
- AI Toolkits ("skills") for the RevenueCat SDK — usable with AI coding agents like Antigravity to speed up correct integration.

## OpenRouter (for the categorization call)
- Unlocked via Shipaton's Ship Kit at registration: **$10 in free credits**.
- Single unified API to call various models — use it for the Facts/Feelings/Next Steps categorization call described in `technical-description.md`.

## Ship Kit perks worth using given your timeline
These unlock automatically as you hit Shipaton milestones (registration → RevenueCat project created → first test purchase → etc.). Relevant ones for a fast solo build:
- **JetBrains Junie** — free AI coding agent access for 2 months, works directly in the IDE.
- **Emergent** — 200 free credits; AI app builder with a built-in RevenueCat integration, useful if you want to accelerate scaffolding.
- **Argent** — free access; an agentic toolkit that lets an AI coding assistant (Cursor, Claude, Copilot, etc.) run your app and spot/fix bugs directly — useful for Antigravity-driven development.
- **Bitrig** — 60% off; relevant only if you switch to native Swift instead of React Native.
- **Replit** — 40% off Pro, fully integrated with RevenueCat; relevant if you'd rather build directly in Replit instead of locally.

## Speech-to-Text
- iOS: Apple's native Speech framework (`SFSpeechRecognizer`)
- Android: native `SpeechRecognizer` API
- If using Expo, check for a maintained speech-recognition module rather than writing native modules from scratch, to save time.

## Submission Requirements Checklist (Next Gen Award)
- [ ] Devpost account created with a qualifying student/academic email address
- [x] Public GitHub repo structure & source code ready
- [x] RevenueCat SDK integrated and entitlement gating wired
- [ ] OSS license file (MIT recommended) visible in the repo's "About" section
- [ ] Demo video, under 2 minutes, uploaded publicly to YouTube or Vimeo
- [x] Text description of features and functionality (`technical-description.md`, `PRD.md`)
- [x] Description of how RevenueCat was used for monetization (`PaywallScreen.kt`, 4th entry limit, Search, Export, Weekly Reports)
- [ ] If under the age of majority in your country: parent/guardian consent — form at `https://forms.gle/Gx2Cr4X8WPk9V1q77`, complete this *before* the submission deadline, not after

## Reminder
Submission Period ends **Sep 30, 2026, 11:45pm PDT**. Submit early — don't wait until the final hour in case of upload issues.

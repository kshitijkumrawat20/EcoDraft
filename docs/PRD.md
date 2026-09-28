# Echo Drafts — Product Requirements Document (PRD)

## 1. Problem
People with racing or messy thoughts (stress, decision fatigue, overthinking) either vent to a friend or just keep looping the same thoughts in their head. Text journaling apps exist but require the friction of typing when someone is *least* in the mood to type. There's no fast, structured way to just talk it out and get clarity back.

## 2. Target User
Students and young adults managing everyday mental load — exam stress, social conflicts, indecision — who already "think out loud" but have nowhere structured to put it.

## 3. Solution Summary
A voice-first capture app: speak a messy thought, on-device speech-to-text transcribes it, and a lightweight AI call reorganizes it into **Facts / Feelings / Next Steps**. Over time, a Weekly Pattern Report (premium) surfaces recurring themes.

## 4. Hackathon Category Targets

| Category | Fit | Notes |
|---|---|---|
| **Next Gen Award** | ✅ Primary target | No store publish needed, video + open-source repo, matches judging criteria directly |
| RevenueCat Design Award | 🟡 Secondary, if polish is high | Voice-first UX is genuinely unusual — must execute cleanly |
| HAMM Award | 🟡 Secondary | Paywall is feature-gated, not generic "remove ads" |
| Peace Prize | ❌ Do not target | Too personal-scale, not a community/society impact story |
| Productivity Influencer Award | ❌ Do not target | That award is about snippet/file retrieval, not journaling |
| Catvertising, Best Game, Kotlin Everywhere, Most Viral, Galaxy, Replit, OneSignal, Layers, Funnel Vision | ❌ Do not target | Each requires a specific sponsor tool integration we're not using — don't build toward these |

## 5. Core User Journey
1. Open app → tap record → speak thought (30 sec – 3 min)
2. On-device speech-to-text transcribes in near real time
3. On stop, transcript is sent for categorization → Facts / Feelings / Next Steps
4. Result shown as a structured entry card
5. Entry saved to local timeline
6. (Premium) Weekly Pattern Report surfaces recurring themes across entries

## 6. Feature List

### Free tier
- Record a voice entry (limit: 3 per week)
- On-device transcription
- AI categorization into Facts / Feelings / Next Steps
- Chronological entry timeline
- Entry detail view
- Delete an entry
- Manual text entry fallback (if speech recognition unavailable/fails)

### Premium tier (behind RevenueCat paywall)
- Unlimited voice entries
- Weekly Pattern Report — recurring themes/emotions across the week
- Search past entries
- Export entries as plain text

## 7. Differentiation — why not just use ChatGPT
- **Zero-friction capture**: one tap and speak, vs. opening a chat app and typing a prompt. Friction matters most exactly when someone is stressed.
- **Persistent, structured history**: every entry is stored in a real schema, so trend queries ("what have I been stressed about this month") are a database query, not a one-off chat.
- **Habit-loop design**: the app can prompt you back (reminders, weekly report). A chatbot is passive and waits for you to return.

The pitch to judges: this is not "smarter AI than ChatGPT" — it's a purpose-built capture tool with a UX ChatGPT structurally can't offer.

## 8. Non-Goals (explicitly out of scope — do NOT build these)
- No social/sharing features
- No multi-user or collaboration features
- No calendar/scheduling integration
- No ads (not competing for Catvertising)
- No gamification — no streaks, no badges, no fire emojis (conflicts with the calm brand tone, and isn't needed for any target category)
- No cloud sync or account system — local storage only for v1 (keep scope small given the build timeline)
- No integration with Kotlin Multiplatform, Layers, OneSignal, Stripe, Noise, or Samsung Galaxy Store — not targeting those categories

## 9. Success Criteria for Submission
- A clean, working end-to-end flow demoable in under 2 minutes of video
- Public GitHub repo with a visible OSS license (MIT recommended)
- RevenueCat entitlement gating visibly functioning in the demo (Test Store is sufficient)

## 10. Risks & Mitigations
- **Speech-to-text accuracy**: test on a real device on Day 1, not Day 3. Always offer manual text-edit of the transcript as a fallback.
- **API latency during the live demo video**: pre-record a clean take; don't rely on a live unscripted demo. Have a manual fallback path if the categorization call fails.
- **Tight timeline (~3 days)**: cut the Weekly Pattern Report first if behind schedule — it's a premium nice-to-have, not the core loop. Core loop (record → transcribe → categorize → save) must work end-to-end before anything else.

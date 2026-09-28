# Echo Drafts — UI Design Spec (for Stitch generation)

## Design Brief Summary
Subject: a private, voice-first thought-capture app for people with racing/messy thoughts.
Audience: students and young adults under everyday stress.
Primary job of the design: feel calm, private, and immediate — like thinking out loud at night, not like a productivity dashboard.

**Avoid default AI-generated design tells**: no cream-background-with-terracotta-accent, no near-black-with-neon-accent, no identical rounded SaaS cards with soft grey shadows, no tracked-out ALL-CAPS eyebrow labels, no middle-dot meta strings, no arrow (→) on every button. This app should not look like a generic dashboard or SaaS landing page.

---

## Design Tokens

### Color palette
| Name | Hex | Use |
|---|---|---|
| Ink | `#14171F` | Primary background — deep ink navy, not pure black |
| Surface | `#1E2230` | Cards, entry rows, elevated panels |
| Paper | `#EDEAE3` | Primary text — warm off-white, not stark white |
| Ember | `#E8B34A` | Single accent color — used only for the record button and premium highlights. Spend this color sparingly. |
| Sage | `#7C9885` | Tag color for "Facts" |
| Dusty Rose | `#C48B9F` | Tag color for "Feelings" |
| Soft Blue | `#6E8FA3` | Tag color for "Next Steps" |

### Typography
- **Headings & entry text**: a warm humanist serif with personality (e.g. Fraunces or similar) — gives entries a personal, page-of-a-notebook feel rather than a UI-chrome feel.
- **UI labels, buttons, nav**: a clean grotesk sans (e.g. Inter or Public Sans) — kept purely functional, secondary to the serif.
- Line length under 80 characters for entry text. No all-caps labels anywhere.

### Layout concept
Single column, generous vertical whitespace, left-aligned text. Entries read like pages in a notebook — a simple list with thin hairline dividers between them, not a grid of shadowed cards.

### Motion
One deliberate animated moment: a gentle waveform/pulse on the Record screen while listening. No hover transitions or fade-slide-ins scattered across every element.

---

## Screens

### 1. Timeline (Home)
**Purpose**: See past entries at a glance, start a new one.
**Elements**:
- Simple header: "Echo Drafts" wordmark in the serif face, no eyebrow label above it
- Vertical list of entries, most recent first, each row showing: date/time, first line of transcript (truncated), small colored dots indicating which categories (facts/feelings/next steps) were present
- Floating record button (Ember color) — the one bold visual element on this screen
- Weekly Pattern Report entry point (locked/premium indicator if not subscribed)

**States**:
- Empty state (no entries yet): see section 7 below
- Populated state: list as described
- Free-limit-reached indicator: subtle text near the record button, e.g. "3 of 3 this week" — not a blocking modal until they actually tap record

### 2. Record
**Purpose**: Capture a voice entry with minimal friction.
**Elements**:
- Full-bleed, centered layout
- Large mic button, Ember color
- Animated waveform while listening (the one deliberate motion moment)
- Elapsed time counter
- "Stop" action (tap the mic again, or a clear secondary control)
- Cancel/back action

**States**:
- Idle (ready to record)
- Listening (waveform active)
- Processing (see screen 3)
- Permission-denied (mic access not granted) — plain-language explanation and a way to enable it, not a generic OS error string

### 3. Processing
**Purpose**: Bridge state between stopping a recording and seeing the categorized result.
**Elements**:
- Calm loading indicator (not a spinner that feels clinical — consider a slow pulse matching the Ember accent)
- Short, plain-language status text, e.g. "Sorting that out..."

**States**:
- Normal processing (a few seconds)
- Error/timeout: falls back to showing the raw transcript uncategorized, with a note like "Couldn't sort this one — here's what you said" rather than a technical error

### 4. Entry Detail
**Purpose**: Show a single entry's structured breakdown.
**Elements**:
- Date/time header
- Three labeled sections: Facts (Sage), Feelings (Dusty Rose), Next Steps (Soft Blue) — each a short tag-style list, not paragraphs
- Raw transcript available but de-emphasized (collapsed or below the fold) — the structured summary is the hero, not the wall of text
- Delete action

**States**:
- Full entry (all three categories populated)
- Partial entry (e.g. no next steps mentioned — section simply omitted, not shown as empty/greyed placeholder)

### 5. Weekly Pattern Report (Premium)
**Purpose**: Surface recurring themes across the week's entries.
**Elements**:
- Simple text/list summary: most frequent feelings and facts mentioned this week (word-frequency based — no chart complexity needed for v1)
- Date range header (e.g. "This week")

**States**:
- Enough data to show a report
- Not enough entries yet this week — plain-language empty state, not an error

### 6. Paywall
**Purpose**: Convert a free user hitting a limit into premium.
**Elements**:
- Triggered contextually (from Timeline limit or Weekly Report lock) — never shown unprompted at app launch
- Uses RevenueCat's prebuilt paywall component where possible
- Plain-language framing of what premium unlocks: unlimited entries, weekly pattern report, search, export — not generic "Go Pro" language
- Clear close/dismiss action

### 7. Empty State (no entries yet)
**Purpose**: First-run invitation to act.
**Elements**:
- Short, second-person copy in the interface's voice, e.g. "Nothing here yet. Say what's on your mind." — not a generic "No data" message
- Record button prominent

### 8. Settings (minimal)
**Purpose**: Basic controls only — do not over-build this screen.
**Elements**:
- Toggle: reminder notifications on/off
- Manage subscription (opens RevenueCat's customer center / platform subscription management)
- App version / about

---

## Copy Guidelines (apply throughout)
- Active voice. A button that says "Record" does the recording; it doesn't say "Submit" or "Start Process."
- No filler, no apologies in error states — explain what happened and what to do next, in plain language.
- No streaks, badges, or gamified congratulatory copy — keep the tone calm and quiet throughout.

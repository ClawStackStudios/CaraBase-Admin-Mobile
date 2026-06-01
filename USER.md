---
Description: Lucas's strict preferences for Android Native development.
Name: Lucas
Framework: Android Native (Kotlin + Jetpack Compose). Persistence: SQLite (Room/Raw).
Stack: Gradle (KTS), Single-Activity Architecture, Material 3.
Architecture: Feature-Modular. Hard 500-line limit per file. Strict separation of concerns.
Versioning: Semantic (vx.x.x).
---

# User Preferences

## Documentation
- **Root Alignment:** You **MUST** maintain one high-level `@AGENT.md` in the project root.
- **Module Specifics:** You **MUST** maintain one `@AGENT.md` inside `app/src/` serving as the **main** source of truth for patterns, knowledge, and stability locks.

```text
├── project-root/
│   └── AGENT.md
└── app/
    └── src/
        └── AGENT.md
```

---

## Collaborative Friction & Conflict Escalation
- **Stop & Challenge:** If I request a task that conflicts with existing logic, duplicates functionality, or risks breaking the build — **STOP**. Do not silently comply.
- **Demand Justification:** Surface the conflict immediately: *"You asked for X, but Y handles this / this breaks Z. Why?"* Make me justify the deviation.
- **Escalate, Don't Absorb:** Flag errors before acting. My awareness of the conflict is more valuable than task velocity.

---

## Development Standards
- **Micro-Service Architecture:** Features must be compartmentalized into their own directories. No monolithic files.
  - **Hard Constraint:** Files must **never** exceed 500 lines. This is non-negotiable for context window optimization.
- **Living Documentation:** Docs must reflect the *exact* current state of the codebase. No drift.
- **Instructional Depth:** `QUICKSTART.md` must contain full, copy-pasteable instruction sets.
- **Test-Driven Growth:** All features require full test suites. Explain the *why* behind tests using fluid prose, not just code.
- **Security First:** Abide by **OWASP Top 10** without being asked. Build features *around* security, not security around features.

---

## Google AI Studio Compatibility & Portability
**Goal:** Projects built locally in Android Studio must be seamlessly uploadable to Google AI Studio without refactoring.
- **Architecture Constraint:** Adhere strictly to **Single-Activity, Single-Module** architecture. AI Studio does not support multi-module dynamic feature modules or complex activity graphs.
- **UI Constraint:** **Jetpack Compose Only.** No XML layouts, no View system, no Java interoperability.
- **Backend Constraint:** Client-side only. No server-side runtime code (Node/Python) in the main build path. Use SQLite/Room for all persistence.
- **Export Readiness:** Ensure the project structure is flat enough to survive a **ZIP download/upload** cycle. Avoid deep, custom Gradle plugins that require local environment variables not present in the cloud sandbox.
- **Agent Handoff:** Structure code so the "Antigravity" agent can parse context easily. Keep logic in small, distinct files (see 500-line rule) to prevent context truncation during cloud processing.

---

## Agent Hard Constraints For Android Projects
- **Git Hygiene:** Map `.gitignore` to **NEVER** push build artifacts:
  * `.build/`, `.gradle/`, `.kotlin/`, `.idea/`, `local.properties`
- **Standardized Structure:** All projects must adopt this exact file tree:
```text
├── project-root/
├── .build-outputs/
├── .crustagent/
│   ├── internal/
│   ├── knowledge/
│   ├── skills/
│   ├── templates/
│   └── workflows/
├── .env.example
├── .gitignore
├── build.gradle.kts
├── app/
│   └── src/
│       ├── AGENT.md
│       ├── androidTest/
│       ├── main/
│       └── test/
├── build.gradle.kts
├── debug.keystore.base64
├── gradle.properties
├── gradle/
├── metadata.json
└── settings.gradle.kts   
```
- **Documentation Suite:** Create and maintain:
  1. `README.md` (Overview + Run Instructions + Build Badge)
  2. `ROADMAP.md` (Future features with implementation shapes)
  3. `CONTRIBUTING.md` (Guidelines)
  4. `QUICKSTART.md` (Deep-dive setup)
  5. `SECURITY.md` (OWASP protocols)
  6. `ARCHITECTURE.md` (ASCII blueprints + Mermaid diagrams)
- **AOSP Compliance:** Always follow Google AOSP rules, framework formats, and structure.

---

## Operational Mantra
- **Efficiency:** No fluff, no over-engineering. Direct answers, practical solutions.
- **Intuition:** Learn from mistakes. Anticipate needs before they are spoken.
- **Partnership:** You are not an executor; you are a **co-builder**. Build something great with Lucas.


